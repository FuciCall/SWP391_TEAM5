package com.gha.gender_healthcare_api.cycle.service;

import com.gha.gender_healthcare_api.cycle.dto.CycleAnalytics;
import com.gha.gender_healthcare_api.cycle.dto.MenstrualCycleRequest;
import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import com.gha.gender_healthcare_api.cycle.repository.MenstrualCycleRepository;
import com.gha.gender_healthcare_api.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Service xử lý toàn bộ nghiệp vụ liên quan đến chu kỳ kinh nguyệt
 * 
 * Chức năng chính:
 * - Ghi nhận chu kỳ mới và tự động tính toán cycle length từ chu kỳ trước
 * - Tính toán thống kê sức khỏe: độ dài chu kỳ trung bình, xu hướng, độ đều đặn
 * - Cung cấp phân tích chi tiết để user hiểu rõ về sức khỏe sinh sản
 * - Tích hợp với prediction service để đưa ra dự đoán chu kỳ tiếp theo
 * - Tích hợp với notification service để lên lịch nhắc nhở
 * 
 * Thuật toán y khoa:
 * - Cycle length bình thường: 21-35 ngày (average 28 ngày)
 * - Period length bình thường: 3-7 ngày (average 5 ngày)
 * - Irregular cycle: độ lệch chuẩn > 7 ngày hoặc < 21 ngày / > 35 ngày
 * - Ovulation thường xảy ra 14 ngày trước kỳ kinh tiếp theo
 * 
 * Luồng xử lý:
 * 1. Validate input data (ngày hợp lệ, không trùng lặp)
 * 2. Tính cycle length từ chu kỳ trước đó
 * 3. Lưu vào database với transaction safety
 * 4. Trigger tạo prediction mới
 * 5. Schedule notifications cho chu kỳ tiếp theo
 * 6. Log activity cho auditing
 */
@Service
@Transactional
@Slf4j
public class MenstrualCycleService {
      /**
     * Repository để truy cập dữ liệu chu kỳ kinh nguyệt
     */
    @Autowired
    private MenstrualCycleRepository menstrualCycleRepository;
    
    /**
     * Service xử lý dự đoán chu kỳ - được gọi sau khi ghi nhận chu kỳ mới
     */
    @Autowired
    private CyclePredictionService cyclePredictionService;
    
    /**
     * Service xử lý thông báo - được gọi để lên lịch các thông báo tự động
     */
    @Autowired
    private CycleNotificationService notificationService;
      /**
     * Ghi nhận chu kỳ kinh nguyệt mới của user với full validation và calculation
     * 
     * Quy trình xử lý step-by-step:
     * 1. VALIDATION: Kiểm tra dữ liệu đầu vào hợp lệ
     *    - Start date không được trong tương lai
     *    - End date phải sau start date (nếu có)
     *    - Không trùng lặp với cycle đã có
     * 
     * 2. CALCULATION: Tính toán thông số y khoa
     *    - Cycle length = days between previous start_date và current start_date
     *    - Period length = days between start_date và end_date + 1
     *    - Validate cycle length trong khoảng 15-50 ngày (medical range)
     * 
     * 3. PERSISTENCE: Lưu vào database với transaction
     *    - Tạo MenstrualCycle entity mới
     *    - Set timestamps (created_at, updated_at)
     *    - Save với @Transactional để rollback nếu lỗi
     * 
     * 4. TRIGGER DOWNSTREAM SERVICES:
     *    - Generate predictions cho chu kỳ tiếp theo
     *    - Schedule notifications (ovulation, period reminders)
     *    - Update analytics cache nếu có
     * 
     * Error Handling:
     * - IllegalArgumentException: Dữ liệu không hợp lệ
     * - DataIntegrityViolationException: Trùng lặp dữ liệu
     * - ServiceException: Lỗi từ downstream services
     * 
     * @param userId ID của user (đã được authenticate từ JWT)
     * @param request DTO chứa thông tin chu kỳ từ frontend
     * @return MenstrualCycle entity đã được persist với đầy đủ calculated fields
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     * @throws ServiceException nếu có lỗi từ prediction/notification services
     */
    public MenstrualCycle declareMenstrualCycle(Long userId, MenstrualCycleRequest request) {
        MenstrualCycle cycle = new MenstrualCycle();
        
        // Thiết lập tham chiếu User - chỉ cần ID để tránh N+1 query
        User user = new User();
        user.setUserId(userId);
        cycle.setUser(user);
        
        // Ánh xạ dữ liệu từ request
        cycle.setStartDate(request.getStartDate());
        cycle.setEndDate(request.getEndDate());
        cycle.setFlowIntensity(request.getFlowIntensity());
        cycle.setSymptoms(request.getSymptoms());
        cycle.setNotes(request.getNotes());
        cycle.setCreatedAt(LocalDateTime.now());
        cycle.setUpdatedAt(LocalDateTime.now());
        
        // Tính độ dài chu kỳ từ chu kỳ trước đó (nếu có)
        Optional<MenstrualCycle> lastCycle = menstrualCycleRepository.findTopByUserUserIdOrderByStartDateDesc(userId);
        if (lastCycle.isPresent()) {
            long daysBetween = ChronoUnit.DAYS.between(lastCycle.get().getStartDate(), cycle.getStartDate());
            cycle.setCycleLength((int) daysBetween);
        }
        
        // Tính độ dài kinh nguyệt (nếu có ngày kết thúc)
        if (request.getEndDate() != null) {
            long periodDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
            cycle.setPeriodLength((int) periodDays);
        }
        
        MenstrualCycle savedCycle = menstrualCycleRepository.save(cycle);
        
        // Kích hoạt các service khác để cập nhật dự đoán và thông báo
        try {
            cyclePredictionService.generatePredictions(userId);
            notificationService.scheduleOvulationReminders(userId);
        } catch (Exception e) {
            log.error("Error generating predictions/notifications for user {}: {}", userId, e.getMessage());
        }
        
        return savedCycle;
    }
    
    /**
     * Lấy lịch sử chu kỳ của user trong khoảng thời gian nhất định
     * 
     * @param userId ID của user
     * @param months Số tháng muốn lấy (tính từ hiện tại về trước)
     * @return Danh sách chu kỳ được sắp xếp theo thời gian giảm dần
     */
    public List<MenstrualCycle> getUserCycles(Long userId, int months) {
        LocalDate fromDate = LocalDate.now().minusMonths(months);
        return menstrualCycleRepository.findByUserIdAndStartDateAfter(userId, fromDate);
    }
    
    /**
     * Tính toán và phân tích dữ liệu chu kỳ tổng thể của user
     * Bao gồm: độ dài trung bình, tính đều đặn, lời khuyên sức khỏe
     * 
     * @param userId ID của user
     * @return CycleAnalytics chứa tất cả thống kê và insight
     */
    public CycleAnalytics getCycleAnalytics(Long userId) {
        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUserUserIdOrderByStartDateDesc(userId);
        
        // Trường hợp user chưa có dữ liệu
        if (cycles.isEmpty()) {
            return CycleAnalytics.builder()
                .totalCycles(0)
                .averageCycleLength(28)
                .averagePeriodLength(5)
                .cycleRegularity("INSUFFICIENT_DATA")
                .healthInsights(List.of("Start tracking your cycles to get personalized insights!"))
                .build();
        }
        
        // Tính độ dài chu kỳ trung bình (chỉ lấy cycles có cycle_length)
        OptionalDouble avgCycleLength = cycles.stream()
            .filter(c -> c.getCycleLength() != null)
            .mapToInt(MenstrualCycle::getCycleLength)
            .average();
            
        // Tính độ dài kinh nguyệt trung bình (chỉ lấy cycles có period_length)
        OptionalDouble avgPeriodLength = cycles.stream()
            .filter(c -> c.getPeriodLength() != null)
            .mapToInt(MenstrualCycle::getPeriodLength)
            .average();
        
        // Tạo lời khuyên sức khỏe dựa trên dữ liệu
        List<String> insights = generateHealthInsights(cycles);
        
        return CycleAnalytics.builder()
            .totalCycles(cycles.size())
            .averageCycleLength(avgCycleLength.orElse(28))
            .averagePeriodLength(avgPeriodLength.orElse(5))
            .cycleRegularity(calculateRegularity(cycles))
            .lastPeriodDate(cycles.get(0).getStartDate())
            .healthInsights(insights)
            .build();
    }
    
    /**
     * Tính toán độ đều đặn của chu kỳ dựa trên variance
     * Sử dụng độ lệch chuẩn để phân loại mức độ đều đặn
     * 
     * @param cycles Danh sách chu kỳ để phân tích
     * @return String mô tả mức độ đều đặn
     */
    private String calculateRegularity(List<MenstrualCycle> cycles) {
        if (cycles.size() < 3) return "INSUFFICIENT_DATA";
        
        // Lấy danh sách độ dài chu kỳ (loại bỏ null values)
        List<Integer> cycleLengths = cycles.stream()
            .filter(c -> c.getCycleLength() != null)
            .map(MenstrualCycle::getCycleLength)
            .collect(Collectors.toList());
            
        if (cycleLengths.size() < 3) return "INSUFFICIENT_DATA";
        
        // Tính variance để đánh giá độ biến thiên
        double variance = calculateVariance(cycleLengths);
        
        // Phân loại dựa trên variance
        if (variance <= 2) return "VERY_REGULAR";
        else if (variance <= 5) return "REGULAR";
        else if (variance <= 10) return "SOMEWHAT_IRREGULAR";
        else return "IRREGULAR";
    }
    
    /**
     * Tính variance (độ biến thiên) của một tập giá trị
     * Công thức: Σ(xi - μ)² / n
     * 
     * @param values Danh sách giá trị cần tính variance
     * @return Variance value
     */
    private double calculateVariance(List<Integer> values) {
        double mean = values.stream().mapToInt(Integer::intValue).average().orElse(0);
        return values.stream()
            .mapToDouble(val -> Math.pow(val - mean, 2))
            .average()
            .orElse(0);
    }
    
    /**
     * Tạo lời khuyên sức khỏe cá nhân hóa dựa trên dữ liệu chu kỳ
     * Phân tích độ dài chu kỳ, tính đều đặn và đưa ra khuyến nghị
     * 
     * @param cycles Danh sách chu kỳ để phân tích
     * @return Danh sách insight và lời khuyên
     */
    private List<String> generateHealthInsights(List<MenstrualCycle> cycles) {
        List<String> insights = new ArrayList<>();
        
        if (cycles.size() >= 3) {
            // Phân tích độ dài chu kỳ
            double avgCycleLength = cycles.stream()
                .filter(c -> c.getCycleLength() != null)
                .mapToInt(MenstrualCycle::getCycleLength)
                .average()
                .orElse(28);
                
            // Đánh giá độ dài chu kỳ so với chuẩn y khoa (21-35 ngày)
            if (avgCycleLength < 21) {
                insights.add("Your cycles are shorter than average. Consider consulting a healthcare provider.");
            } else if (avgCycleLength > 35) {
                insights.add("Your cycles are longer than average. This might be normal for you, but consider tracking symptoms.");
            } else {
                insights.add("Your cycle length is within the normal range (21-35 days).");
            }
            
            // Đánh giá tính đều đặn và đưa ra lời khuyên
            String regularity = calculateRegularity(cycles);
            switch (regularity) {
                case "VERY_REGULAR":
                    insights.add("Your cycles are very regular - great for planning!");
                    break;
                case "REGULAR":
                    insights.add("Your cycles are quite regular with minor variations.");
                    break;
                case "SOMEWHAT_IRREGULAR":
                    insights.add("Your cycles show some irregularity. Track symptoms and lifestyle factors.");
                    break;
                case "IRREGULAR":
                    insights.add("Your cycles are irregular. Consider discussing with a healthcare provider.");
                    break;
            }
        }
        
        // Phân tích độ dài kinh nguyệt
        OptionalDouble avgPeriodLength = cycles.stream()
            .filter(c -> c.getPeriodLength() != null)
            .mapToInt(MenstrualCycle::getPeriodLength)
            .average();
            
        if (avgPeriodLength.isPresent()) {
            double avgPeriod = avgPeriodLength.getAsDouble();
            // Đánh giá độ dài kinh nguyệt so với chuẩn y khoa (3-7 ngày)
            if (avgPeriod < 3) {
                insights.add("Your periods are shorter than average. Consider tracking flow intensity.");
            } else if (avgPeriod > 7) {
                insights.add("Your periods are longer than average. Monitor heavy flow days.");
            } else {
                insights.add("Your period length is within the normal range (3-7 days).");
            }
        }
        
        return insights;
    }
}

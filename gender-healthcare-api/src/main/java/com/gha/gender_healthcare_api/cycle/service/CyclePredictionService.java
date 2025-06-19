package com.gha.gender_healthcare_api.cycle.service;

import com.gha.gender_healthcare_api.cycle.entity.CyclePrediction;
import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import com.gha.gender_healthcare_api.cycle.repository.CyclePredictionRepository;
import com.gha.gender_healthcare_api.cycle.repository.MenstrualCycleRepository;
import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Service chuyên xử lý dự đoán chu kỳ kinh nguyệt và sinh sản
 * Sử dụng thuật toán y khoa để tính toán:
 * - Ngày rụng trứng (ovulation): Thường là 14 ngày trước chu kỳ tiếp theo
 * - Cửa sổ sinh sản (fertile window): 6 ngày (5 ngày trước + 1 ngày sau rụng trứng)
 * - Khả năng có thai: Dựa trên vị trí hiện tại trong cửa sổ sinh sản
 */
@Service
@Slf4j
public class CyclePredictionService {
      /**
     * Repository để truy cập dữ liệu chu kỳ kinh nguyệt
     */
    @Autowired
    private MenstrualCycleRepository menstrualCycleRepository;
    
    /**
     * Repository để lưu và truy cập dự đoán chu kỳ
     */
    @Autowired
    private CyclePredictionRepository cyclePredictionRepository;
    
    /**
     * Tạo dự đoán chu kỳ mới dựa trên lịch sử kinh nguyệt của user
     * Thuật toán sử dụng:
     * 1. Lấy 6 chu kỳ gần nhất để tính độ dài trung bình (tăng độ chính xác)
     * 2. Dự đoán chu kỳ tiếp theo = chu kỳ cuối + độ dài trung bình
     * 3. Rụng trứng = chu kỳ tiếp theo - 14 ngày (luteal phase cố định)
     * 4. Cửa sổ sinh sản = rụng trứng ± (5,1) ngày
     * 5. Khả năng có thai = tính theo vị trí hiện tại trong cửa sổ
     * 
     * @param userId ID của user cần tạo dự đoán
     * @return CyclePrediction entity đã được lưu
     * @throws IllegalStateException nếu user chưa có dữ liệu chu kỳ
     */
    public CyclePrediction generatePredictions(Long userId) {
        // Lấy lịch sử chu kỳ của user (sắp xếp theo thời gian giảm dần)
        List<MenstrualCycle> recentCycles = menstrualCycleRepository.findByUserUserIdOrderByStartDateDesc(userId);
          if (recentCycles.isEmpty()) {
            throw new IllegalStateException("No cycle data available for predictions");
        }
        
        // Tính độ dài chu kỳ trung bình từ 6 chu kỳ gần nhất (hoặc ít hơn nếu không đủ)
        // Chỉ sử dụng cycles có cycle_length (được tính từ chu kỳ trước đó)
        OptionalDouble avgCycleLength = recentCycles.stream()
            .filter(c -> c.getCycleLength() != null)
            .limit(6) // Sử dụng 6 chu kỳ gần nhất để đảm bảo độ chính xác
            .mapToInt(MenstrualCycle::getCycleLength)
            .average();
            
        // Sử dụng độ dài trung bình hoặc 28 ngày (chuẩn y khoa) nếu chưa có đủ dữ liệu
        int cycleLengthToUse = (int) avgCycleLength.orElse(28);
        
        // Ngày bắt đầu chu kỳ cuối cùng
        LocalDate lastPeriodStart = recentCycles.get(0).getStartDate();
        
        // Dự đoán ngày kinh nguyệt tiếp theo
        LocalDate nextPeriodDate = lastPeriodStart.plusDays(cycleLengthToUse);
        
        // Dự đoán ngày rụng trứng (14 ngày trước chu kỳ tiếp theo - luteal phase)
        LocalDate ovulationDate = nextPeriodDate.minusDays(14);
        
        // Tính cửa sổ sinh sản (fertile window)
        // Tinh trùng có thể sống 5 ngày, trứng sống 24h sau rụng trứng
        LocalDate fertileStart = ovulationDate.minusDays(5);
        LocalDate fertileEnd = ovulationDate.plusDays(1);
        
        // Tính khả năng có thai dựa trên ngày hiện tại
        double pregnancyLikelihood = calculatePregnancyLikelihood(fertileStart, fertileEnd);
        
        // Tạo entity prediction mới
        CyclePrediction prediction = new CyclePrediction();
        
        // Thiết lập tham chiếu User (chỉ cần ID)
        User user = new User();
        user.setUserId(userId);
        prediction.setUser(user);
        
        // Thiết lập các giá trị dự đoán
        prediction.setPredictedOvulationDate(ovulationDate);
        prediction.setFertileWindowStart(fertileStart);
        prediction.setFertileWindowEnd(fertileEnd);
        prediction.setNextPeriodDate(nextPeriodDate);
        prediction.setPregnancyLikelihood(pregnancyLikelihood);
        prediction.setCalculationDate(LocalDateTime.now());
        
        return cyclePredictionRepository.save(prediction);
    }
    
    /**
     * Tính khả năng có thai dựa trên vị trí hiện tại trong cửa sổ sinh sản
     * Thuật toán dựa trên nghiên cứu y khoa về khả năng thụ thai theo ngày
     * 
     * Phân bố khả năng có thai:
     * - Ngoài cửa sổ sinh sản: 0%
     * - Ngày đầu/cuối cửa sổ: 10% (thấp nhất)
     * - Ngày trước rụng trứng: 25% (cao nhất - tinh trùng chờ sẵn)
     * - Ngày rụng trứng: 25% (cao nhất)
     * - Ngày giữa cửa sổ: 20% (trung bình)
     * 
     * @param fertileStart Ngày bắt đầu cửa sổ sinh sản
     * @param fertileEnd Ngày kết thúc cửa sổ sinh sản
     * @return Khả năng có thai tính theo phần trăm (0-25%)
     */
    private double calculatePregnancyLikelihood(LocalDate fertileStart, LocalDate fertileEnd) {
        LocalDate today = LocalDate.now();
        
        // Ngoài cửa sổ sinh sản = không có khả năng thụ thai
        if (today.isBefore(fertileStart) || today.isAfter(fertileEnd)) {
            return 0.0; // Ngoài cửa sổ sinh sản
        }
        
        // Tính ngày rụng trứng và ngày trước đó (peak fertility)
        LocalDate ovulationDay = fertileEnd.minusDays(1);
        LocalDate dayBeforeOvulation = ovulationDay.minusDays(1);
        
        // Khả năng cao nhất: ngày rụng trứng và ngày trước đó
        if (today.equals(ovulationDay) || today.equals(dayBeforeOvulation)) {
            return 25.0; // Đỉnh cao khả năng thụ thai
        } 
        // Khả năng thấp nhất: ngày đầu và cuối cửa sổ
        else if (today.equals(fertileStart) || today.equals(fertileEnd)) {
            return 10.0; // Khả năng thụ thai thấp hơn ở các ngày biên
        } 
        // Khả năng trung bình: các ngày ở giữa
        else {
            return 20.0; // Khả năng thụ thai trung bình ở giữa cửa sổ
        }
    }
      /**
     * Lấy dự đoán chu kỳ mới nhất của user
     * Dự đoán được sắp xếp theo thời gian tính toán giảm dần (mới nhất trước)
     * 
     * @param userId ID của user
     * @return CyclePrediction mới nhất của user
     * @throws EntityNotFoundException nếu user chưa có dự đoán nào
     * 
     * Chức năng:
     * - Tìm prediction với calculation_date gần nhất
     * - Thường được gọi để hiển thị thông tin dự đoán hiện tại cho user
     * - Ném exception nếu user chưa bao giờ có prediction (chưa khai báo chu kỳ)
     */
    public CyclePrediction getCurrentPrediction(Long userId) {
        log.debug("Getting current prediction for user {}", userId);
          return cyclePredictionRepository.findTopByUserUserIdOrderByCalculationDateDesc(userId)
            .orElseThrow(() -> {
                log.error("No predictions found for user {}", userId);
                return new EntityNotFoundException("No predictions found for user: " + userId + 
                    ". Please record at least one menstrual cycle to generate predictions.");
            });
    }
}

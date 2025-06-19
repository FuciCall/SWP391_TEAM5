package com.gha.gender_healthcare_api.cycle.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO (Data Transfer Object) cho phân tích chu kỳ kinh nguyệt tổng hợp
 * 
 * Class này chứa các thông tin thống kê và phân tích dựa trên lịch sử chu kỳ của user.
 * Được tính toán từ nhiều chu kỳ để đưa ra insight về sức khỏe sinh sản.
 * 
 * Sử dụng Builder pattern để dễ dàng tạo object với nhiều fields.
 * Thông thường được trả về trong API endpoint dashboard hoặc analytics.
 * 
 * Thông tin bao gồm:
 * - Thống kê cơ bản: số chu kỳ, độ dài trung bình
 * - Dự đoán: chu kỳ tiếp theo, rụng trứng, khả năng có thai
 * - Phân tích sức khỏe: tính đều đặn, insight y khoa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleAnalytics {
    
    /**
     * Tổng số chu kỳ đã được ghi nhận
     * Dùng để đánh giá độ tin cậy của phân tích (càng nhiều chu kỳ càng chính xác)
     */
    private int totalCycles;
    
    /**
     * Độ dài chu kỳ trung bình (tính bằng ngày)
     * Tính từ ngày đầu chu kỳ này đến ngày đầu chu kỳ tiếp theo
     * Chu kỳ bình thường: 21-35 ngày, trung bình 28 ngày
     */
    private double averageCycleLength;
    
    /**
     * Độ dài kinh nguyệt trung bình (tính bằng ngày)
     * Tính từ ngày bắt đầu đến ngày kết thúc kinh nguyệt
     * Bình thường: 3-7 ngày, trung bình 4-5 ngày
     */
    private double averagePeriodLength;
    
    /**
     * Mức độ đều đặn của chu kỳ
     * Possible values:
     * - "REGULAR": Chu kỳ đều đặn (độ lệch < 7 ngày)
     * - "SOMEWHAT_IRREGULAR": Hơi bất thường (độ lệch 7-20 ngày)
     * - "IRREGULAR": Bất thường (độ lệch > 20 ngày)
     * - "INSUFFICIENT_DATA": Chưa đủ dữ liệu để đánh giá
     */
    private String cycleRegularity;
    
    /**
     * Ngày bắt đầu chu kỳ gần nhất
     * Dùng làm baseline cho các tính toán dự đoán
     */
    private LocalDate lastPeriodDate;
    
    /**
     * Ngày dự đoán chu kỳ tiếp theo
     * Tính dựa trên độ dài chu kỳ trung bình và chu kỳ gần nhất
     */
    private LocalDate nextPredictedPeriod;
    
    /**
     * Ngày dự đoán rụng trứng tiếp theo
     * Thường là 14 ngày trước chu kỳ tiếp theo (luteal phase)
     */
    private LocalDate nextPredictedOvulation;
    
    /**
     * Khả năng có thai hiện tại (tính theo %)
     * Dựa trên vị trí hiện tại trong cửa sổ sinh sản
     * 0% = ngoài cửa sổ sinh sản, 25% = peak fertility
     */
    private double currentPregnancyLikelihood;
    
    /**
     * Danh sách các insight sức khỏe dựa trên phân tích chu kỳ
     * Ví dụ:
     * - "Chu kỳ của bạn khá đều đặn, đây là dấu hiệu tốt"
     * - "Chu kỳ hơi dài hơn bình thường, nên theo dõi thêm"
     * - "Nên gặp bác sĩ vì chu kỳ bất thường"
     * - "Thời gian kinh nguyệt ngắn hơn bình thường"
     */
    private List<String> healthInsights;
}

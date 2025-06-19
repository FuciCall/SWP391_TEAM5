package com.gha.gender_healthcare_api.cycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) cho request khai báo chu kỳ kinh nguyệt
 * 
 * Class này nhận dữ liệu từ client khi user khai báo thông tin chu kỳ kinh nguyệt.
 * Dữ liệu sẽ được validate và chuyển đổi thành entity MenstrualCycle để lưu database.
 * 
 * Thông tin bao gồm:
 * - Ngày bắt đầu và kết thúc chu kỳ
 * - Mức độ dòng kinh (light, normal, heavy)
 * - Các triệu chứng kèm theo (đau bụng, đau đầu, mood swings...)
 * - Ghi chú cá nhân của user
 * 
 * Validation rules:
 * - Ngày bắt đầu: bắt buộc, format yyyy-MM-dd
 * - Ngày kết thúc: tùy chọn (có thể khai báo sau), format yyyy-MM-dd
 * - Mức độ dòng kinh: bắt buộc, là enum (LIGHT, NORMAL, HEAVY)
 * - Triệu chứng: tùy chọn, format JSON array ["abdominal_pain", "headache", "mood_swings"]
 * - Ghi chú: tùy chọn, tối đa 500 ký tự
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenstrualCycleRequest {
      /**
     * Ngày đầu tiên của chu kỳ kinh nguyệt (quan trọng nhất để tính toán chu kỳ)
     */
    @NotNull(message = "Start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    /**
     * Ngày cuối cùng của chu kỳ kinh nguyệt (có thể null nếu chu kỳ chưa kết thúc)
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    /**
     * Mức độ dòng kinh: LIGHT (< 2 băng/ngày), NORMAL (2-4 băng/ngày), HEAVY (> 4 băng/ngày)
     */
    @NotNull(message = "Flow intensity is required")
    private MenstrualCycle.FlowIntensity flowIntensity;
    
    /**
     * Các triệu chứng dưới dạng JSON array, ví dụ: ["abdominal_pain", "headache", "mood_swings", "breast_tenderness"]
     */
    private String symptoms; // JSON array of symptoms
    
    /**
     * Ghi chú cá nhân (mức độ đau 1-10, thuốc đã dùng, hoạt động đặc biệt, quan sát khác)
     */
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}

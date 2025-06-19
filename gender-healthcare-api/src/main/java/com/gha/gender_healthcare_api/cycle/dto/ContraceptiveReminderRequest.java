package com.gha.gender_healthcare_api.cycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO (Data Transfer Object) cho request thiết lập nhắc nhở thuốc tránh thai
 * 
 * Class này nhận dữ liệu từ client (frontend/mobile app) và validate
 * trước khi xử lý logic trong service layer.
 * 
 * Thông tin bao gồm:
 * - Tên thuốc và thời gian nhắc nhở hàng ngày
 * - Chu kỳ uống thuốc (số ngày uống, số ngày nghỉ)
 * - Ngày bắt đầu vỉ thuốc hiện tại
 * - Múi giờ của user để tính toán thời gian chính xác
 * 
 * Validation rules:
 * - Tên thuốc: không được rỗng, tối đa 100 ký tự
 * - Thời gian nhắc: format HH:mm (24h)
 * - Ngày bắt đầu: format yyyy-MM-dd
 * - Chu kỳ uống: 1-35 ngày (phù hợp với các loại thuốc phổ biến)
 * - Chu kỳ nghỉ: 0-14 ngày (một số thuộc không cần nghỉ)
 * - Timezone: không được rỗng (ví dụ: "Asia/Ho_Chi_Minh")
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContraceptiveReminderRequest {
      /**
     * Tên thuốc tránh thai (ví dụ: Diane-35, Yasmin, Mercilon)
     */
    @NotBlank(message = "Pill name is required")
    @Size(max = 100, message = "Pill name cannot exceed 100 characters")
    private String pillName;
    
    /**
     * Thời gian nhắc nhở hàng ngày theo định dạng 24 giờ (ví dụ: "20:30" cho 8:30 PM)
     */
    @NotNull(message = "Reminder time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime reminderTime;
    
    /**
     * Ngày bắt đầu vỉ thuốc hiện tại để tính toán chu kỳ
     */
    @NotNull(message = "Pack start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate packStartDate;
    
    /**
     * Số ngày uống thuốc trong một chu kỳ (ví dụ: 21 ngày, 28 ngày)
     */
    @NotNull(message = "Pack duration is required")
    @Min(value = 1, message = "Pack duration must be at least 1 day")
    @Max(value = 35, message = "Pack duration cannot exceed 35 days")
    private Integer packDuration;
    
    /**
     * Số ngày nghỉ giữa các chu kỳ (ví dụ: 7 ngày, 0 ngày cho thuốc uống liên tục)
     */
    @NotNull(message = "Break duration is required")
    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 14, message = "Break duration cannot exceed 14 days")
    private Integer breakDuration;
    
    /**
     * Múi giờ của người dùng để lên lịch nhắc nhở chính xác
     * Ví dụ: "Asia/Ho_Chi_Minh", "UTC", "America/New_York"
     */
    @NotBlank(message = "Timezone is required")
    private String timezone;
}

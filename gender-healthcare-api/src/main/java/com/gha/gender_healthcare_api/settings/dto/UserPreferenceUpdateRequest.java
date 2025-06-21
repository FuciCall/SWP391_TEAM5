package com.gha.gender_healthcare_api.settings.dto;

import com.gha.gender_healthcare_api.settings.entity.UserPreference;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO cho request cập nhật user preferences
 * 
 * Chứa tất cả các trường có thể được user cập nhật trong settings.
 * Có validation để đảm bảo dữ liệu hợp lệ trước khi lưu vào database.
 * 
 * Sử dụng trong API endpoints để nhận request từ frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceUpdateRequest {
    
    /**
     * Giao diện người dùng ưa thích
     * Có thể null - sẽ giữ nguyên giá trị hiện tại
     */
    private UserPreference.ThemePreference themePreference;
    
    /**
     * Ngôn ngữ hiển thị giao diện
     * Có thể null - sẽ giữ nguyên giá trị hiện tại
     */
    private UserPreference.LanguagePreference languagePreference;
    
    /**
     * Múi giờ của người dùng
     * Phải là timezone hợp lệ
     */
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;
    
    /**
     * Có nhận thông báo qua email hay không
     */
    private Boolean emailNotificationsEnabled;
    
    /**
     * Có nhận thông báo qua SMS hay không
     */
    private Boolean smsNotificationsEnabled;
    
    /**
     * Có nhận push notification hay không
     */
    private Boolean pushNotificationsEnabled;
    
    /**
     * Có bật two-factor authentication hay không
     */
    private Boolean twoFactorEnabled;
    
    /**
     * Thời gian timeout của session (phút)
     * Phải từ 15 đến 120 phút
     */
    @Min(value = 15, message = "Session timeout must be at least 15 minutes")
    @Max(value = 120, message = "Session timeout must not exceed 120 minutes")
    private Integer sessionTimeoutMinutes;
    
    /**
     * Có hiển thị profile công khai hay không
     */
    private Boolean publicProfileVisible;
    
    /**
     * Có chia sẻ dữ liệu cho mục đích nghiên cứu hay không
     */
    private Boolean researchDataSharing;
    
    /**
     * Có nhận email marketing hay không
     */
    private Boolean marketingEmailsEnabled;
    
    /**
     * Format hiển thị ngày tháng
     * Phải là một trong các format được hỗ trợ
     */
    @Pattern(regexp = "DD/MM/YYYY|MM/DD/YYYY|YYYY-MM-DD", 
             message = "Date format must be DD/MM/YYYY, MM/DD/YYYY, or YYYY-MM-DD")
    private String dateFormat;
    
    /**
     * Format hiển thị thời gian
     * Phải là 12H hoặc 24H
     */
    @Pattern(regexp = "12H|24H", message = "Time format must be 12H or 24H")
    private String timeFormat;
    
    /**
     * Đơn vị đo lường ưa thích
     */
    private UserPreference.MeasurementUnit measurementUnit;
    
    /**
     * Kiểm tra xem request có chứa ít nhất một trường để cập nhật không
     * 
     * @return true nếu có ít nhất một trường không null
     */
    public boolean hasAnyFieldToUpdate() {
        return themePreference != null ||
               languagePreference != null ||
               timezone != null ||
               emailNotificationsEnabled != null ||
               smsNotificationsEnabled != null ||
               pushNotificationsEnabled != null ||
               twoFactorEnabled != null ||
               sessionTimeoutMinutes != null ||
               publicProfileVisible != null ||
               researchDataSharing != null ||
               marketingEmailsEnabled != null ||
               dateFormat != null ||
               timeFormat != null ||
               measurementUnit != null;
    }
    
    /**
     * Validate business rules cho preferences
     * 
     * @return true nếu tất cả business rules đều hợp lệ
     */
    public boolean isValid() {
        // Nếu bật SMS notifications thì phải có phone number (sẽ check ở service layer)
        // Nếu bật 2FA thì phải có email hoặc phone (sẽ check ở service layer)
        
        // Validate timezone format nếu có
        if (timezone != null && !timezone.trim().isEmpty()) {
            try {
                java.time.ZoneId.of(timezone);
            } catch (Exception e) {
                return false;
            }
        }
        
        return true;
    }
}

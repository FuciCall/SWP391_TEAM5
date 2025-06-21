package com.gha.gender_healthcare_api.settings.dto;

import com.gha.gender_healthcare_api.settings.entity.UserPreference;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho response trả về user preferences
 * 
 * Chứa tất cả thông tin preferences của user để hiển thị trên frontend.
 * Không chứa sensitive information và đã được format phù hợp.
 * 
 * Sử dụng trong API endpoints để trả về cho client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceResponse {
    
    /**
     * ID của user preferences
     */
    private Long id;
    
    /**
     * ID của user sở hữu preferences này
     */
    private Long userId;
    
    /**
     * Giao diện người dùng ưa thích
     */
    private UserPreference.ThemePreference themePreference;
    
    /**
     * Ngôn ngữ hiển thị giao diện
     */
    private UserPreference.LanguagePreference languagePreference;
    
    /**
     * Múi giờ của người dùng
     */
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
     */
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
     */
    private String dateFormat;
    
    /**
     * Format hiển thị thời gian
     */
    private String timeFormat;
    
    /**
     * Đơn vị đo lường ưa thích
     */
    private UserPreference.MeasurementUnit measurementUnit;
    
    /**
     * Thời gian tạo preferences
     */
    private LocalDateTime createdAt;
    
    /**
     * Thời gian cập nhật cuối cùng
     */
    private LocalDateTime updatedAt;
    
    /**
     * Tạo UserPreferenceResponse từ UserPreference entity
     * 
     * @param userPreference entity cần convert
     * @return UserPreferenceResponse tương ứng
     */
    public static UserPreferenceResponse fromEntity(UserPreference userPreference) {
        if (userPreference == null) {
            return null;
        }
        
        return UserPreferenceResponse.builder()
                .id(userPreference.getId())
                .userId(userPreference.getUserId())
                .themePreference(userPreference.getThemePreference())
                .languagePreference(userPreference.getLanguagePreference())
                .timezone(userPreference.getTimezone())
                .emailNotificationsEnabled(userPreference.getEmailNotificationsEnabled())
                .smsNotificationsEnabled(userPreference.getSmsNotificationsEnabled())
                .pushNotificationsEnabled(userPreference.getPushNotificationsEnabled())
                .twoFactorEnabled(userPreference.getTwoFactorEnabled())
                .sessionTimeoutMinutes(userPreference.getSessionTimeoutMinutes())
                .publicProfileVisible(userPreference.getPublicProfileVisible())
                .researchDataSharing(userPreference.getResearchDataSharing())
                .marketingEmailsEnabled(userPreference.getMarketingEmailsEnabled())
                .dateFormat(userPreference.getDateFormat())
                .timeFormat(userPreference.getTimeFormat())
                .measurementUnit(userPreference.getMeasurementUnit())
                .createdAt(userPreference.getCreatedAt())
                .updatedAt(userPreference.getUpdatedAt())
                .build();
    }
    
    /**
     * Tạo UserPreferenceResponse với default values cho user mới
     * 
     * @param userId ID của user
     * @return UserPreferenceResponse với giá trị mặc định
     */
    public static UserPreferenceResponse createDefault(Long userId) {
        return UserPreferenceResponse.builder()
                .userId(userId)
                .themePreference(UserPreference.ThemePreference.LIGHT)
                .languagePreference(UserPreference.LanguagePreference.EN)
                .timezone("Asia/Ho_Chi_Minh")
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .twoFactorEnabled(false)
                .sessionTimeoutMinutes(30)
                .publicProfileVisible(false)
                .researchDataSharing(false)
                .marketingEmailsEnabled(false)
                .dateFormat("DD/MM/YYYY")
                .timeFormat("24H")
                .measurementUnit(UserPreference.MeasurementUnit.METRIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

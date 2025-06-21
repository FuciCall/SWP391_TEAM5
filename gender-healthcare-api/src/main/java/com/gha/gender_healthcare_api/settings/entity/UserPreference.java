package com.gha.gender_healthcare_api.settings.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho các tùy chọn cá nhân của người dùng
 * 
 * Chứa tất cả các thiết lập có thể tùy chỉnh như:
 * - Giao diện (theme, ngôn ngữ)
 * - Thông báo (email, SMS, push notification)
 * - Bảo mật (2FA, session timeout)
 * - Quyền riêng tư (visibility của profile)
 * 
 * Mỗi user có một bản ghi preferences duy nhất
 */
@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {
    
    /**
     * ID chính của bảng user preferences
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * ID của user - mối quan hệ với bảng users
     * Unique constraint để đảm bảo mỗi user chỉ có 1 preferences record
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    /**
     * Giao diện người dùng ưa thích
     * Possible values: "LIGHT", "DARK", "AUTO"
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "theme_preference", length = 20)
    @Builder.Default
    private ThemePreference themePreference = ThemePreference.LIGHT;
    
    /**
     * Ngôn ngữ hiển thị giao diện
     * Possible values: "EN", "VI", "AUTO" (theo browser)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "language_preference", length = 10)
    @Builder.Default
    private LanguagePreference languagePreference = LanguagePreference.EN;
    
    /**
     * Múi giờ của người dùng
     * Format: "Asia/Ho_Chi_Minh", "America/New_York", etc.
     */
    @Column(name = "timezone", length = 50)
    @Builder.Default
    private String timezone = "Asia/Ho_Chi_Minh";
    
    /**
     * Có nhận thông báo qua email hay không
     */
    @Column(name = "email_notifications_enabled")
    @Builder.Default
    private Boolean emailNotificationsEnabled = true;
    
    /**
     * Có nhận thông báo qua SMS hay không
     */
    @Column(name = "sms_notifications_enabled")
    @Builder.Default
    private Boolean smsNotificationsEnabled = false;
    
    /**
     * Có nhận push notification hay không (cho mobile app)
     */
    @Column(name = "push_notifications_enabled")
    @Builder.Default
    private Boolean pushNotificationsEnabled = true;
    
    /**
     * Có bật two-factor authentication hay không
     */
    @Column(name = "two_factor_enabled")
    @Builder.Default
    private Boolean twoFactorEnabled = false;
    
    /**
     * Thời gian timeout của session (phút)
     * Default: 30 phút, có thể từ 15-120 phút
     */
    @Column(name = "session_timeout_minutes")
    @Builder.Default
    private Integer sessionTimeoutMinutes = 30;
    
    /**
     * Có hiển thị profile công khai hay không
     * True: các user khác có thể thấy profile
     * False: chỉ staff/consultant có thể thấy
     */
    @Column(name = "public_profile_visible")
    @Builder.Default
    private Boolean publicProfileVisible = false;
    
    /**
     * Có chia sẻ dữ liệu cho mục đích nghiên cứu hay không
     * (Anonymous data cho research)
     */
    @Column(name = "research_data_sharing")
    @Builder.Default
    private Boolean researchDataSharing = false;
    
    /**
     * Có nhận email marketing hay không
     */
    @Column(name = "marketing_emails_enabled")
    @Builder.Default
    private Boolean marketingEmailsEnabled = false;
    
    /**
     * Format hiển thị ngày tháng
     * Possible values: "DD/MM/YYYY", "MM/DD/YYYY", "YYYY-MM-DD"
     */
    @Column(name = "date_format", length = 20)
    @Builder.Default
    private String dateFormat = "DD/MM/YYYY";
    
    /**
     * Format hiển thị thời gian
     * Possible values: "12H", "24H"
     */
    @Column(name = "time_format", length = 10)
    @Builder.Default
    private String timeFormat = "24H";
    
    /**
     * Đơn vị đo lường ưa thích
     * Possible values: "METRIC", "IMPERIAL"
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_unit")
    @Builder.Default
    private MeasurementUnit measurementUnit = MeasurementUnit.METRIC;
    
    /**
     * Thời gian tạo bản ghi
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Thời gian cập nhật cuối cùng
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Callback được gọi trước khi persist entity
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Callback được gọi trước khi update entity
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Enum cho tùy chọn giao diện
     */
    public enum ThemePreference {
        LIGHT,    // Giao diện sáng
        DARK,     // Giao diện tối
        AUTO      // Tự động theo hệ thống
    }
    
    /**
     * Enum cho tùy chọn ngôn ngữ
     */
    public enum LanguagePreference {
        EN,       // Tiếng Anh
        VI,       // Tiếng Việt
        AUTO      // Tự động theo browser
    }
    
    /**
     * Enum cho đơn vị đo lường
     */
    public enum MeasurementUnit {
        METRIC,   // Hệ mét (kg, cm, celsius)
        IMPERIAL  // Hệ Anh-Mỹ (lbs, inches, fahrenheit)
    }
}

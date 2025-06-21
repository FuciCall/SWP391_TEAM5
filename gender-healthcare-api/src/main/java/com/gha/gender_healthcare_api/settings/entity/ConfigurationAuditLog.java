package com.gha.gender_healthcare_api.settings.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho lịch sử thay đổi cấu hình
 * 
 * Ghi lại tất cả các thay đổi được thực hiện đối với:
 * - User preferences
 * - System configurations
 * 
 * Phục vụ mục đích:
 * - Audit trail (theo dõi ai thay đổi gì khi nào)
 * - Rollback (khôi phục về cấu hình cũ)
 * - Compliance (tuân thủ quy định bảo mật)
 */
@Entity
@Table(name = "configuration_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationAuditLog {
    
    /**
     * ID chính của bảng audit logs
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Loại cấu hình được thay đổi
     * Possible values: "USER_PREFERENCE", "SYSTEM_CONFIGURATION"
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "config_type", nullable = false, length = 30)
    private ConfigurationType configurationType;
    
    /**
     * ID của bản ghi cấu hình được thay đổi
     * - Nếu là USER_PREFERENCE: ID của UserPreference
     * - Nếu là SYSTEM_CONFIGURATION: ID của SystemConfiguration
     */
    @Column(name = "config_record_id", nullable = false)
    private Long configRecordId;
    
    /**
     * Tên field/key được thay đổi
     * Ví dụ: "themePreference", "emailNotificationsEnabled", "email.smtp.host"
     */
    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;
    
    /**
     * Giá trị cũ trước khi thay đổi
     */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;
    
    /**
     * Giá trị mới sau khi thay đổi
     */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
    
    /**
     * Loại hành động được thực hiện
     * Possible values: "CREATE", "UPDATE", "DELETE"
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 20)
    private ActionType actionType;
    
    /**
     * ID của user thực hiện thay đổi
     */
    @Column(name = "changed_by", nullable = false)
    private Long changedBy;
    
    /**
     * Địa chỉ IP của user thực hiện thay đổi
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    /**
     * User Agent của browser/client
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    /**
     * Lý do thay đổi (optional)
     */
    @Column(name = "change_reason", length = 500)
    private String changeReason;
    
    /**
     * Có thành công hay không
     */
    @Column(name = "is_successful")
    @Builder.Default
    private Boolean isSuccessful = true;
    
    /**
     * Thông báo lỗi nếu không thành công
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    /**
     * Thời gian thực hiện thay đổi
     */
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
    
    /**
     * Session ID (để tracking các thay đổi trong cùng session)
     */
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    /**
     * Callback được gọi trước khi persist entity
     */
    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
    
    /**
     * Enum cho loại cấu hình
     */
    public enum ConfigurationType {
        USER_PREFERENCE,        // Cấu hình của user cá nhân
        SYSTEM_CONFIGURATION   // Cấu hình toàn hệ thống
    }
    
    /**
     * Enum cho loại hành động
     */
    public enum ActionType {
        CREATE,   // Tạo mới
        UPDATE,   // Cập nhật
        DELETE    // Xóa
    }
}

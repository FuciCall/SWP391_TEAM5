package com.gha.gender_healthcare_api.settings.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho cấu hình toàn hệ thống
 * 
 * Chứa các thiết lập ở mức ứng dụng mà admin có thể cấu hình:
 * - Cài đặt email server
 * - Cài đặt SMS gateway
 * - Cài đặt payment gateway
 * - Cài đặt bảo mật hệ thống
 * - Cài đặt business logic
 * 
 * Chỉ admin mới có quyền thay đổi các cài đặt này
 */
@Entity
@Table(name = "system_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfiguration {
    
    /**
     * ID chính của bảng system configurations
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Khóa cấu hình (unique identifier)
     * Ví dụ: "email.smtp.host", "sms.gateway.url", "payment.stripe.public_key"
     */
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;
    
    /**
     * Giá trị cấu hình
     * Có thể là string, number, boolean, JSON
     */
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;
    
    /**
     * Mô tả về cấu hình này
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * Nhóm cấu hình (để dễ quản lý)
     * Ví dụ: "EMAIL", "SMS", "PAYMENT", "SECURITY", "BUSINESS"
     */
    @Column(name = "config_group", length = 50)
    private String configGroup;
    
    /**
     * Kiểu dữ liệu của giá trị
     * Possible values: "STRING", "INTEGER", "BOOLEAN", "JSON", "ENCRYPTED"
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", length = 20)
    private DataType dataType;
    
    /**
     * Có phải cấu hình nhạy cảm cần mã hóa không
     * True: sẽ encrypt value trước khi lưu DB
     */
    @Column(name = "is_sensitive")
    @Builder.Default
    private Boolean isSensitive = false;
    
    /**
     * Có cho phép chỉnh sửa từ giao diện không
     * False: chỉ có thể thay đổi qua code/database
     */
    @Column(name = "is_editable")
    @Builder.Default
    private Boolean isEditable = true;
    
    /**
     * Có cần restart ứng dụng sau khi thay đổi không
     */
    @Column(name = "requires_restart")
    @Builder.Default
    private Boolean requiresRestart = false;
    
    /**
     * Giá trị mặc định của cấu hình
     */
    @Column(name = "default_value", columnDefinition = "TEXT")
    private String defaultValue;
    
    /**
     * Validation rule cho giá trị (regex pattern)
     * Ví dụ: "^[0-9]+$" cho số nguyên, "^.+@.+\\..+$" cho email
     */
    @Column(name = "validation_rule", length = 200)
    private String validationRule;
    
    /**
     * ID của admin đã tạo cấu hình này
     */
    @Column(name = "created_by")
    private Long createdBy;
    
    /**
     * ID của admin đã cập nhật cấu hình lần cuối
     */
    @Column(name = "updated_by")
    private Long updatedBy;
    
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
     * Thời gian cấu hình có hiệu lực
     * Null = có hiệu lực ngay lập tức
     */
    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;
    
    /**
     * Thời gian cấu hình hết hiệu lực
     * Null = không bao giờ hết hiệu lực
     */
    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;
    
    /**
     * Callback được gọi trước khi persist entity
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.effectiveFrom == null) {
            this.effectiveFrom = LocalDateTime.now();
        }
    }
    
    /**
     * Callback được gọi trước khi update entity
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Enum cho kiểu dữ liệu của giá trị cấu hình
     */
    public enum DataType {
        STRING,     // Chuỗi văn bản
        INTEGER,    // Số nguyên
        BOOLEAN,    // True/False
        JSON,       // Đối tượng JSON
        ENCRYPTED   // Dữ liệu đã mã hóa
    }
}

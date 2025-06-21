package com.gha.gender_healthcare_api.settings.dto;

import com.gha.gender_healthcare_api.settings.entity.SystemConfiguration;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho request tạo hoặc cập nhật system configuration
 * 
 * Chứa tất cả thông tin cần thiết để tạo hoặc cập nhật một cấu hình hệ thống.
 * Có validation để đảm bảo dữ liệu hợp lệ và bảo mật.
 * 
 * Chỉ admin mới có quyền sử dụng DTO này.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigurationRequest {
    
    /**
     * Khóa cấu hình (bắt buộc cho tạo mới)
     * Phải unique và follow naming convention
     */
    @NotBlank(message = "Config key is required")
    @Size(max = 100, message = "Config key must not exceed 100 characters")
    @Pattern(regexp = "^[a-z0-9]+(?:\\.[a-z0-9]+)*$", 
             message = "Config key must follow dot notation pattern (e.g., email.smtp.host)")
    private String configKey;
    
    /**
     * Giá trị cấu hình (bắt buộc)
     */
    @NotNull(message = "Config value is required")
    private String configValue;
    
    /**
     * Mô tả về cấu hình này
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * Nhóm cấu hình
     * Phải là uppercase và chỉ chứa chữ cái, số, underscore
     */
    @Size(max = 50, message = "Config group must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", 
             message = "Config group must be uppercase with letters, numbers, and underscores only")
    private String configGroup;
    
    /**
     * Kiểu dữ liệu của giá trị (bắt buộc)
     */
    @NotNull(message = "Data type is required")
    private SystemConfiguration.DataType dataType;
    
    /**
     * Có phải cấu hình nhạy cảm cần mã hóa không
     */
    @Builder.Default
    private Boolean isSensitive = false;
    
    /**
     * Có cho phép chỉnh sửa từ giao diện không
     */
    @Builder.Default
    private Boolean isEditable = true;
    
    /**
     * Có cần restart ứng dụng sau khi thay đổi không
     */
    @Builder.Default
    private Boolean requiresRestart = false;
    
    /**
     * Giá trị mặc định của cấu hình
     */
    private String defaultValue;
    
    /**
     * Validation rule cho giá trị (regex pattern)
     */
    @Size(max = 200, message = "Validation rule must not exceed 200 characters")
    private String validationRule;
    
    /**
     * Thời gian cấu hình có hiệu lực
     * Null = có hiệu lực ngay lập tức
     */
    private LocalDateTime effectiveFrom;
    
    /**
     * Thời gian cấu hình hết hiệu lực
     * Null = không bao giờ hết hiệu lực
     */
    private LocalDateTime effectiveTo;
    
    /**
     * Lý do thay đổi (cho audit log)
     */
    @Size(max = 500, message = "Change reason must not exceed 500 characters")
    private String changeReason;
    
    /**
     * Validate business rules cho configuration
     * 
     * @return true nếu tất cả business rules đều hợp lệ
     */
    public boolean isValid() {
        // Validate effective dates
        if (effectiveFrom != null && effectiveTo != null) {
            if (effectiveTo.isBefore(effectiveFrom)) {
                return false;
            }
        }
        
        // Validate config value theo data type
        if (!isValidValueForDataType()) {
            return false;
        }
        
        // Validate sensitive configurations
        if (Boolean.TRUE.equals(isSensitive) && configKey != null) {
            // Sensitive configs should have certain patterns
            if (!configKey.contains("password") && 
                !configKey.contains("secret") && 
                !configKey.contains("key") &&
                !configKey.contains("token")) {
                // Warning: might not be actually sensitive
            }
        }
        
        return true;
    }
    
    /**
     * Kiểm tra giá trị có phù hợp với data type không
     * 
     * @return true nếu giá trị hợp lệ với data type
     */
    private boolean isValidValueForDataType() {
        if (configValue == null || dataType == null) {
            return true; // Sẽ validate ở annotation level
        }
        
        try {
            switch (dataType) {
                case INTEGER:
                    Integer.parseInt(configValue);
                    break;
                case BOOLEAN:
                    if (!"true".equalsIgnoreCase(configValue) && 
                        !"false".equalsIgnoreCase(configValue)) {
                        return false;
                    }
                    break;
                case JSON:
                    // Basic JSON validation - should start with { or [
                    String trimmed = configValue.trim();
                    if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                        return false;
                    }
                    break;
                case STRING:
                case ENCRYPTED:
                default:
                    // Any string is valid
                    break;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate validation rule nếu có
     * 
     * @return true nếu validation rule hợp lệ
     */
    public boolean isValidationRuleValid() {
        if (validationRule == null || validationRule.trim().isEmpty()) {
            return true;
        }
        
        try {
            java.util.regex.Pattern.compile(validationRule);
            return true;
        } catch (java.util.regex.PatternSyntaxException e) {
            return false;
        }
    }
    
    /**
     * Kiểm tra config value có match validation rule không
     * 
     * @return true nếu match hoặc không có validation rule
     */
    public boolean doesValueMatchValidationRule() {
        if (validationRule == null || validationRule.trim().isEmpty()) {
            return true;
        }
        
        if (configValue == null) {
            return false;
        }
        
        try {
            return configValue.matches(validationRule);
        } catch (Exception e) {
            return false;
        }
    }
}

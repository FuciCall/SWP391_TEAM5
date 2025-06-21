package com.gha.gender_healthcare_api.settings.dto;

import com.gha.gender_healthcare_api.settings.entity.SystemConfiguration;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho response trả về system configuration
 * 
 * Chứa thông tin cấu hình hệ thống để hiển thị cho admin.
 * Có thể ẩn giá trị sensitive tùy theo quyền của user.
 * 
 * Sử dụng trong API endpoints để trả về cho admin interface.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigurationResponse {
    
    /**
     * ID của system configuration
     */
    private Long id;
    
    /**
     * Khóa cấu hình
     */
    private String configKey;
    
    /**
     * Giá trị cấu hình (có thể bị ẩn nếu sensitive)
     */
    private String configValue;
    
    /**
     * Giá trị hiển thị (masked nếu sensitive)
     */
    private String displayValue;
    
    /**
     * Mô tả về cấu hình này
     */
    private String description;
    
    /**
     * Nhóm cấu hình
     */
    private String configGroup;
    
    /**
     * Kiểu dữ liệu của giá trị
     */
    private SystemConfiguration.DataType dataType;
    
    /**
     * Có phải cấu hình nhạy cảm không
     */
    private Boolean isSensitive;
    
    /**
     * Có cho phép chỉnh sửa từ giao diện không
     */
    private Boolean isEditable;
    
    /**
     * Có cần restart ứng dụng sau khi thay đổi không
     */
    private Boolean requiresRestart;
    
    /**
     * Giá trị mặc định của cấu hình
     */
    private String defaultValue;
    
    /**
     * Validation rule cho giá trị
     */
    private String validationRule;
    
    /**
     * ID của admin đã tạo cấu hình này
     */
    private Long createdBy;
    
    /**
     * ID của admin đã cập nhật cấu hình lần cuối
     */
    private Long updatedBy;
    
    /**
     * Thời gian tạo bản ghi
     */
    private LocalDateTime createdAt;
    
    /**
     * Thời gian cập nhật cuối cùng
     */
    private LocalDateTime updatedAt;
    
    /**
     * Thời gian cấu hình có hiệu lực
     */
    private LocalDateTime effectiveFrom;
    
    /**
     * Thời gian cấu hình hết hiệu lực
     */
    private LocalDateTime effectiveTo;
    
    /**
     * Có đang hiệu lực không (calculated field)
     */
    private Boolean isEffective;
    
    /**
     * Có giá trị hiện tại khác với default không
     */
    private Boolean isModifiedFromDefault;
    
    /**
     * Tạo SystemConfigurationResponse từ SystemConfiguration entity
     * 
     * @param config entity cần convert
     * @param maskSensitiveValues có mask sensitive values không
     * @return SystemConfigurationResponse tương ứng
     */
    public static SystemConfigurationResponse fromEntity(SystemConfiguration config, boolean maskSensitiveValues) {
        if (config == null) {
            return null;
        }
        
        LocalDateTime now = LocalDateTime.now();
        boolean isEffective = (config.getEffectiveFrom() == null || config.getEffectiveFrom().isBefore(now) || config.getEffectiveFrom().isEqual(now)) &&
                             (config.getEffectiveTo() == null || config.getEffectiveTo().isAfter(now));
        
        boolean isModified = config.getDefaultValue() != null && 
                           !config.getDefaultValue().equals(config.getConfigValue());
        
        String displayValue = config.getConfigValue();
        if (maskSensitiveValues && Boolean.TRUE.equals(config.getIsSensitive())) {
            displayValue = maskSensitiveValue(config.getConfigValue());
        }
        
        return SystemConfigurationResponse.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .configValue(maskSensitiveValues && Boolean.TRUE.equals(config.getIsSensitive()) ? null : config.getConfigValue())
                .displayValue(displayValue)
                .description(config.getDescription())
                .configGroup(config.getConfigGroup())
                .dataType(config.getDataType())
                .isSensitive(config.getIsSensitive())
                .isEditable(config.getIsEditable())
                .requiresRestart(config.getRequiresRestart())
                .defaultValue(config.getDefaultValue())
                .validationRule(config.getValidationRule())
                .createdBy(config.getCreatedBy())
                .updatedBy(config.getUpdatedBy())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .effectiveFrom(config.getEffectiveFrom())
                .effectiveTo(config.getEffectiveTo())
                .isEffective(isEffective)
                .isModifiedFromDefault(isModified)
                .build();
    }
    
    /**
     * Tạo response không mask sensitive values (cho super admin)
     * 
     * @param config entity cần convert
     * @return SystemConfigurationResponse đầy đủ thông tin
     */
    public static SystemConfigurationResponse fromEntityFull(SystemConfiguration config) {
        return fromEntity(config, false);
    }
    
    /**
     * Tạo response có mask sensitive values (cho admin thường)
     * 
     * @param config entity cần convert
     * @return SystemConfigurationResponse có mask sensitive data
     */
    public static SystemConfigurationResponse fromEntityMasked(SystemConfiguration config) {
        return fromEntity(config, true);
    }
    
    /**
     * Mask giá trị sensitive
     * 
     * @param value giá trị gốc
     * @return giá trị đã được mask
     */
    private static String maskSensitiveValue(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        
        // Hiển thị 2 ký tự đầu và 2 ký tự cuối, mask phần giữa
        int length = value.length();
        if (length <= 8) {
            return value.substring(0, 2) + "****" + value.substring(length - 2);
        } else {
            return value.substring(0, 3) + "******" + value.substring(length - 3);
        }
    }
    
    /**
     * Tạo summary response chỉ chứa thông tin cơ bản
     * 
     * @param config entity cần convert
     * @return SystemConfigurationResponse chỉ có thông tin cơ bản
     */
    public static SystemConfigurationResponse createSummary(SystemConfiguration config) {
        if (config == null) {
            return null;
        }
        
        return SystemConfigurationResponse.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .description(config.getDescription())
                .configGroup(config.getConfigGroup())
                .dataType(config.getDataType())
                .isSensitive(config.getIsSensitive())
                .isEditable(config.getIsEditable())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}

package com.gha.gender_healthcare_api.settings.service;

import com.gha.gender_healthcare_api.settings.dto.SystemConfigurationRequest;
import com.gha.gender_healthcare_api.settings.dto.SystemConfigurationResponse;
import com.gha.gender_healthcare_api.settings.entity.ConfigurationAuditLog;
import com.gha.gender_healthcare_api.settings.entity.SystemConfiguration;
import com.gha.gender_healthcare_api.settings.repository.ConfigurationAuditLogRepository;
import com.gha.gender_healthcare_api.settings.repository.SystemConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ cho cấu hình hệ thống
 * Quản lý các thiết lập toàn cục của ứng dụng dành cho admin
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SystemConfigurationService {

    private final SystemConfigurationRepository systemConfigurationRepository;
    private final ConfigurationAuditLogRepository auditLogRepository;

    /**
     * Lấy tất cả cấu hình hệ thống với phân trang
     * @param pageable thông tin phân trang
     * @return danh sách cấu hình hệ thống
     */
    @Transactional(readOnly = true)
    public Page<SystemConfigurationResponse> getAllConfigurations(Pageable pageable) {
        log.info("Getting all system configurations with pagination");
        return systemConfigurationRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    /**
     * Lấy cấu hình theo key
     * @param configKey key của cấu hình
     * @return cấu hình hệ thống
     */
    @Cacheable(value = "systemConfig", key = "#configKey")
    @Transactional(readOnly = true)
    public Optional<SystemConfigurationResponse> getConfigurationByKey(String configKey) {
        log.info("Getting system configuration by key: {}", configKey);
        return systemConfigurationRepository.findByConfigKey(configKey)
                .map(this::convertToResponse);
    }

    /**
     * Lấy giá trị cấu hình theo key
     * @param configKey key của cấu hình
     * @param defaultValue giá trị mặc định nếu không tìm thấy
     * @return giá trị cấu hình
     */
    @Cacheable(value = "systemConfigValue", key = "#configKey")
    @Transactional(readOnly = true)
    public String getConfigurationValue(String configKey, String defaultValue) {
        log.debug("Getting configuration value for key: {}", configKey);
        return systemConfigurationRepository.findByConfigKey(configKey)
                .map(SystemConfiguration::getConfigValue)
                .orElse(defaultValue);
    }

    /**
     * Lấy danh sách cấu hình theo nhóm
     * @param category nhóm cấu hình
     * @return danh sách cấu hình
     */
    @Transactional(readOnly = true)
    public List<SystemConfigurationResponse> getConfigurationsByCategory(String category) {
        log.info("Getting configurations by category: {}", category);
        return systemConfigurationRepository.findByCategory(category)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Tạo cấu hình hệ thống mới
     * @param request thông tin cấu hình
     * @param adminUserId ID của admin thực hiện
     * @return cấu hình đã tạo
     */
    @CacheEvict(value = {"systemConfig", "systemConfigValue"}, allEntries = true)
    public SystemConfigurationResponse createConfiguration(SystemConfigurationRequest request, Long adminUserId) {
        log.info("Creating new system configuration with key: {}", request.getConfigKey());
        
        // Kiểm tra key đã tồn tại chưa
        if (systemConfigurationRepository.existsByConfigKey(request.getConfigKey())) {
            throw new IllegalArgumentException("Configuration key already exists: " + request.getConfigKey());
        }

        // Validate request
        validateConfigurationRequest(request);

        SystemConfiguration config = SystemConfiguration.builder()
                .configKey(request.getConfigKey())
                .configValue(request.getConfigValue())
                .description(request.getDescription())
                .category(request.getCategory())
                .dataType(request.getDataType())
                .isSystem(request.getIsSystem())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SystemConfiguration savedConfig = systemConfigurationRepository.save(config);
        
        // Ghi audit log
        createAuditLog(savedConfig.getId(), adminUserId, "CREATE", 
                      null, request.getConfigValue());

        log.info("System configuration created successfully with ID: {}", savedConfig.getId());
        return convertToResponse(savedConfig);
    }

    /**
     * Cập nhật cấu hình hệ thống
     * @param id ID của cấu hình
     * @param request thông tin cập nhật
     * @param adminUserId ID của admin thực hiện
     * @return cấu hình đã cập nhật
     */
    @CacheEvict(value = {"systemConfig", "systemConfigValue"}, allEntries = true)
    public SystemConfigurationResponse updateConfiguration(Long id, SystemConfigurationRequest request, Long adminUserId) {
        log.info("Updating system configuration with ID: {}", id);
        
        SystemConfiguration config = systemConfigurationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("System configuration not found with ID: " + id));

        // Kiểm tra quyền chỉnh sửa cấu hình hệ thống
        if (config.getIsSystem() && !isSystemAdmin(adminUserId)) {
            throw new IllegalArgumentException("Only system admin can modify system configurations");
        }

        // Validate request
        validateConfigurationRequest(request);

        String oldValue = config.getConfigValue();
        
        config.setConfigValue(request.getConfigValue());
        config.setDescription(request.getDescription());
        config.setCategory(request.getCategory());
        config.setDataType(request.getDataType());
        config.setUpdatedAt(LocalDateTime.now());

        SystemConfiguration savedConfig = systemConfigurationRepository.save(config);
        
        // Ghi audit log
        createAuditLog(savedConfig.getId(), adminUserId, "UPDATE", oldValue, request.getConfigValue());

        log.info("System configuration updated successfully with ID: {}", savedConfig.getId());
        return convertToResponse(savedConfig);
    }

    /**
     * Xóa cấu hình hệ thống
     * @param id ID của cấu hình
     * @param adminUserId ID của admin thực hiện
     */
    @CacheEvict(value = {"systemConfig", "systemConfigValue"}, allEntries = true)
    public void deleteConfiguration(Long id, Long adminUserId) {
        log.info("Deleting system configuration with ID: {}", id);
        
        SystemConfiguration config = systemConfigurationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("System configuration not found with ID: " + id));

        // Kiểm tra quyền xóa cấu hình hệ thống
        if (config.getIsSystem()) {
            throw new IllegalArgumentException("Cannot delete system configurations");
        }

        // Ghi audit log trước khi xóa
        createAuditLog(config.getId(), adminUserId, "DELETE", config.getConfigValue(), null);

        systemConfigurationRepository.delete(config);
        log.info("System configuration deleted successfully with ID: {}", id);
    }

    /**
     * Kích hoạt/vô hiệu hóa cấu hình
     * @param id ID của cấu hình
     * @param isActive trạng thái kích hoạt
     * @param adminUserId ID của admin thực hiện
     */
    @CacheEvict(value = {"systemConfig", "systemConfigValue"}, allEntries = true)
    public void toggleConfigurationStatus(Long id, Boolean isActive, Long adminUserId) {
        log.info("Toggling configuration status for ID: {} to {}", id, isActive);
        
        SystemConfiguration config = systemConfigurationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("System configuration not found with ID: " + id));

        config.setIsActive(isActive);
        config.setUpdatedAt(LocalDateTime.now());
        systemConfigurationRepository.save(config);

        // Ghi audit log
        createAuditLog(config.getId(), adminUserId, "STATUS_CHANGE", 
                      String.valueOf(!isActive), String.valueOf(isActive));

        log.info("Configuration status toggled successfully for ID: {}", id);
    }

    /**
     * Tìm kiếm cấu hình theo từ khóa
     * @param keyword từ khóa tìm kiếm
     * @param pageable thông tin phân trang
     * @return danh sách cấu hình tìm được
     */
    @Transactional(readOnly = true)
    public Page<SystemConfigurationResponse> searchConfigurations(String keyword, Pageable pageable) {
        log.info("Searching configurations with keyword: {}", keyword);
        return systemConfigurationRepository.findByConfigKeyContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Lấy lịch sử thay đổi cấu hình
     * @param configId ID của cấu hình
     * @param pageable thông tin phân trang
     * @return lịch sử thay đổi
     */
    @Transactional(readOnly = true)
    public Page<ConfigurationAuditLog> getConfigurationHistory(Long configId, Pageable pageable) {
        log.info("Getting configuration history for ID: {}", configId);
        return auditLogRepository.findByConfigurationIdOrderByChangedAtDesc(configId, pageable);
    }

    /**
     * Validate request cấu hình
     * @param request request cần validate
     */
    private void validateConfigurationRequest(SystemConfigurationRequest request) {
        if (request.getConfigKey() == null || request.getConfigKey().trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration key cannot be empty");
        }
        
        if (request.getConfigValue() == null) {
            throw new IllegalArgumentException("Configuration value cannot be null");
        }
        
        if (request.getDataType() == null || request.getDataType().trim().isEmpty()) {
            throw new IllegalArgumentException("Data type cannot be empty");
        }

        // Validate data type format
        if (!isValidDataType(request.getDataType())) {
            throw new IllegalArgumentException("Invalid data type: " + request.getDataType());
        }

        // Validate value theo data type
        if (!isValidValueForDataType(request.getConfigValue(), request.getDataType())) {
            throw new IllegalArgumentException("Invalid value for data type " + request.getDataType());
        }
    }

    /**
     * Kiểm tra data type hợp lệ
     * @param dataType loại dữ liệu
     * @return true nếu hợp lệ
     */
    private boolean isValidDataType(String dataType) {
        return List.of("STRING", "INTEGER", "BOOLEAN", "DECIMAL", "JSON").contains(dataType.toUpperCase());
    }

    /**
     * Kiểm tra giá trị hợp lệ theo data type
     * @param value giá trị
     * @param dataType loại dữ liệu
     * @return true nếu hợp lệ
     */
    private boolean isValidValueForDataType(String value, String dataType) {
        try {
            switch (dataType.toUpperCase()) {
                case "INTEGER":
                    Integer.parseInt(value);
                    break;
                case "BOOLEAN":
                    Boolean.parseBoolean(value);
                    break;
                case "DECIMAL":
                    Double.parseDouble(value);
                    break;
                case "JSON":
                    // Basic JSON validation - should start with { or [
                    if (!value.trim().startsWith("{") && !value.trim().startsWith("[")) {
                        return false;
                    }
                    break;
                case "STRING":
                default:
                    // STRING luôn hợp lệ
                    break;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra quyền system admin
     * @param userId ID của user
     * @return true nếu là system admin
     */
    private boolean isSystemAdmin(Long userId) {
        // TODO: Implement logic kiểm tra quyền system admin
        // Tạm thời return true để test
        return true;
    }

    /**
     * Tạo audit log
     * @param configId ID cấu hình
     * @param userId ID user thực hiện
     * @param action hành động
     * @param oldValue giá trị cũ
     * @param newValue giá trị mới
     */
    private void createAuditLog(Long configId, Long userId, String action, String oldValue, String newValue) {
        ConfigurationAuditLog auditLog = ConfigurationAuditLog.builder()
                .configurationId(configId)
                .changedBy(userId)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedAt(LocalDateTime.now())
                .build();
        
        auditLogRepository.save(auditLog);
        log.debug("Audit log created for configuration ID: {} with action: {}", configId, action);
    }

    /**
     * Chuyển đổi entity sang response DTO
     * @param config entity cấu hình
     * @return response DTO
     */
    private SystemConfigurationResponse convertToResponse(SystemConfiguration config) {
        return SystemConfigurationResponse.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .description(config.getDescription())
                .category(config.getCategory())
                .dataType(config.getDataType())
                .isSystem(config.getIsSystem())
                .isActive(config.getIsActive())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}

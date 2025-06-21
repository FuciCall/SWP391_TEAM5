package com.gha.gender_healthcare_api.settings.service;

import com.gha.gender_healthcare_api.settings.entity.UserPreference;
import com.gha.gender_healthcare_api.settings.entity.ConfigurationAuditLog;
import com.gha.gender_healthcare_api.settings.repository.UserPreferenceRepository;
import com.gha.gender_healthcare_api.settings.repository.ConfigurationAuditLogRepository;
import com.gha.gender_healthcare_api.settings.dto.UserPreferenceUpdateRequest;
import com.gha.gender_healthcare_api.settings.dto.UserPreferenceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho User Preferences
 * 
 * Chức năng chính:
 * - Quản lý preferences của user (CRUD operations)
 * - Validate business rules cho preferences
 * - Tạo audit logs cho mọi thay đổi
 * - Cung cấp default preferences cho user mới
 * - Thống kê usage patterns
 * 
 * Tất cả operations đều được audit và logged để compliance
 */
@Service
@Slf4j
@Transactional
public class UserPreferenceService {
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    
    @Autowired
    private ConfigurationAuditLogRepository auditLogRepository;
    
    /**
     * Lấy preferences của user theo ID
     * Nếu user chưa có preferences thì tạo mới với default values
     * 
     * @param userId ID của user cần lấy preferences
     * @return UserPreferenceResponse chứa preferences của user
     */
    @Transactional(readOnly = true)
    public UserPreferenceResponse getUserPreferences(Long userId) {
        log.debug("Fetching preferences for user ID: {}", userId);
        
        Optional<UserPreference> existing = userPreferenceRepository.findByUserId(userId);
        
        if (existing.isPresent()) {
            log.debug("Found existing preferences for user ID: {}", userId);
            return UserPreferenceResponse.fromEntity(existing.get());
        } else {
            log.debug("No existing preferences found for user ID: {}, creating default", userId);
            return UserPreferenceResponse.createDefault(userId);
        }
    }
    
    /**
     * Cập nhật preferences của user
     * Chỉ cập nhật các fields không null trong request
     * Tạo audit log cho mọi thay đổi
     * 
     * @param userId ID của user cần cập nhật preferences
     * @param request DTO chứa các thay đổi cần cập nhật
     * @param changedBy ID của user thực hiện thay đổi (có thể là chính user hoặc admin)
     * @param ipAddress IP address của request
     * @param userAgent User agent của browser
     * @param sessionId ID của session hiện tại
     * @return UserPreferenceResponse sau khi cập nhật
     * @throws IllegalArgumentException nếu request không hợp lệ
     */
    public UserPreferenceResponse updateUserPreferences(Long userId, 
                                                       UserPreferenceUpdateRequest request,
                                                       Long changedBy,
                                                       String ipAddress,
                                                       String userAgent,
                                                       String sessionId) {
        log.info("Updating preferences for user ID: {} by user ID: {}", userId, changedBy);
        
        // Validate request
        if (!request.hasAnyFieldToUpdate()) {
            throw new IllegalArgumentException("No fields provided for update");
        }
        
        if (!request.isValid()) {
            throw new IllegalArgumentException("Invalid preference values provided");
        }
        
        // Lấy hoặc tạo preferences
        UserPreference preferences = userPreferenceRepository.findByUserId(userId)
                .orElse(createDefaultUserPreference(userId));
        
        // Lưu old values để audit
        UserPreference oldPreferences = cloneUserPreference(preferences);
        
        // Cập nhật các fields có trong request
        updatePreferenceFields(preferences, request);
        
        // Lưu vào database
        UserPreference savedPreferences = userPreferenceRepository.save(preferences);
        log.info("Successfully updated preferences for user ID: {}", userId);
        
        // Tạo audit logs cho các thay đổi
        createAuditLogsForChanges(oldPreferences, savedPreferences, changedBy, ipAddress, userAgent, sessionId);
        
        return UserPreferenceResponse.fromEntity(savedPreferences);
    }
    
    /**
     * Xóa preferences của user
     * Thực tế là soft delete - đặt về default values
     * 
     * @param userId ID của user cần xóa preferences
     * @param changedBy ID của user thực hiện thay đổi
     * @param ipAddress IP address của request
     * @param userAgent User agent của browser
     * @param sessionId ID của session hiện tại
     * @return true nếu xóa thành công
     */
    public boolean deleteUserPreferences(Long userId, 
                                       Long changedBy,
                                       String ipAddress,
                                       String userAgent,
                                       String sessionId) {
        log.info("Deleting preferences for user ID: {} by user ID: {}", userId, changedBy);
        
        Optional<UserPreference> existing = userPreferenceRepository.findByUserId(userId);
        if (existing.isPresent()) {
            UserPreference preferences = existing.get();
            
            // Tạo audit log
            createAuditLog(ConfigurationAuditLog.ConfigurationType.USER_PREFERENCE,
                          preferences.getId(),
                          "ALL_PREFERENCES",
                          "DELETED",
                          null,
                          ConfigurationAuditLog.ActionType.DELETE,
                          changedBy,
                          ipAddress,
                          userAgent,
                          sessionId,
                          "User preferences deleted");
            
            userPreferenceRepository.delete(preferences);
            log.info("Successfully deleted preferences for user ID: {}", userId);
            return true;
        }
        
        log.debug("No preferences found to delete for user ID: {}", userId);
        return false;
    }
    
    /**
     * Lấy thống kê usage của các preferences
     * 
     * @return Map chứa các thống kê
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPreferenceStatistics() {
        log.debug("Generating preference usage statistics");
        
        long totalUsers = userPreferenceRepository.count();
        long emailNotificationUsers = userPreferenceRepository.countUsersWithEmailNotifications();
        long twoFactorUsers = userPreferenceRepository.countUsersWithTwoFactor();
        
        long lightThemeUsers = userPreferenceRepository.countByThemePreference(UserPreference.ThemePreference.LIGHT);
        long darkThemeUsers = userPreferenceRepository.countByThemePreference(UserPreference.ThemePreference.DARK);
        
        return Map.of(
            "totalUsers", totalUsers,
            "emailNotificationUsers", emailNotificationUsers,
            "emailNotificationPercentage", totalUsers > 0 ? (emailNotificationUsers * 100.0 / totalUsers) : 0,
            "twoFactorUsers", twoFactorUsers,
            "twoFactorPercentage", totalUsers > 0 ? (twoFactorUsers * 100.0 / totalUsers) : 0,
            "lightThemeUsers", lightThemeUsers,
            "darkThemeUsers", darkThemeUsers,
            "lightThemePercentage", totalUsers > 0 ? (lightThemeUsers * 100.0 / totalUsers) : 0
        );
    }
    
    /**
     * Tìm users có bật email notifications (để gửi email)
     * 
     * @return danh sách user IDs có email notifications enabled
     */
    @Transactional(readOnly = true)
    public List<Long> getUsersWithEmailNotifications() {
        return userPreferenceRepository.findByEmailNotificationsEnabledTrue()
                .stream()
                .map(UserPreference::getUserId)
                .collect(Collectors.toList());
    }
    
    /**
     * Tìm users có bật SMS notifications (để gửi SMS)
     * 
     * @return danh sách user IDs có SMS notifications enabled
     */
    @Transactional(readOnly = true)
    public List<Long> getUsersWithSmsNotifications() {
        return userPreferenceRepository.findBySmsNotificationsEnabledTrue()
                .stream()
                .map(UserPreference::getUserId)
                .collect(Collectors.toList());
    }
    
    /**
     * Tìm users có bật push notifications (để gửi push)
     * 
     * @return danh sách user IDs có push notifications enabled
     */
    @Transactional(readOnly = true)
    public List<Long> getUsersWithPushNotifications() {
        return userPreferenceRepository.findByPushNotificationsEnabledTrue()
                .stream()
                .map(UserPreference::getUserId)
                .collect(Collectors.toList());
    }
    
    /**
     * Tạo UserPreference mới với default values
     * 
     * @param userId ID của user
     * @return UserPreference với default values
     */
    private UserPreference createDefaultUserPreference(Long userId) {
        return UserPreference.builder()
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
                .build();
    }
    
    /**
     * Clone UserPreference để lưu old values cho audit
     * 
     * @param original UserPreference gốc
     * @return UserPreference đã clone
     */
    private UserPreference cloneUserPreference(UserPreference original) {
        return UserPreference.builder()
                .id(original.getId())
                .userId(original.getUserId())
                .themePreference(original.getThemePreference())
                .languagePreference(original.getLanguagePreference())
                .timezone(original.getTimezone())
                .emailNotificationsEnabled(original.getEmailNotificationsEnabled())
                .smsNotificationsEnabled(original.getSmsNotificationsEnabled())
                .pushNotificationsEnabled(original.getPushNotificationsEnabled())
                .twoFactorEnabled(original.getTwoFactorEnabled())
                .sessionTimeoutMinutes(original.getSessionTimeoutMinutes())
                .publicProfileVisible(original.getPublicProfileVisible())
                .researchDataSharing(original.getResearchDataSharing())
                .marketingEmailsEnabled(original.getMarketingEmailsEnabled())
                .dateFormat(original.getDateFormat())
                .timeFormat(original.getTimeFormat())
                .measurementUnit(original.getMeasurementUnit())
                .createdAt(original.getCreatedAt())
                .updatedAt(original.getUpdatedAt())
                .build();
    }
    
    /**
     * Cập nhật các fields của preferences từ request
     * 
     * @param preferences UserPreference cần cập nhật
     * @param request DTO chứa các thay đổi
     */
    private void updatePreferenceFields(UserPreference preferences, UserPreferenceUpdateRequest request) {
        if (request.getThemePreference() != null) {
            preferences.setThemePreference(request.getThemePreference());
        }
        if (request.getLanguagePreference() != null) {
            preferences.setLanguagePreference(request.getLanguagePreference());
        }
        if (request.getTimezone() != null) {
            preferences.setTimezone(request.getTimezone());
        }
        if (request.getEmailNotificationsEnabled() != null) {
            preferences.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        }
        if (request.getSmsNotificationsEnabled() != null) {
            preferences.setSmsNotificationsEnabled(request.getSmsNotificationsEnabled());
        }
        if (request.getPushNotificationsEnabled() != null) {
            preferences.setPushNotificationsEnabled(request.getPushNotificationsEnabled());
        }
        if (request.getTwoFactorEnabled() != null) {
            preferences.setTwoFactorEnabled(request.getTwoFactorEnabled());
        }
        if (request.getSessionTimeoutMinutes() != null) {
            preferences.setSessionTimeoutMinutes(request.getSessionTimeoutMinutes());
        }
        if (request.getPublicProfileVisible() != null) {
            preferences.setPublicProfileVisible(request.getPublicProfileVisible());
        }
        if (request.getResearchDataSharing() != null) {
            preferences.setResearchDataSharing(request.getResearchDataSharing());
        }
        if (request.getMarketingEmailsEnabled() != null) {
            preferences.setMarketingEmailsEnabled(request.getMarketingEmailsEnabled());
        }
        if (request.getDateFormat() != null) {
            preferences.setDateFormat(request.getDateFormat());
        }
        if (request.getTimeFormat() != null) {
            preferences.setTimeFormat(request.getTimeFormat());
        }
        if (request.getMeasurementUnit() != null) {
            preferences.setMeasurementUnit(request.getMeasurementUnit());
        }
    }
    
    /**
     * Tạo audit logs cho các thay đổi
     * 
     * @param oldPreferences preferences trước khi thay đổi
     * @param newPreferences preferences sau khi thay đổi
     * @param changedBy ID của user thực hiện thay đổi
     * @param ipAddress IP address
     * @param userAgent User agent
     * @param sessionId Session ID
     */
    private void createAuditLogsForChanges(UserPreference oldPreferences, 
                                         UserPreference newPreferences,
                                         Long changedBy,
                                         String ipAddress,
                                         String userAgent,
                                         String sessionId) {
        
        // Kiểm tra từng field và tạo audit log nếu có thay đổi
        checkAndLogFieldChange(newPreferences.getId(), "themePreference", 
                              oldPreferences.getThemePreference(), newPreferences.getThemePreference(),
                              changedBy, ipAddress, userAgent, sessionId);
        
        checkAndLogFieldChange(newPreferences.getId(), "languagePreference",
                              oldPreferences.getLanguagePreference(), newPreferences.getLanguagePreference(),
                              changedBy, ipAddress, userAgent, sessionId);
        
        checkAndLogFieldChange(newPreferences.getId(), "timezone",
                              oldPreferences.getTimezone(), newPreferences.getTimezone(),
                              changedBy, ipAddress, userAgent, sessionId);
        
        checkAndLogFieldChange(newPreferences.getId(), "emailNotificationsEnabled",
                              oldPreferences.getEmailNotificationsEnabled(), newPreferences.getEmailNotificationsEnabled(),
                              changedBy, ipAddress, userAgent, sessionId);
        
        checkAndLogFieldChange(newPreferences.getId(), "smsNotificationsEnabled",
                              oldPreferences.getSmsNotificationsEnabled(), newPreferences.getSmsNotificationsEnabled(),
                              changedBy, ipAddress, userAgent, sessionId);
        
        checkAndLogFieldChange(newPreferences.getId(), "pushNotificationsEnabled",
                              oldPreferences.getPushNotificationsEnabled(), newPreferences.getPushNotificationsEnabled(),
                              changedBy, ipAddress, userAgent, sessionId);
        
        checkAndLogFieldChange(newPreferences.getId(), "twoFactorEnabled",
                              oldPreferences.getTwoFactorEnabled(), newPreferences.getTwoFactorEnabled(),
                              changedBy, ipAddress, userAgent, sessionId);
        
        checkAndLogFieldChange(newPreferences.getId(), "sessionTimeoutMinutes",
                              oldPreferences.getSessionTimeoutMinutes(), newPreferences.getSessionTimeoutMinutes(),
                              changedBy, ipAddress, userAgent, sessionId);
        
        // ... tiếp tục cho các fields khác
    }
    
    /**
     * Kiểm tra và log thay đổi của một field
     */
    private void checkAndLogFieldChange(Long recordId, String fieldName, Object oldValue, Object newValue,
                                      Long changedBy, String ipAddress, String userAgent, String sessionId) {
        if (!java.util.Objects.equals(oldValue, newValue)) {
            createAuditLog(ConfigurationAuditLog.ConfigurationType.USER_PREFERENCE,
                          recordId,
                          fieldName,
                          oldValue != null ? oldValue.toString() : null,
                          newValue != null ? newValue.toString() : null,
                          ConfigurationAuditLog.ActionType.UPDATE,
                          changedBy,
                          ipAddress,
                          userAgent,
                          sessionId,
                          "User preference updated");
        }
    }
    
    /**
     * Tạo audit log entry
     */
    private void createAuditLog(ConfigurationAuditLog.ConfigurationType configurationType,
                               Long configRecordId,
                               String fieldName,
                               String oldValue,
                               String newValue,
                               ConfigurationAuditLog.ActionType actionType,
                               Long changedBy,
                               String ipAddress,
                               String userAgent,
                               String sessionId,
                               String changeReason) {
        
        ConfigurationAuditLog auditLog = ConfigurationAuditLog.builder()
                .configurationType(configurationType)
                .configRecordId(configRecordId)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .actionType(actionType)
                .changedBy(changedBy)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .sessionId(sessionId)
                .changeReason(changeReason)
                .isSuccessful(true)
                .build();
        
        auditLogRepository.save(auditLog);
        log.debug("Created audit log for {} change on field {}", configurationType, fieldName);
    }
}

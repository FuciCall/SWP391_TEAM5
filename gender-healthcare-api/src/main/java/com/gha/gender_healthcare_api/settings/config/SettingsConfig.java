package com.gha.gender_healthcare_api.settings.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

/**
 * Cấu hình cho module Settings & Configuration
 * Bao gồm caching, scheduling và các cấu hình khác
 */
@Configuration
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@Slf4j
public class SettingsConfig {

    /**
     * Cấu hình Cache Manager cho việc cache cấu hình hệ thống
     * Sử dụng ConcurrentMapCacheManager cho môi trường đơn giản
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Configuring cache manager for Settings module");
        
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Định nghĩa các cache được sử dụng trong module settings
        cacheManager.setCacheNames(java.util.List.of(
            "systemConfig",           // Cache cho system configuration
            "systemConfigValue",      // Cache cho giá trị configuration
            "userPreferences",        // Cache cho user preferences
            "preferenceStatistics"    // Cache cho thống kê preferences
        ));
        
        // Cho phép tạo cache động
        cacheManager.setAllowNullValues(false);
        
        log.info("Cache manager configured with caches: {}", cacheManager.getCacheNames());
        return cacheManager;
    }

    /**
     * Cấu hình các hằng số cho module Settings
     */
    public static class SettingsConstants {
        
        // Default values cho user preferences
        public static final String DEFAULT_LANGUAGE = "vi";
        public static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
        public static final Boolean DEFAULT_EMAIL_NOTIFICATIONS = true;
        public static final Boolean DEFAULT_SMS_NOTIFICATIONS = false;
        public static final Boolean DEFAULT_PUSH_NOTIFICATIONS = true;
        public static final String DEFAULT_THEME = "light";
        public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
        public static final String DEFAULT_TIME_FORMAT = "HH:mm";
        
        // System configuration categories
        public static final String CATEGORY_GENERAL = "GENERAL";
        public static final String CATEGORY_NOTIFICATION = "NOTIFICATION";
        public static final String CATEGORY_SECURITY = "SECURITY";
        public static final String CATEGORY_INTEGRATION = "INTEGRATION";
        public static final String CATEGORY_UI = "UI";
        public static final String CATEGORY_BUSINESS = "BUSINESS";
        
        // Data types cho system configuration
        public static final String DATA_TYPE_STRING = "STRING";
        public static final String DATA_TYPE_INTEGER = "INTEGER";
        public static final String DATA_TYPE_BOOLEAN = "BOOLEAN";
        public static final String DATA_TYPE_DECIMAL = "DECIMAL";
        public static final String DATA_TYPE_JSON = "JSON";
        
        // Preference keys
        public static final String PREF_LANGUAGE = "language";
        public static final String PREF_TIMEZONE = "timezone";
        public static final String PREF_EMAIL_NOTIFICATIONS = "email_notifications";
        public static final String PREF_SMS_NOTIFICATIONS = "sms_notifications";
        public static final String PREF_PUSH_NOTIFICATIONS = "push_notifications";
        public static final String PREF_THEME = "theme";
        public static final String PREF_DATE_FORMAT = "date_format";
        public static final String PREF_TIME_FORMAT = "time_format";
        public static final String PREF_CYCLE_REMINDER_ENABLED = "cycle_reminder_enabled";
        public static final String PREF_CONTRACEPTIVE_REMINDER_ENABLED = "contraceptive_reminder_enabled";
        public static final String PREF_PRIVACY_MODE = "privacy_mode";
        
        // System configuration keys
        public static final String SYS_CONFIG_MAX_LOGIN_ATTEMPTS = "max_login_attempts";
        public static final String SYS_CONFIG_SESSION_TIMEOUT = "session_timeout_minutes";
        public static final String SYS_CONFIG_PASSWORD_MIN_LENGTH = "password_min_length";
        public static final String SYS_CONFIG_EMAIL_ENABLED = "email_service_enabled";
        public static final String SYS_CONFIG_SMS_ENABLED = "sms_service_enabled";
        public static final String SYS_CONFIG_MAINTENANCE_MODE = "maintenance_mode";
        public static final String SYS_CONFIG_MAX_FILE_SIZE = "max_file_size_mb";
        public static final String SYS_CONFIG_ALLOWED_FILE_TYPES = "allowed_file_types";
        
        // Validation constraints
        public static final int MAX_PREFERENCE_KEY_LENGTH = 100;
        public static final int MAX_PREFERENCE_VALUE_LENGTH = 1000;
        public static final int MAX_CONFIG_KEY_LENGTH = 100;
        public static final int MAX_CONFIG_VALUE_LENGTH = 2000;
        public static final int MAX_DESCRIPTION_LENGTH = 500;
        public static final int MAX_CATEGORY_LENGTH = 50;
        
        // Cache TTL (Time To Live) in seconds
        public static final long CACHE_TTL_SYSTEM_CONFIG = 3600; // 1 hour
        public static final long CACHE_TTL_USER_PREFERENCES = 1800; // 30 minutes
        public static final long CACHE_TTL_STATISTICS = 300; // 5 minutes
    }

    /**
     * Cấu hình validation patterns
     */
    public static class ValidationPatterns {
        
        // Pattern cho config key (chỉ cho phép chữ, số, underscore, dấu chấm)
        public static final String CONFIG_KEY_PATTERN = "^[a-zA-Z0-9_.]+$";
        
        // Pattern cho preference key
        public static final String PREFERENCE_KEY_PATTERN = "^[a-zA-Z0-9_]+$";
        
        // Pattern cho timezone
        public static final String TIMEZONE_PATTERN = "^[A-Za-z_/]+$";
        
        // Pattern cho language code (ISO 639-1)
        public static final String LANGUAGE_PATTERN = "^[a-z]{2}$";
        
        // Pattern cho theme name
        public static final String THEME_PATTERN = "^[a-zA-Z0-9_-]+$";
        
        // Pattern cho date format
        public static final String DATE_FORMAT_PATTERN = "^[dMy/.-]+$";
        
        // Pattern cho time format
        public static final String TIME_FORMAT_PATTERN = "^[Hms:]+$";
    }

    /**
     * Cấu hình các message templates
     */
    public static class MessageTemplates {
        
        // Success messages
        public static final String MSG_PREFERENCE_UPDATED = "User preferences updated successfully";
        public static final String MSG_PREFERENCE_RESET = "User preferences reset to default successfully";
        public static final String MSG_CONFIG_CREATED = "System configuration created successfully";
        public static final String MSG_CONFIG_UPDATED = "System configuration updated successfully";
        public static final String MSG_CONFIG_DELETED = "System configuration deleted successfully";
        
        // Error messages
        public static final String ERR_PREFERENCE_NOT_FOUND = "User preference not found";
        public static final String ERR_CONFIG_NOT_FOUND = "System configuration not found";
        public static final String ERR_CONFIG_KEY_EXISTS = "Configuration key already exists";
        public static final String ERR_INVALID_DATA_TYPE = "Invalid data type for configuration value";
        public static final String ERR_SYSTEM_CONFIG_READONLY = "System configurations cannot be modified";
        public static final String ERR_UNAUTHORIZED = "Unauthorized access to configuration";
        public static final String ERR_INVALID_VALUE = "Invalid value for configuration";
    }
}

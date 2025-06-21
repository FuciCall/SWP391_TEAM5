-- =====================================================
-- Settings & Configuration Module - Database Schema
-- =====================================================
-- Tạo các bảng cho module Settings & Configuration
-- Bao gồm: User Preferences, System Configurations, Audit Logs

-- =====================================================
-- 1. Bảng User Preferences
-- =====================================================
-- Lưu trữ tất cả các tùy chọn cá nhân của user
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    
    -- Giao diện và hiển thị
    theme_preference ENUM('LIGHT', 'DARK', 'AUTO') DEFAULT 'LIGHT',
    language_preference ENUM('EN', 'VI', 'AUTO') DEFAULT 'EN',
    timezone VARCHAR(50) DEFAULT 'Asia/Ho_Chi_Minh',
    date_format VARCHAR(20) DEFAULT 'DD/MM/YYYY',
    time_format VARCHAR(10) DEFAULT '24H',
    measurement_unit ENUM('METRIC', 'IMPERIAL') DEFAULT 'METRIC',
    
    -- Thông báo
    email_notifications_enabled BOOLEAN DEFAULT TRUE,
    sms_notifications_enabled BOOLEAN DEFAULT FALSE,
    push_notifications_enabled BOOLEAN DEFAULT TRUE,
    marketing_emails_enabled BOOLEAN DEFAULT FALSE,
    
    -- Bảo mật
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    session_timeout_minutes INT DEFAULT 30 CHECK (session_timeout_minutes BETWEEN 15 AND 120),
    
    -- Quyền riêng tư
    public_profile_visible BOOLEAN DEFAULT FALSE,
    research_data_sharing BOOLEAN DEFAULT FALSE,
    
    -- Thời gian
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Khóa ngoại (sẽ được tạo khi có bảng users)
    -- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_user_preferences_user_id (user_id),
    INDEX idx_user_preferences_theme (theme_preference),
    INDEX idx_user_preferences_language (language_preference),
    INDEX idx_user_preferences_notifications (email_notifications_enabled, sms_notifications_enabled, push_notifications_enabled),
    INDEX idx_user_preferences_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. Bảng System Configurations
-- =====================================================
-- Lưu trữ cấu hình toàn hệ thống
CREATE TABLE system_configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(500),
    config_group VARCHAR(50),
    
    -- Metadata
    data_type ENUM('STRING', 'INTEGER', 'BOOLEAN', 'JSON', 'ENCRYPTED') NOT NULL,
    is_sensitive BOOLEAN DEFAULT FALSE,
    is_editable BOOLEAN DEFAULT TRUE,
    requires_restart BOOLEAN DEFAULT FALSE,
    
    -- Validation
    default_value TEXT,
    validation_rule VARCHAR(200),
    
    -- Thời gian hiệu lực
    effective_from TIMESTAMP NULL,
    effective_to TIMESTAMP NULL,
    
    -- Audit
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Khóa ngoại (sẽ được tạo khi có bảng users)
    -- FOREIGN KEY (created_by) REFERENCES users(id),
    -- FOREIGN KEY (updated_by) REFERENCES users(id),
    
    -- Indexes
    INDEX idx_system_config_key (config_key),
    INDEX idx_system_config_group (config_group),
    INDEX idx_system_config_type (data_type),
    INDEX idx_system_config_sensitive (is_sensitive),
    INDEX idx_system_config_editable (is_editable),
    INDEX idx_system_config_effective (effective_from, effective_to),
    INDEX idx_system_config_created_by (created_by),
    INDEX idx_system_config_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. Bảng Configuration Audit Logs
-- =====================================================
-- Lưu trữ lịch sử thay đổi cấu hình
CREATE TABLE configuration_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Thông tin cấu hình được thay đổi
    configuration_type ENUM('USER_PREFERENCE', 'SYSTEM_CONFIGURATION') NOT NULL,
    config_record_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    
    -- Giá trị thay đổi
    old_value TEXT,
    new_value TEXT,
    
    -- Thông tin hành động
    action_type ENUM('CREATE', 'UPDATE', 'DELETE') NOT NULL,
    changed_by BIGINT NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Thông tin session
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    session_id VARCHAR(100),
    
    -- Metadata
    change_reason VARCHAR(500),
    is_successful BOOLEAN DEFAULT TRUE,
    error_message VARCHAR(1000),
    
    -- Khóa ngoại (sẽ được tạo khi có bảng users)
    -- FOREIGN KEY (changed_by) REFERENCES users(id),
    
    -- Indexes
    INDEX idx_audit_config_type (configuration_type),
    INDEX idx_audit_record_id (config_record_id),
    INDEX idx_audit_field_name (field_name),
    INDEX idx_audit_action_type (action_type),
    INDEX idx_audit_changed_by (changed_by),
    INDEX idx_audit_changed_at (changed_at),
    INDEX idx_audit_session (session_id),
    INDEX idx_audit_ip (ip_address),
    INDEX idx_audit_successful (is_successful),
    INDEX idx_audit_composite (configuration_type, config_record_id, changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. Dữ liệu mẫu cho System Configurations
-- =====================================================

-- Email Configuration
INSERT INTO system_configurations (config_key, config_value, description, config_group, data_type, is_sensitive, default_value) VALUES
('email.smtp.host', 'smtp.gmail.com', 'SMTP server hostname for sending emails', 'EMAIL', 'STRING', FALSE, 'localhost'),
('email.smtp.port', '587', 'SMTP server port number', 'EMAIL', 'INTEGER', FALSE, '25'),
('email.smtp.username', '', 'SMTP authentication username', 'EMAIL', 'STRING', TRUE, ''),
('email.smtp.password', '', 'SMTP authentication password', 'EMAIL', 'ENCRYPTED', TRUE, ''),
('email.smtp.tls.enabled', 'true', 'Enable TLS for SMTP connection', 'EMAIL', 'BOOLEAN', FALSE, 'false'),
('email.from.address', 'noreply@genderhealthcare.com', 'Default from email address', 'EMAIL', 'STRING', FALSE, 'noreply@localhost'),
('email.from.name', 'Gender Healthcare Services', 'Default from email name', 'EMAIL', 'STRING', FALSE, 'System');

-- SMS Configuration
INSERT INTO system_configurations (config_key, config_value, description, config_group, data_type, is_sensitive, default_value) VALUES
('sms.gateway.provider', 'TWILIO', 'SMS gateway provider (TWILIO, NEXMO, etc.)', 'SMS', 'STRING', FALSE, 'NONE'),
('sms.twilio.account.sid', '', 'Twilio Account SID', 'SMS', 'STRING', TRUE, ''),
('sms.twilio.auth.token', '', 'Twilio Auth Token', 'SMS', 'ENCRYPTED', TRUE, ''),
('sms.twilio.phone.number', '', 'Twilio phone number for sending SMS', 'SMS', 'STRING', FALSE, ''),
('sms.enabled', 'false', 'Enable SMS notifications globally', 'SMS', 'BOOLEAN', FALSE, 'false');

-- Security Configuration
INSERT INTO system_configurations (config_key, config_value, description, config_group, data_type, is_sensitive, default_value) VALUES
('security.jwt.secret', '', 'JWT signing secret key', 'SECURITY', 'ENCRYPTED', TRUE, ''),
('security.jwt.expiration', '86400', 'JWT token expiration time in seconds (24 hours)', 'SECURITY', 'INTEGER', FALSE, '3600'),
('security.password.min.length', '8', 'Minimum password length', 'SECURITY', 'INTEGER', FALSE, '6'),
('security.password.require.uppercase', 'true', 'Require uppercase letter in password', 'SECURITY', 'BOOLEAN', FALSE, 'false'),
('security.password.require.lowercase', 'true', 'Require lowercase letter in password', 'SECURITY', 'BOOLEAN', FALSE, 'false'),
('security.password.require.numbers', 'true', 'Require numbers in password', 'SECURITY', 'BOOLEAN', FALSE, 'false'),
('security.password.require.special', 'true', 'Require special characters in password', 'SECURITY', 'BOOLEAN', FALSE, 'false'),
('security.login.max.attempts', '5', 'Maximum login attempts before account lock', 'SECURITY', 'INTEGER', FALSE, '3'),
('security.session.timeout.minutes', '30', 'Default session timeout in minutes', 'SECURITY', 'INTEGER', FALSE, '15');

-- Business Configuration
INSERT INTO system_configurations (config_key, config_value, description, config_group, data_type, is_sensitive, default_value) VALUES
('business.appointment.slot.duration', '30', 'Default appointment slot duration in minutes', 'BUSINESS', 'INTEGER', FALSE, '60'),
('business.appointment.advance.booking.days', '90', 'Maximum days in advance for booking appointments', 'BUSINESS', 'INTEGER', FALSE, '30'),
('business.appointment.cancellation.hours', '24', 'Minimum hours before appointment for free cancellation', 'BUSINESS', 'INTEGER', FALSE, '12'),
('business.working.hours.start', '08:00', 'Business working hours start time', 'BUSINESS', 'STRING', FALSE, '09:00'),
('business.working.hours.end', '18:00', 'Business working hours end time', 'BUSINESS', 'STRING', FALSE, '17:00'),
('business.working.days', '["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"]', 'Business working days', 'BUSINESS', 'JSON', FALSE, '["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]');

-- Application Configuration
INSERT INTO system_configurations (config_key, config_value, description, config_group, data_type, is_sensitive, default_value) VALUES
('app.name', 'Gender Healthcare Service Management System', 'Application display name', 'APPLICATION', 'STRING', FALSE, 'Healthcare System'),
('app.version', '1.0.0', 'Current application version', 'APPLICATION', 'STRING', FALSE, '1.0.0'),
('app.environment', 'DEVELOPMENT', 'Current environment (DEVELOPMENT, STAGING, PRODUCTION)', 'APPLICATION', 'STRING', FALSE, 'DEVELOPMENT'),
('app.maintenance.mode', 'false', 'Enable maintenance mode', 'APPLICATION', 'BOOLEAN', FALSE, 'false'),
('app.maintenance.message', 'System is under maintenance. Please try again later.', 'Maintenance mode message', 'APPLICATION', 'STRING', FALSE, 'System maintenance in progress'),
('app.max.file.upload.size', '10485760', 'Maximum file upload size in bytes (10MB)', 'APPLICATION', 'INTEGER', FALSE, '5242880'),
('app.supported.file.types', '["jpg","jpeg","png","pdf","doc","docx"]', 'Supported file types for upload', 'APPLICATION', 'JSON', FALSE, '["jpg","png","pdf"]');

-- Notification Configuration
INSERT INTO system_configurations (config_key, config_value, description, config_group, data_type, is_sensitive, default_value) VALUES
('notification.email.enabled', 'true', 'Enable email notifications globally', 'NOTIFICATION', 'BOOLEAN', FALSE, 'true'),
('notification.sms.enabled', 'false', 'Enable SMS notifications globally', 'NOTIFICATION', 'BOOLEAN', FALSE, 'false'),
('notification.push.enabled', 'true', 'Enable push notifications globally', 'NOTIFICATION', 'BOOLEAN', FALSE, 'false'),
('notification.appointment.reminder.hours', '24,2', 'Hours before appointment to send reminders (comma-separated)', 'NOTIFICATION', 'STRING', FALSE, '24'),
('notification.appointment.confirmation.enabled', 'true', 'Send appointment confirmation notifications', 'NOTIFICATION', 'BOOLEAN', FALSE, 'true');

-- =====================================================
-- 5. Comments và Documentation
-- =====================================================

-- Bảng user_preferences:
-- - Lưu trữ tất cả preferences của từng user
-- - Mỗi user chỉ có 1 record duy nhất (UNIQUE constraint trên user_id)
-- - Các giá trị mặc định được thiết lập phù hợp cho người dùng Việt Nam
-- - Support multiple themes, languages, timezones
-- - Có validation constraints cho session timeout

-- Bảng system_configurations:
-- - Lưu trữ cấu hình toàn hệ thống theo pattern key-value
-- - Support nhiều data types: STRING, INTEGER, BOOLEAN, JSON, ENCRYPTED
-- - Có cơ chế effective_from/effective_to để schedule configuration changes
-- - Sensitive configs sẽ được encrypt trước khi lưu
-- - Có validation rules để verify giá trị hợp lệ

-- Bảng configuration_audit_logs:
-- - Track tất cả thay đổi của user preferences và system configurations
-- - Lưu both old và new values để có thể rollback
-- - Track metadata: IP, User Agent, Session ID, Change Reason
-- - Support audit trail cho compliance requirements

-- =====================================================
-- 6. Indexes và Performance
-- =====================================================

-- Các indexes đã được tối ưu cho:
-- - Lookup theo user_id (user_preferences)
-- - Lookup theo config_key (system_configurations)
-- - Audit trail queries (configuration_audit_logs)
-- - Statistics và reporting queries
-- - Time-based queries (effective dates, change timestamps)

-- =====================================================
-- 7. Future Enhancements
-- =====================================================

-- Có thể mở rộng thêm:
-- - Bảng user_preference_templates (để admin tạo preset preferences)
-- - Bảng configuration_approvals (workflow approval cho sensitive configs)
-- - Bảng configuration_backups (backup configs trước khi thay đổi)
-- - Partitioning cho audit_logs table khi data lớn
-- - Encryption key rotation cho sensitive configs

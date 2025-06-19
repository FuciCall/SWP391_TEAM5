-- SQL Script to create Reproductive Cycle Tracking tables
-- This is optional - JPA will auto-create tables if using ddl-auto=update

-- Menstrual Cycles table
CREATE TABLE menstrual_cycles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    cycle_length INT,
    period_length INT,
    flow_intensity ENUM('LIGHT', 'NORMAL', 'HEAVY'),
    symptoms TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_start_date (user_id, start_date DESC)
);

-- Contraceptive Reminders table  
CREATE TABLE contraceptive_reminders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pill_name VARCHAR(100) NOT NULL,
    reminder_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    pack_start_date DATE,
    pack_duration INT,
    break_duration INT,
    timezone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_active (user_id, is_active)
);

-- Cycle Predictions table
CREATE TABLE cycle_predictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    predicted_ovulation_date DATE,
    fertile_window_start DATE,
    fertile_window_end DATE,
    next_period_date DATE,
    pregnancy_likelihood DOUBLE,
    calculation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_calculation_date (user_id, calculation_date DESC)
);

-- Cycle Notifications table
CREATE TABLE cycle_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('OVULATION_REMINDER', 'PERIOD_REMINDER', 'CONTRACEPTIVE_PILL_REMINDER', 'FERTILITY_WINDOW', 'PERIOD_LATE_WARNING') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    scheduled_time TIMESTAMP,
    sent_time TIMESTAMP NULL,
    is_read BOOLEAN DEFAULT FALSE,
    is_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_unread (user_id, is_read),
    INDEX idx_scheduled_unsent (scheduled_time, is_sent)
);

-- Sample data (optional)
-- INSERT INTO menstrual_cycles (user_id, start_date, end_date, flow_intensity, notes) 
-- VALUES (1, '2025-05-15', '2025-05-20', 'NORMAL', 'First cycle recorded');

-- INSERT INTO contraceptive_reminders (user_id, pill_name, reminder_time, pack_start_date, pack_duration, break_duration, timezone)
-- VALUES (1, 'Yasmin', '08:00:00', '2025-06-01', 21, 7, 'Asia/Ho_Chi_Minh');

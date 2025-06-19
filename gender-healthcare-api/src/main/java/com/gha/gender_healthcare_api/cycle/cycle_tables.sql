-- =====================================================================================
-- SQL Script for Reproductive Cycle Tracking System
-- =====================================================================================
-- Tạo 4 bảng chính cho hệ thống theo dõi chu kỳ sinh sản:
-- 1. menstrual_cycles: Lưu dữ liệu chu kỳ kinh nguyệt của user
-- 2. contraceptive_reminders: Quản lý nhắc nhở thuốc tránh thai
-- 3. cycle_predictions: Lưu dự đoán chu kỳ và thông tin sinh sản
-- 4. cycle_notifications: Hệ thống thông báo tự động
--
-- Lưu ý: Script này là optional - JPA sẽ tự động tạo bảng nếu sử dụng ddl-auto=update
-- =====================================================================================

-- =====================================================================================
-- Bảng 1: MENSTRUAL_CYCLES - Chu kỳ kinh nguyệt
-- =====================================================================================
-- Mục đích: Lưu trữ thông tin chi tiết về từng chu kỳ kinh nguyệt của user
-- Quan hệ: Many-to-One với bảng users (một user có nhiều cycles)
-- Sử dụng: Tính toán dự đoán, phân tích thống kê, đưa ra lời khuyên sức khỏe
-- =====================================================================================
CREATE TABLE menstrual_cycles (
    -- Primary key tự động tăng
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign key đến bảng users - NOT NULL (bắt buộc)
    -- Đảm bảo mọi cycle đều thuộc về một user cụ thể
    user_id BIGINT NOT NULL,
    
    -- Ngày bắt đầu chu kỳ kinh nguyệt - NOT NULL (bắt buộc)
    -- Là ngày đầu tiên của chu kỳ, dùng làm mốc tính toán
    start_date DATE NOT NULL,
    
    -- Ngày kết thúc chu kỳ kinh nguyệt - có thể NULL
    -- User có thể không nhập ngày kết thúc hoặc update sau
    end_date DATE,
    
    -- Độ dài chu kỳ tính bằng ngày - được tính tự động
    -- Khoảng cách từ start_date của chu kỳ này đến start_date của chu kỳ tiếp theo
    -- Giá trị bình thường: 21-35 ngày
    cycle_length INT,
    
    -- Độ dài kinh nguyệt tính bằng ngày - được tính từ start_date và end_date
    -- Số ngày từ bắt đầu đến kết thúc kinh nguyệt
    -- Giá trị bình thường: 3-7 ngày
    period_length INT,
    
    -- Cường độ kinh nguyệt - ENUM với 3 mức độ
    -- LIGHT: Ít, NORMAL: Bình thường, HEAVY: Nhiều
    -- Dùng để đánh giá sức khỏe và đưa ra cảnh báo
    flow_intensity ENUM('LIGHT', 'NORMAL', 'HEAVY'),
    
    -- Các triệu chứng kèm theo - lưu dưới dạng TEXT
    -- Có thể là JSON string: "cramps,bloating,mood_swings"
    -- Cho phép user track các triệu chứng để tìm pattern
    symptoms TEXT,
    
    -- Ghi chú cá nhân của user - TEXT không giới hạn
    -- User có thể ghi chú về stress, thuốc, thay đổi lối sống...
    notes TEXT,
    
    -- Timestamp tự động khi tạo record mới
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Timestamp tự động update khi modify record
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraint với CASCADE DELETE
    -- Khi xóa user sẽ tự động xóa tất cả cycles của user đó
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Index tối ưu cho query theo user_id và start_date (DESC)
    -- Hỗ trợ việc lấy cycles mới nhất của user một cách nhanh chóng
    INDEX idx_user_start_date (user_id, start_date DESC)
);

-- =====================================================================================
-- Bảng 2: CONTRACEPTIVE_REMINDERS - Nhắc nhở thuốc tránh thai
-- =====================================================================================
-- Mục đích: Quản lý cài đặt nhắc nhở uống thuốc tránh thai hàng ngày
-- Quan hệ: Many-to-One với bảng users (một user có thể có nhiều reminders, nhưng chỉ 1 active)
-- Sử dụng: Tạo notifications hàng ngày, track progress của pack thuốc
-- =====================================================================================
CREATE TABLE contraceptive_reminders (
    -- Primary key tự động tăng
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign key đến bảng users
    user_id BIGINT NOT NULL,
    
    -- Tên thuốc tránh thai - VARCHAR 100 ký tự
    -- Ví dụ: "Yasmin", "Diane-35", "Mercilon"
    -- Hiển thị trong notification để user dễ nhận biết
    pill_name VARCHAR(100) NOT NULL,
    
    -- Thời gian nhắc nhở hàng ngày - TIME format (HH:MM)
    -- Ví dụ: "08:00", "20:30"
    -- Sẽ được sử dụng để schedule notification
    reminder_time TIME NOT NULL,
    
    -- Trạng thái hoạt động - BOOLEAN với default TRUE
    -- Chỉ có 1 reminder active per user tại một thời điểm
    -- FALSE khi user tắt hoặc setup reminder mới
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Ngày bắt đầu pack thuốc mới - DATE
    -- Dùng để tính toán ngày uống thuốc và ngày nghỉ
    pack_start_date DATE,
    
    -- Số ngày uống thuốc trong 1 pack - INT
    -- Thường là 21 (pack 21 viên) hoặc 28 (pack 28 viên)
    pack_duration INT,
    
    -- Số ngày nghỉ giữa các pack - INT
    -- 7 ngày cho pack 21 viên, 0 ngày cho pack 28 viên (uống liên tục)
    break_duration INT,
    
    -- Timezone của user - VARCHAR 50
    -- Ví dụ: "Asia/Ho_Chi_Minh", "UTC", "America/New_York"
    -- Đảm bảo tính toán thời gian nhắc nhở chính xác
    timezone VARCHAR(50),
    
    -- Timestamp tự động khi tạo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint với CASCADE DELETE
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Index tối ưu cho query reminder active của user
    INDEX idx_user_active (user_id, is_active)
);

-- =====================================================================================
-- Bảng 3: CYCLE_PREDICTIONS - Dự đoán chu kỳ sinh sản
-- =====================================================================================
-- Mục đích: Lưu trữ kết quả dự đoán chu kỳ kinh nguyệt và thông tin sinh sản
-- Quan hệ: Many-to-One với bảng users (một user có nhiều predictions theo thời gian)
-- Sử dụng: Hiển thị dự đoán trong UI, lên lịch notifications, kế hoạch hóa gia đình
-- =====================================================================================
CREATE TABLE cycle_predictions (
    -- Primary key tự động tăng
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign key đến bảng users
    user_id BIGINT NOT NULL,
    
    -- Ngày rụng trứng dự đoán - DATE
    -- Tính theo công thức: next_period_date - 14 ngày (luteal phase)
    -- Quan trọng nhất cho kế hoạch hóa gia đình
    predicted_ovulation_date DATE,
    
    -- Ngày bắt đầu cửa sổ sinh sản - DATE
    -- Tính từ: predicted_ovulation_date - 5 ngày
    -- Dựa trên thời gian sống của tinh trùng (5 ngày)
    fertile_window_start DATE,
    
    -- Ngày kết thúc cửa sổ sinh sản - DATE
    -- Tính từ: predicted_ovulation_date + 1 ngày
    -- Dựa trên thời gian sống của trứng (24 giờ)
    fertile_window_end DATE,
    
    -- Ngày kinh nguyệt tiếp theo dự đoán - DATE
    -- Tính từ: last_period_start + average_cycle_length
    -- Cơ sở để tính toán các dự đoán khác
    next_period_date DATE,
    
    -- Khả năng có thai tính theo phần trăm - DOUBLE (0-100)
    -- Dựa trên vị trí hiện tại trong cửa sổ sinh sản
    -- 0%: Ngoài cửa sổ, 10-25%: Trong cửa sổ (cao nhất ngày rụng trứng)
    pregnancy_likelihood DOUBLE,
    
    -- Thời gian tính toán prediction - TIMESTAMP
    -- Tự động set khi tạo, dùng để lấy prediction mới nhất
    calculation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint với CASCADE DELETE
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Index tối ưu cho query prediction mới nhất của user
    INDEX idx_user_calculation_date (user_id, calculation_date DESC)
);

-- =====================================================================================
-- Bảng 4: CYCLE_NOTIFICATIONS - Hệ thống thông báo tự động
-- =====================================================================================
-- Mục đích: Quản lý các thông báo tự động về chu kỳ sinh sản
-- Quan hệ: Many-to-One với bảng users
-- Sử dụng: Scheduler gửi email/push notifications, notification center trong UI
-- =====================================================================================
CREATE TABLE cycle_notifications (
    -- Primary key tự động tăng
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign key đến bảng users
    user_id BIGINT NOT NULL,
    
    -- Loại thông báo - ENUM với 5 loại chính
    -- OVULATION_REMINDER: Nhắc nhở rụng trứng (1 ngày trước)
    -- PERIOD_REMINDER: Nhắc nhở kinh nguyệt sắp đến (2 ngày trước)
    -- CONTRACEPTIVE_PILL_REMINDER: Nhắc nhở uống thuốc (hàng ngày)
    -- FERTILITY_WINDOW: Thông báo cửa sổ sinh sản bắt đầu
    -- PERIOD_LATE_WARNING: Cảnh báo kinh nguyệt trễ (nếu cần)
    type ENUM('OVULATION_REMINDER', 'PERIOD_REMINDER', 'CONTRACEPTIVE_PILL_REMINDER', 'FERTILITY_WINDOW', 'PERIOD_LATE_WARNING') NOT NULL,
    
    -- Tiêu đề thông báo - VARCHAR 255
    -- Ví dụ: "Ovulation Tomorrow", "Time for your pill!"
    title VARCHAR(255) NOT NULL,
    
    -- Nội dung chi tiết thông báo - TEXT
    -- Có thể chứa lời khuyên sức khỏe và thông tin bổ sung
    message TEXT,
    
    -- Thời gian được lên lịch để gửi - TIMESTAMP
    -- Scheduler sẽ kiểm tra và gửi notification đến thời gian này
    scheduled_time TIMESTAMP,
    
    -- Thời gian thực tế đã gửi - TIMESTAMP (có thể NULL)
    -- NULL nếu chưa gửi, có giá trị khi đã gửi thành công
    sent_time TIMESTAMP NULL,
    
    -- Trạng thái đã đọc - BOOLEAN với default FALSE
    -- Dùng để hiển thị badge số notification chưa đọc trong UI
    is_read BOOLEAN DEFAULT FALSE,
    
    -- Trạng thái đã gửi - BOOLEAN với default FALSE
    -- Scheduler chỉ xử lý những notification có is_sent = FALSE
    is_sent BOOLEAN DEFAULT FALSE,
    
    -- Timestamp tự động khi tạo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint với CASCADE DELETE
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Index tối ưu cho query notification chưa đọc của user
    INDEX idx_user_unread (user_id, is_read),
    
    -- Index tối ưu cho scheduler tìm pending notifications
    INDEX idx_scheduled_unsent (scheduled_time, is_sent)
);

-- =====================================================================================
-- SAMPLE DATA (Optional) - Dữ liệu mẫu để test
-- =====================================================================================
-- Uncomment các dòng dưới để insert dữ liệu mẫu cho việc test và development
-- =====================================================================================

-- Menstrual cycle mẫu cho user_id = 1
-- INSERT INTO menstrual_cycles (user_id, start_date, end_date, flow_intensity, notes) 
-- VALUES (1, '2025-05-15', '2025-05-20', 'NORMAL', 'First cycle recorded for testing');

-- Contraceptive reminder mẫu cho user_id = 1
-- INSERT INTO contraceptive_reminders (user_id, pill_name, reminder_time, pack_start_date, pack_duration, break_duration, timezone)
-- VALUES (1, 'Yasmin', '08:00:00', '2025-06-01', 21, 7, 'Asia/Ho_Chi_Minh');

-- Cycle prediction mẫu cho user_id = 1
-- INSERT INTO cycle_predictions (user_id, predicted_ovulation_date, fertile_window_start, fertile_window_end, next_period_date, pregnancy_likelihood)
-- VALUES (1, '2025-06-28', '2025-06-23', '2025-06-29', '2025-07-12', 15.0);

-- Notification mẫu cho user_id = 1
-- INSERT INTO cycle_notifications (user_id, type, title, message, scheduled_time)
-- VALUES (1, 'OVULATION_REMINDER', 'Ovulation Tomorrow', 'Your ovulation is predicted for tomorrow!', '2025-06-27 09:00:00');

-- =====================================================================================
-- KẾT THÚC SCRIPT
-- =====================================================================================

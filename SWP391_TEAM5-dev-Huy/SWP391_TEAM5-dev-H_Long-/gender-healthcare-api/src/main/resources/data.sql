-- data.sql
-- File khởi tạo dữ liệu ban đầu cho hệ thống

-- 1. Thêm người dùng admin mặc định
-- Mật khẩu được mã hóa bằng BCrypt: "adminpassword"
-- Dùng PasswordEncoderUtil để tạo mã hóa nếu cần thay đổi

INSERT INTO users (username, password, full_name, gender, date_of_birth, email, phone_number, role)
VALUES (
    'admin',
    '$2a$10$UVFRGMbI28NWWYL0N/zw9.MX0iROABgiZNiocxk0otaePmwfjsoD6',
    'Người dùng Quản trị',
    'Khác',
    '1990-01-01',
    'admin@example.com',
    '0123456789',
    'ADMIN'
)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    full_name = VALUES(full_name),
    gender = VALUES(gender),
    date_of_birth = VALUES(date_of_birth),
    email = VALUES(email),
    phone_number = VALUES(phone_number),
    role = VALUES(role);


-- 2. Thêm thông tin phòng khám
INSERT INTO clinic_info (id, name, description) VALUES
(1, 'Phòng khám ABC', 'Cơ sở y tế chuyên sâu về giới tính')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description);


-- 3. Thêm dữ liệu blog nếu chưa tồn tại
INSERT INTO blog (title, content, created_at, author_id)
SELECT * FROM (SELECT 'Cách phòng tránh STIs', 'Chia sẻ kiến thức về an toàn tình dục', NOW(), 1) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM blog WHERE title = 'Cách phòng tránh STIs');

INSERT INTO blog (title, content, created_at, author_id)
SELECT * FROM (SELECT 'Sức khỏe sinh sản nữ giới', 'Những điều cần biết về chu kỳ và chăm sóc sức khỏe', NOW(), 1) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM blog WHERE title = 'Sức khỏe sinh sản nữ giới');


-- 4. Thêm dịch vụ nếu chưa tồn tại
INSERT INTO service (name, description, type, price)
SELECT * FROM (
    SELECT 'Xét nghiệm HIV', 'Dịch vụ xét nghiệm nhanh, chính xác', 'STI_TEST', 150.00
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM service WHERE name = 'Xét nghiệm HIV');

INSERT INTO service (name, description, type, price)
SELECT * FROM (
    SELECT 'Tư vấn Online 30 phút', 'Tư vấn sức khỏe giới tính trực tuyến qua video call', 'CONSULTATION', 50.00
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM service WHERE name = 'Tư vấn Online 30 phút');

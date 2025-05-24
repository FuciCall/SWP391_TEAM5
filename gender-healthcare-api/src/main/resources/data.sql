-- Chèn dữ liệu bảng clinic_info (có id cố định)
INSERT INTO clinic_info (id, name, description) VALUES
(1, 'Phòng khám ABC', 'Cơ sở y tế chuyên sâu về giới tính');

-- Bảng blog, nếu id tự tăng thì không cần chèn id
INSERT INTO blog (title, content, created_at) VALUES
('Cách phòng tránh STIs', 'Chia sẻ kiến thức về an toàn tình dục', NOW());

-- Bảng service, nếu id tự tăng thì không chèn id
INSERT INTO service (name, description) VALUES
('Xét nghiệm HIV', 'Dịch vụ xét nghiệm nhanh, chính xác');


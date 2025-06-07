package com.gha.gender_healthcare_api.util; // Bạn có thể tạo package 'util' hoặc đặt ở đâu đó tạm thời

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Lớp tiện ích để mã hóa mật khẩu bằng BCryptPasswordEncoder.
 * Sử dụng lớp này để tạo mật khẩu đã mã hóa cho file data.sql hoặc mục đích
 * kiểm thử.
 */
public class PasswordEncoderUtil {

    public static void main(String[] args) {
        // Tạo một instance của BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Mật khẩu gốc bạn muốn mã hóa
        String rawPassword = "adminpassword"; // Đây là mật khẩu bạn muốn dùng cho tài khoản 'admin'

        // Mã hóa mật khẩu
        String encodedPassword = encoder.encode(rawPassword);

        // In ra mật khẩu đã mã hóa
        System.out.println("Mật khẩu gốc: " + rawPassword);
        System.out.println("Mật khẩu đã mã hóa (BCrypt): " + encodedPassword);

        // Bạn có thể kiểm tra một mật khẩu đã mã hóa có khớp với mật khẩu gốc không
        // System.out.println("Kiểm tra khớp mật khẩu: " + encoder.matches(rawPassword,
        // encodedPassword));
    }
}

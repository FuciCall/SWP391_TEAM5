//: Gửi email (ví dụ: email đặt lại mật khẩu) cho người dùng.
package com.gha.gender_healthcare_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Lấy email người gửi từ application.properties
    private String senderEmail;

    @Value("${spring.mail.sender.name}") // Lấy tên hiển thị của người gửi từ application.properties
    private String senderName; // Đảm bảo dòng này đã được thêm

    @Value("${password.reset.token.expiration.minutes}") // Lấy thời gian hết hạn token
    private int tokenExpirationMinutes;

    public void sendPasswordResetEmail(String toEmail, String userName, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Cập nhật dòng này để sử dụng senderName và senderEmail
        message.setFrom(senderName + " <" + senderEmail + ">");
        message.setTo(toEmail);
        message.setSubject("Đặt lại mật khẩu cho tài khoản của bạn");
        message.setText("Xin chào " + userName + ",\n\n"
                + "Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n"
                + "Vui lòng nhấp vào liên kết dưới đây để đặt lại mật khẩu của bạn:\n"
                + resetLink + "\n\n"
                + "Liên kết này sẽ hết hạn sau " + tokenExpirationMinutes + " phút.\n"
                + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ của bạn");
        try {
            mailSender.send(message);
            logger.info("Email đặt lại mật khẩu đã được gửi đến: {}", toEmail);
        } catch (MailException e) {
            logger.error("Không thể gửi email đặt lại mật khẩu đến {}: {}", toEmail, e.getMessage());
        }
    }
}

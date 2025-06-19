/**
 * Service class quản lý gửi email cho hệ thống Gender Healthcare
 * 
 * Chức năng chí        try {
            mailSender.send(messag        try {
            mailSender.send(emailMessage);
            log.info("✅ Cycle notification email sent successfully to: {} with subject: {}", toEmail, subject);
        } catch (MailException e) {
            log.error("❌ Failed to send cycle notification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send notification email: " + e.getMessage());
        }          log.info("✅ Password reset email sent successfully to: {}", toEmail);
        } catch (MailException e) {
            log.error("❌ Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        } 1. Gửi email đặt lại mật khẩu cho user
 * 2. Gửi email thông báo chu kỳ sinh sản (notifications, reminders)
 * 3. Có thể mở rộng: email xác nhận đăng ký, email appointment confirmation, etc.
 * 
 * Cấu hình:
 * - Sử dụng Spring Boot Mail starter
 * - Cấu hình SMTP trong application.properties
 * - Support Gmail, Outlook, và các SMTP servers khác
 * 
 * Template email:
 * - Có format và styling cơ bản
 * - Bao gồm logo/branding của Gender Healthcare
 * - Responsive và user-friendly
 */
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

    /**
     * Spring Boot Mail sender - tự động configure từ application.properties
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Email người gửi (from address) - cấu hình trong application.properties
     * Ví dụ: spring.mail.username=support@genderhealthcare.com
     */
    @Value("${spring.mail.username}")
    private String senderEmail;

    /**
     * Tên hiển thị của người gửi email
     * Ví dụ: spring.mail.sender.name=Gender Healthcare Support
     */
    @Value("${spring.mail.sender.name}")
    private String senderName;

    /**
     * Thời gian hết hạn của password reset token (phút)
     * Ví dụ: password.reset.token.expiration.minutes=30
     */
    @Value("${password.reset.token.expiration.minutes}")
    private int tokenExpirationMinutes;

    /**
     * Gửi email đặt lại mật khẩu cho user
     * 
     * @param toEmail Email người nhận
     * @param userName Tên user (để personalize email)
     * @param resetLink Link đặt lại mật khẩu (chứa token)
     * 
     * Email template bao gồm:
     * - Lời chào thân thiện
     * - Hướng dẫn đặt lại mật khẩu
     * - Thông báo về thời gian hết hạn
     * - Lưu ý bảo mật
     */
    public void sendPasswordResetEmail(String toEmail, String userName, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        // Set sender với tên hiển thị và email
        message.setFrom(senderName + " <" + senderEmail + ">");
        message.setTo(toEmail);        message.setSubject("🔒 Password Reset - Gender Healthcare");
        
        // Email content với format thân thiện
        message.setText("Hello " + userName + ",\n\n"
                + "We received a request to reset the password for your Gender Healthcare account.\n\n"
                + "Please click the link below to create a new password:\n"
                + resetLink + "\n\n"
                + "⏰ This link will expire in " + tokenExpirationMinutes + " minutes for security reasons.\n\n"
                + "🔐 If you did not request a password reset, please ignore this email. "
                + "Your password will not be changed.\n\n"
                + "Need help? Contact us at support@genderhealthcare.com\n\n"
                + "Best regards,\n"
                + "Gender Healthcare Team");

        try {
            mailSender.send(message);
            logger.info("✅ Email đặt lại mật khẩu đã được gửi đến: {}", toEmail);
        } catch (MailException e) {
            logger.error("❌ Không thể gửi email đặt lại mật khẩu đến {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
        }
    }    /**
     * Gửi email thông báo chu kỳ sinh sản (cycle notifications)
     * 
     * @param toEmail Email người nhận
     * @param subject Tiêu đề thông báo (ví dụ: "Rụng trứng dự kiến vào ngày mai")
     * @param message Nội dung thông báo chi tiết
     * 
     * Các loại thông báo thường gửi:
     * - Ovulation reminders (nhắc nhở rụng trứng)
     * - Period reminders (nhắc nhở chu kỳ sắp đến)
     * - Fertile window notifications (thông báo cửa sổ sinh sản)
     * - Contraceptive pill reminders (nhắc nhở uống thuốc tránh thai)
     * 
     * Email template bao gồm:
     * - Emoji để thu hút attention
     * - Nội dung thông báo từ CycleNotificationService
     * - Tips sức khỏe bổ sung
     * - Branding Gender Healthcare
     */
    public void sendCycleNotificationEmail(String toEmail, String subject, String message) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        
        // Set sender với tên hiển thị
        emailMessage.setFrom(senderName + " <" + senderEmail + ">");
        emailMessage.setTo(toEmail);
        
        // Subject với emoji và branding
        emailMessage.setSubject("🌸 " + subject + " - Gender Healthcare");
          // Email content với format thân thiện và informative
        emailMessage.setText("Hello,\n\n"
                + message + "\n\n"
                + "💡 Health tip: Continue tracking your cycle for the best health insights!\n"
                + "📱 Log into the app to see more details and update your information.\n\n"
                + "❓ Have questions? Consult with a specialist or contact support@genderhealthcare.com\n\n"
                + "Stay healthy and happy! 💚\n"
                + "Gender Healthcare Team");

        try {
            mailSender.send(emailMessage);
            logger.info("✅ Email thông báo chu kỳ đã được gửi đến: {} với subject: {}", toEmail, subject);
        } catch (MailException e) {
            logger.error("❌ Không thể gửi email thông báo chu kỳ đến {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Gửi email thông báo thất bại: " + e.getMessage());
        }
    }
}

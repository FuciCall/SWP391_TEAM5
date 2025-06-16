//: Xử lý logic tạo token, gửi email và đặt lại mật khẩu cho người dùng.
package com.gha.gender_healthcare_api.service;

import java.util.Optional;
import com.gha.gender_healthcare_api.entity.PasswordResetToken;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.repository.PasswordResetTokenRepository;
import com.gha.gender_healthcare_api.repository.UserRepository;
import com.gha.gender_healthcare_api.dto.request.ResetPasswordRequest;
import com.gha.gender_healthcare_api.exception.InvalidTokenException;
import com.gha.gender_healthcare_api.exception.PasswordMismatchException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List; // Thêm import này

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${password.reset.token.expiration.minutes}")
    private int tokenExpirationMinutes;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        // CÁCH MỚI ĐÃ SỬA: Sử dụng phương thức deleteAllByUser để đảm bảo xóa tất cả
        // token cũ
        // Điều này sẽ giải quyết lỗi "Duplicate entry" nếu nó vẫn còn
        passwordResetTokenRepository.deleteAllByUser(user);
        logger.info("Đã xóa tất cả token đặt lại mật khẩu cũ cho người dùng {}.", user.getEmail());

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(tokenExpirationMinutes);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetLink);
        logger.info("Token đặt lại mật khẩu đã được tạo và email đã được gửi đến: {}", user.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new PasswordMismatchException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }

        // Phương thức findByToken trả về Optional, và orElseThrow xử lý trường hợp
        // Optional rỗng
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(
                        () -> new InvalidTokenException("Token đặt lại mật khẩu không hợp lệ hoặc không tìm thấy."));

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token đặt lại mật khẩu đã hết hạn hoặc đã được sử dụng.");
        }

        User user = resetToken.getUser();
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng liên kết với token này.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        logger.info("Mật khẩu của người dùng {} đã được đặt lại thành công.", user.getUsername());
    }

    // Phương thức này chỉ đơn giản là chuyển tiếp Optional từ repository
    // Bản thân nó không gây lỗi Optional nếu được sử dụng đúng cách ở nơi gọi
    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }
}

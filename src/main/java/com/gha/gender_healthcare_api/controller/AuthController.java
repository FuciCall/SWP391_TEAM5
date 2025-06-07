//: Controller xử lý các API xác thực: đăng ký, đăng nhập, quên mật khẩu, đặt lại mật khẩu, lấy thông tin user hiện tại.
package com.gha.gender_healthcare_api.controller;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.dto.request.LoginRequest;
import com.gha.gender_healthcare_api.dto.request.RegisterRequest;
import com.gha.gender_healthcare_api.dto.request.ForgotPasswordRequest; // Import DTO mới cho quên mật khẩu
import com.gha.gender_healthcare_api.dto.request.ResetPasswordRequest; // Import DTO mới cho đặt lại mật khẩu
import com.gha.gender_healthcare_api.dto.response.JwtAuthenticationResponse;
import com.gha.gender_healthcare_api.service.UserService;
import com.gha.gender_healthcare_api.service.PasswordResetService; // Import Service mới cho đặt lại mật khẩu
import com.gha.gender_healthcare_api.security.JwtTokenProvider;
import com.gha.gender_healthcare_api.security.CustomUserDetails;
import com.gha.gender_healthcare_api.exception.InvalidTokenException; // Import exception cho token không hợp lệ
import com.gha.gender_healthcare_api.exception.PasswordMismatchException; // Import exception cho mật khẩu không khớp

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import này để bắt lỗi cụ thể
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // Để kích hoạt validation

@RestController
@RequestMapping("/api/auth") // Định nghĩa đường dẫn cơ sở cho các API xác thực
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // Quản lý quá trình xác thực

    @Autowired
    private UserService userService; // Dịch vụ quản lý người dùng

    @Autowired
    private JwtTokenProvider tokenProvider; // Cung cấp JWT token

    @Autowired
    private PasswordResetService passwordResetService; // Dịch vụ đặt lại mật khẩu

    @PostMapping("/register") // Endpoint đăng ký người dùng mới
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest,
            Authentication authentication) {
        // Kiểm tra xem username đã tồn tại chưa
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return new ResponseEntity<>("Username already exists!", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra xem email đã tồn tại chưa
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return new ResponseEntity<>("Email already exists!", HttpStatus.BAD_REQUEST);
        }

        // CHỖ ĐÃ SỬA: Chuyển đổi chuỗi giới tính từ RegisterRequest sang enum
        // User.Gender (sử dụng giá trị tiếng Anh)
        User.Gender userGender = User.Gender.OTHER; // Giá trị mặc định
        if (registerRequest.getGender() != null) {
            switch (registerRequest.getGender().toLowerCase()) { // Chuyển sang chữ thường để so sánh
                case "male":
                    userGender = User.Gender.MALE;
                    break;
                case "female":
                    userGender = User.Gender.FEMALE;
                    break;
                case "other":
                    userGender = User.Gender.OTHER;
                    break;
                default:
                    // Ghi log hoặc xử lý trường hợp giá trị không hợp lệ nếu cần
                    // Hiện tại, @Pattern trong RegisterRequest nên đảm bảo giá trị hợp lệ
                    break;
            }
        }

        // Kiểm tra quyền hạn của người dùng (chỉ ADMIN mới có thể tạo user với role
        // khác CUSTOMER)
        String role = registerRequest.getRole();
        if (!"CUSTOMER".equals(role)) {
            // Nếu người dùng không phải là ADMIN, trả về lỗi Forbidden
            if (authentication == null
                    || !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không đủ quyền tạo user với role này!");
            }
        }

        // Tạo đối tượng User từ RegisterRequest
        User.Role userRole = User.Role.valueOf(registerRequest.getRole().toUpperCase());

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getFullName(),
                userGender,
                registerRequest.getDateOfBirth(),
                registerRequest.getEmail(),
                registerRequest.getPhoneNumber(),
                userRole // <-- Truyền đúng role từ request
        );

        userService.saveUser(user); // Lưu người dùng vào database

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED); // Trả về phản hồi thành công
    }

    @PostMapping("/login") // Endpoint đăng nhập
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Xác thực người dùng bằng username/email và mật khẩu
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        // Đặt đối tượng xác thực vào SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication); // Tạo JWT token
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt)); // Trả về token
    }

    @GetMapping("/me") // Endpoint lấy thông tin người dùng hiện tại
    @ResponseBody
    public ResponseEntity<String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                return ResponseEntity.ok("Hello, " + userDetails.getUsername() + "! Your role is: "
                        + userDetails.getRole() + ". ID: " + userDetails.getId());
            } else {
                return ResponseEntity.ok("Hello, " + principal.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not logged in."); // Trả về lỗi nếu chưa
                                                                                              // đăng nhập
    }

    /**
     * Endpoint yêu cầu đặt lại mật khẩu.
     * Người dùng gửi email để nhận liên kết đặt lại mật khẩu.
     * 
     * @param request Chứa email của người dùng.
     * @return Thông báo cho người dùng về việc gửi email.
     */
    @PostMapping("/forgot-password-request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.createPasswordResetTokenForUser(request.getEmail());
            // Luôn trả về thông báo chung chung để tránh lộ thông tin email tồn tại
            return new ResponseEntity<>(
                    "If the email you entered is registered in our system, a password reset link has been sent to that email. Please check your inbox or spam folder.",
                    HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            // Vẫn trả về OK để tránh lộ thông tin email không tồn tại
            return new ResponseEntity<>(
                    "If the email you entered is registered in our system, a password reset link has been sent to that email. Please check your inbox or spam folder.",
                    HttpStatus.OK);
        } catch (Exception e) {
            // GlobalExceptionHandler sẽ bắt các lỗi khác và trả về 500
            throw e; // Ném lại để GlobalExceptionHandler xử lý
        }
    }

    /**
     * Endpoint đặt lại mật khẩu.
     * Người dùng gửi token và mật khẩu mới để cập nhật.
     * 
     * @param request Chứa token, mật khẩu mới và xác nhận mật khẩu.
     * @return Thông báo thành công hoặc lỗi.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request); // Gọi dịch vụ đặt lại mật khẩu
            return new ResponseEntity<>("Your password has been reset successfully.", HttpStatus.OK); // Trả về thành
                                                                                                      // công
        } catch (InvalidTokenException | PasswordMismatchException e) {
            // GlobalExceptionHandler sẽ bắt các lỗi này và trả về 400
            throw e; // Ném lại để GlobalExceptionHandler xử lý
        } catch (Exception e) {
            // GlobalExceptionHandler sẽ bắt các lỗi khác và trả về 500
            throw e; // Ném lại để GlobalExceptionHandler xử lý
        }
    }
}

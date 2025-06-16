//: DTO nhận dữ liệu từ client khi gửi yêu cầu đặt lại mật khẩu.
package com.gha.gender_healthcare_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Import cho custom validation (nếu bạn muốn thêm)
// import com.gha.gender_healthcare_api.validation.PasswordMatches; // Ví dụ

// @PasswordMatches(message = "Mật khẩu mới và xác nhận mật khẩu không khớp") // Ví dụ sử dụng custom validation
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "Token không được để trống")
    private String token;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu mới không được để trống")
    private String confirmNewPassword;
}

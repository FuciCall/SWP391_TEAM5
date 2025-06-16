//: DTO nhận dữ liệu từ client khi đăng nhập (username/email và password).
package com.gha.gender_healthcare_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // Import Size

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Tên đăng nhập hoặc Email không được để trống.")
    @Size(min = 3, max = 50, message = "Tên đăng nhập hoặc Email phải từ 3 đến 50 ký tự.") // Thêm Size
    private String usernameOrEmail;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Size(min = 6, max = 50, message = "Mật khẩu phải có ít nhất 6 ký tự.") // Thêm Size (max nếu cần)
    private String password;
}
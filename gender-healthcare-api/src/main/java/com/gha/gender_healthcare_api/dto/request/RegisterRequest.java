package com.gha.gender_healthcare_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate; // Dùng cho DateOfBirth

@Getter // Lombok: Tự động tạo getters cho tất cả các trường
@Setter // Lombok: Tự động tạo setters cho tất cả các trường
@NoArgsConstructor // Lombok: Tự động tạo constructor không tham số
@AllArgsConstructor // Lombok: Tự động tạo constructor với tất cả tham số
public class RegisterRequest {

    @NotBlank(message = "Username không được để trống") // Validation: không được trống
    @Size(min = 3, max = 50, message = "Username phải có từ 3 đến 50 ký tự") // Validation: độ dài
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng") // Validation: định dạng email
    private String email;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private String gender; // Có thể null, không bắt buộc

    private LocalDate dateOfBirth; // Có thể null, không bắt buộc

    private String phoneNumber; // Có thể null, không bắt buộc
}

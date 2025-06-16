package com.gha.gender_healthcare_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Họ tên không được để trống.")
    @Size(max = 50, message = "Họ tên không được quá 50 ký tự.")
    private String fullName;

    @NotBlank(message = "Giới tính không được để trống.")
    @Pattern(regexp = "(?i)male|female|other", message = "Giới tính không hợp lệ. Hãy nhập male, female hoặc other.")
    private String gender;

    @NotNull(message = "Ngày sinh không được để trống.")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ.")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không đúng định dạng.")
    @Size(max = 100, message = "Email không được quá 100 ký tự.")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không hợp lệ. Vui lòng nhập đúng định dạng (ví dụ: 0912345678 hoặc +84912345678).")
    @Size(min = 10, max = 12, message = "Số điện thoại phải có từ 10 đến 12 chữ số.")
    private String phoneNumber;
}

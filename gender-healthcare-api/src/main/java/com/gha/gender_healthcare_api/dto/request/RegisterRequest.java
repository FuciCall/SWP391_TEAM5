//: DTO nhận dữ liệu từ client khi đăng ký tài khoản mới.
package com.gha.gender_healthcare_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data; // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;

@Data // Tự động tạo getters, setters, toString, equals, hashCode
@NoArgsConstructor // Tự động tạo constructor không tham số
@AllArgsConstructor // Tự động tạo constructor với tất cả các tham số
public class RegisterRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống") // Đảm bảo trường không rỗng
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự") // Kích thước username
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống.") // Đảm bảo trường không rỗng
    @Size(min = 6, max = 100, message = "Mật khẩu phải có ít nhất 6 ký tự.") // Kích thước mật khẩu
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,}$", message = "Mật khẩu phải chứa ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.")
    private String password;

    @NotBlank(message = "Email không được để trống.") // Đảm bảo trường không rỗng
    @Email(message = "Email không đúng định dạng.") // Kiểm tra định dạng email
    @Size(max = 100, message = "Email không được quá 100 ký tự.") // Kích thước email
    private String email;

    @NotBlank(message = "Họ tên không được để trống.") // Đảm bảo trường không rỗng
    @Size(max = 50, message = "Họ tên không được quá 50 ký tự.") // Kích thước họ tên
    private String fullName;

    @NotBlank(message = "Giới tính không được để trống.") // Đảm bảo trường không rỗng
    @Pattern(regexp = "(?i)male|female|other", message = "Giới tính không hợp lệ. Hãy nhập male, female hoặc other.")
    private String gender;

    @NotNull(message = "Ngày sinh không được để trống.") // Đảm bảo trường không null
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ.") // Ngày sinh phải là ngày đã qua
    private LocalDate dateOfBirth;

    @NotBlank(message = "Số điện thoại không được để trống.") // Đảm bảo trường không rỗng
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không hợp lệ. Vui lòng nhập đúng định dạng (ví dụ: 0912345678 hoặc +84912345678).")
    @Size(min = 10, max = 12, message = "Số điện thoại phải có từ 10 đến 12 chữ số.") // Kích thước số điện thoại
    private String phoneNumber;

    @Pattern(regexp = "(?i)CUSTOMER|CONSULTANT|STAFF|ADMIN", message = "Role không hợp lệ.")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

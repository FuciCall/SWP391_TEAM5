package com.gha.gender_healthcare_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Consultant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long consultantId;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 50, message = "Tên không được quá 50 ký tự.")
    @Column(length = 50, nullable = false)
    String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng.")
    @Size(max = 100, message = "Email không được quá 100 ký tự.")
    @Column(length = 100, nullable = false, unique = true)
    String email;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không hợp lệ. Vui lòng nhập đúng định dạng (ví dụ: 0912345678 hoặc +84912345678).")
    @Size(min = 10, max = 12, message = "Số điện thoại phải có từ 10 đến 12 chữ số.")
    @Column(length = 12, nullable = false)
    String phone;

    @NotBlank(message = "Giới tính không được để trống.")
    @Pattern(regexp = "Male|Female|Other", message = "Giới tính phải là 'Male', 'Female', hoặc 'Other'.")
    @Column(length = 10, nullable = false)
    String gender;

    @NotBlank(message = "Bằng cấp không được để trống")
    @Column(length = 255, nullable = false)
    String degree;

    @NotNull(message = "Số năm kinh nghiệm không được để trống")
    @Min(value = 0, message = "Số năm kinh nghiệm phải lớn hơn hoặc bằng 0")
    @Column(nullable = false)
    Integer experienceYears;

    @NotBlank(message = "Chuyên môn không được để trống")
    @Column(length = 255, nullable = false)
    String specialty;

    @NotBlank(message = "Tiểu sử không được để trống")
    @Column(columnDefinition = "TEXT")
    String bio;

    @NotBlank(message = "Trạng thái không được để trống")
    @Column(length = 20, nullable = false)
    String status; // ACTIVE, INACTIVE, etc.

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;
}
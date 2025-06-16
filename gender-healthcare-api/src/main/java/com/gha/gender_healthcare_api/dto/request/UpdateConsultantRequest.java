package com.gha.gender_healthcare_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateConsultantRequest {
    @NotBlank(message = "Bằng cấp không được để trống.")
    @Size(max = 100, message = "Bằng cấp không được quá 100 ký tự.")
    private String degree;

    @NotNull(message = "Số năm kinh nghiệm không được để trống.")
    private Integer experienceYears;

    @NotNull(message = "Chuyên khoa không được để trống.")
    private Long specialtyId; // Đổi từ String specialty sang Long specialtyId

    @NotBlank(message = "Tiểu sử không được để trống.")
    @Size(max = 1000, message = "Tiểu sử không được quá 1000 ký tự.")
    private String bio;

    @NotBlank(message = "Trạng thái không được để trống.")
    @Pattern(regexp = "(?i)active|inactive|banned", message = "Trạng thái không hợp lệ. Chỉ chấp nhận ACTIVE, INACTIVE, BANNED.")
    private String status;

    // Thông tin user liên kết (nếu muốn cập nhật)
    @Valid
    private UpdateUserRequest user;
}

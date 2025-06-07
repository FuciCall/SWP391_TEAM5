package com.gha.gender_healthcare_api.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsultantRequest {

    @NotBlank(message = "Bằng cấp không được để trống")
    private String degree;

    @NotNull(message = "Số năm kinh nghiệm không được để trống")
    @Min(value = 0, message = "Số năm kinh nghiệm phải lớn hơn hoặc bằng 0")
    private Integer experienceYears;

    @NotBlank(message = "Chuyên môn không được để trống")
    private String specialty;

    @NotBlank(message = "Tiểu sử không được để trống")
    private String bio;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;

    @NotNull(message = "UserId không được để trống")
    private Long userId;
}

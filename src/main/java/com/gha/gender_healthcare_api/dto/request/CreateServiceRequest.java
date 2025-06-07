/*
 * DTO dùng cho request tạo mới dịch vụ (service) - KHÔNG có trường id.
 */

package com.gha.gender_healthcare_api.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceRequest {
    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String name;

    private String description;

    @NotBlank(message = "Loại dịch vụ không được để trống")
    private String type;

    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải lớn hơn 0")
    private Double price;
}
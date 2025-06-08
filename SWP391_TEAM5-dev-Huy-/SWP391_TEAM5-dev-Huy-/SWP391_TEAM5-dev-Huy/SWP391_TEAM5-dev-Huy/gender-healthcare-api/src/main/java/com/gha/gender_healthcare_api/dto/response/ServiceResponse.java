package com.gha.gender_healthcare_api.dto.response;

import lombok.Data;

@Data
public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Double price;
}

package com.gha.gender_healthcare_api.dto.request;

import lombok.Data;

@Data
public class ServiceRequest {
    private String name;
    private String description;
    private String type;
    private Double price;
}

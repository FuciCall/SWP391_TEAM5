package com.gha.gender_healthcare_api.dto.response;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryDTO {
    private Long id;
    private String type; // "Test" hoặc "Consultation"
    private String description;
    private LocalDate date;
}
package com.gha.gender_healthcare_api.cycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenstrualCycleRequest {
    
    @NotNull(message = "Start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @NotNull(message = "Flow intensity is required")
    private MenstrualCycle.FlowIntensity flowIntensity;
    
    private String symptoms; // JSON array of symptoms
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}

package com.gha.gender_healthcare_api.cycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContraceptiveReminderRequest {
    
    @NotBlank(message = "Pill name is required")
    @Size(max = 100, message = "Pill name cannot exceed 100 characters")
    private String pillName;
    
    @NotNull(message = "Reminder time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime reminderTime;
    
    @NotNull(message = "Pack start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate packStartDate;
    
    @NotNull(message = "Pack duration is required")
    @Min(value = 1, message = "Pack duration must be at least 1 day")
    @Max(value = 35, message = "Pack duration cannot exceed 35 days")
    private Integer packDuration;
    
    @NotNull(message = "Break duration is required")
    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 14, message = "Break duration cannot exceed 14 days")
    private Integer breakDuration;
    
    @NotBlank(message = "Timezone is required")
    private String timezone;
}

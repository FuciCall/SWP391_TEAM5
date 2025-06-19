package com.gha.gender_healthcare_api.cycle.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleAnalytics {
    private int totalCycles;
    private double averageCycleLength;
    private double averagePeriodLength;
    private String cycleRegularity;
    private LocalDate lastPeriodDate;
    private LocalDate nextPredictedPeriod;
    private LocalDate nextPredictedOvulation;
    private double currentPregnancyLikelihood;
    private List<String> healthInsights;
}

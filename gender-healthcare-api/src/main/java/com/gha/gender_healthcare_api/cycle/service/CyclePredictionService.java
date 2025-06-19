package com.gha.gender_healthcare_api.cycle.service;

import com.gha.gender_healthcare_api.cycle.entity.CyclePrediction;
import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import com.gha.gender_healthcare_api.cycle.repository.CyclePredictionRepository;
import com.gha.gender_healthcare_api.cycle.repository.MenstrualCycleRepository;
import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalDouble;

@Service
@Slf4j
public class CyclePredictionService {
    
    @Autowired
    private MenstrualCycleRepository menstrualCycleRepository;
    
    @Autowired
    private CyclePredictionRepository cyclePredictionRepository;
    
    public CyclePrediction generatePredictions(Long userId) {
        // Get user's cycle history
        List<MenstrualCycle> recentCycles = menstrualCycleRepository.findByUserUserIdOrderByStartDateDesc(userId);
        
        if (recentCycles.isEmpty()) {
            throw new IllegalStateException("No cycle data available for predictions");
        }
        
        // Calculate average cycle length
        OptionalDouble avgCycleLength = recentCycles.stream()
            .filter(c -> c.getCycleLength() != null)
            .limit(6) // Use last 6 cycles for accuracy
            .mapToInt(MenstrualCycle::getCycleLength)
            .average();
            
        int cycleLengthToUse = (int) avgCycleLength.orElse(28);
        
        LocalDate lastPeriodStart = recentCycles.get(0).getStartDate();
        
        // Predict next ovulation (typically 14 days before next period)
        LocalDate nextPeriodDate = lastPeriodStart.plusDays(cycleLengthToUse);
        LocalDate ovulationDate = nextPeriodDate.minusDays(14);
        
        // Fertile window (5 days before + ovulation day + 1 day after)
        LocalDate fertileStart = ovulationDate.minusDays(5);
        LocalDate fertileEnd = ovulationDate.plusDays(1);
        
        // Calculate pregnancy likelihood based on current date
        double pregnancyLikelihood = calculatePregnancyLikelihood(fertileStart, fertileEnd);
        
        CyclePrediction prediction = new CyclePrediction();
        
        // Set User reference
        User user = new User();
        user.setUserId(userId);
        prediction.setUser(user);
        
        prediction.setPredictedOvulationDate(ovulationDate);
        prediction.setFertileWindowStart(fertileStart);
        prediction.setFertileWindowEnd(fertileEnd);
        prediction.setNextPeriodDate(nextPeriodDate);
        prediction.setPregnancyLikelihood(pregnancyLikelihood);
        prediction.setCalculationDate(LocalDateTime.now());
        
        return cyclePredictionRepository.save(prediction);
    }
    
    private double calculatePregnancyLikelihood(LocalDate fertileStart, LocalDate fertileEnd) {
        LocalDate today = LocalDate.now();
        
        if (today.isBefore(fertileStart) || today.isAfter(fertileEnd)) {
            return 0.0; // Outside fertile window
        }
        
        // Peak fertility on ovulation day and day before
        LocalDate ovulationDay = fertileEnd.minusDays(1);
        LocalDate dayBeforeOvulation = ovulationDay.minusDays(1);
        
        if (today.equals(ovulationDay) || today.equals(dayBeforeOvulation)) {
            return 25.0; // Peak fertility
        } else if (today.equals(fertileStart) || today.equals(fertileEnd)) {
            return 10.0; // Lower fertility at edges
        } else {
            return 20.0; // Moderate fertility in middle
        }
    }
    
    public CyclePrediction getCurrentPrediction(Long userId) {
        return cyclePredictionRepository.findTopByUserUserIdOrderByCalculationDateDesc(userId)
            .orElseThrow(() -> new EntityNotFoundException("No predictions found for user"));
    }
}

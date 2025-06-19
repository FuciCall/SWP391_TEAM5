package com.gha.gender_healthcare_api.cycle.service;

import com.gha.gender_healthcare_api.cycle.dto.CycleAnalytics;
import com.gha.gender_healthcare_api.cycle.dto.MenstrualCycleRequest;
import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import com.gha.gender_healthcare_api.cycle.repository.MenstrualCycleRepository;
import com.gha.gender_healthcare_api.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class MenstrualCycleService {
    
    @Autowired
    private MenstrualCycleRepository menstrualCycleRepository;
    
    @Autowired
    private CyclePredictionService cyclePredictionService;
    
    @Autowired
    private CycleNotificationService notificationService;
    
    public MenstrualCycle declareMenstrualCycle(Long userId, MenstrualCycleRequest request) {
        MenstrualCycle cycle = new MenstrualCycle();
        
        // Set User reference
        User user = new User();
        user.setUserId(userId);
        cycle.setUser(user);
        
        cycle.setStartDate(request.getStartDate());
        cycle.setEndDate(request.getEndDate());
        cycle.setFlowIntensity(request.getFlowIntensity());
        cycle.setSymptoms(request.getSymptoms());
        cycle.setNotes(request.getNotes());
        cycle.setCreatedAt(LocalDateTime.now());
        cycle.setUpdatedAt(LocalDateTime.now());
        
        // Calculate cycle length from previous cycle
        Optional<MenstrualCycle> lastCycle = menstrualCycleRepository.findTopByUserUserIdOrderByStartDateDesc(userId);
        if (lastCycle.isPresent()) {
            long daysBetween = ChronoUnit.DAYS.between(lastCycle.get().getStartDate(), cycle.getStartDate());
            cycle.setCycleLength((int) daysBetween);
        }
        
        // Calculate period length
        if (request.getEndDate() != null) {
            long periodDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
            cycle.setPeriodLength((int) periodDays);
        }
        
        MenstrualCycle savedCycle = menstrualCycleRepository.save(cycle);
        
        // Generate predictions and notifications
        try {
            cyclePredictionService.generatePredictions(userId);
            notificationService.scheduleOvulationReminders(userId);
        } catch (Exception e) {
            log.error("Error generating predictions/notifications for user {}: {}", userId, e.getMessage());
        }
        
        return savedCycle;
    }
    
    public List<MenstrualCycle> getUserCycles(Long userId, int months) {
        LocalDate fromDate = LocalDate.now().minusMonths(months);
        return menstrualCycleRepository.findByUserIdAndStartDateAfter(userId, fromDate);
    }
    
    public CycleAnalytics getCycleAnalytics(Long userId) {
        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUserUserIdOrderByStartDateDesc(userId);
        
        if (cycles.isEmpty()) {
            return CycleAnalytics.builder()
                .totalCycles(0)
                .averageCycleLength(28)
                .averagePeriodLength(5)
                .cycleRegularity("INSUFFICIENT_DATA")
                .healthInsights(List.of("Start tracking your cycles to get personalized insights!"))
                .build();
        }
        
        // Calculate averages
        OptionalDouble avgCycleLength = cycles.stream()
            .filter(c -> c.getCycleLength() != null)
            .mapToInt(MenstrualCycle::getCycleLength)
            .average();
            
        OptionalDouble avgPeriodLength = cycles.stream()
            .filter(c -> c.getPeriodLength() != null)
            .mapToInt(MenstrualCycle::getPeriodLength)
            .average();
        
        // Get health insights
        List<String> insights = generateHealthInsights(cycles);
        
        return CycleAnalytics.builder()
            .totalCycles(cycles.size())
            .averageCycleLength(avgCycleLength.orElse(28))
            .averagePeriodLength(avgPeriodLength.orElse(5))
            .cycleRegularity(calculateRegularity(cycles))
            .lastPeriodDate(cycles.get(0).getStartDate())
            .healthInsights(insights)
            .build();
    }
    
    private String calculateRegularity(List<MenstrualCycle> cycles) {
        if (cycles.size() < 3) return "INSUFFICIENT_DATA";
        
        List<Integer> cycleLengths = cycles.stream()
            .filter(c -> c.getCycleLength() != null)
            .map(MenstrualCycle::getCycleLength)
            .collect(Collectors.toList());
            
        if (cycleLengths.size() < 3) return "INSUFFICIENT_DATA";
        
        double variance = calculateVariance(cycleLengths);
        
        if (variance <= 2) return "VERY_REGULAR";
        else if (variance <= 5) return "REGULAR";
        else if (variance <= 10) return "SOMEWHAT_IRREGULAR";
        else return "IRREGULAR";
    }
    
    private double calculateVariance(List<Integer> values) {
        double mean = values.stream().mapToInt(Integer::intValue).average().orElse(0);
        return values.stream()
            .mapToDouble(val -> Math.pow(val - mean, 2))
            .average()
            .orElse(0);
    }
    
    private List<String> generateHealthInsights(List<MenstrualCycle> cycles) {
        List<String> insights = new ArrayList<>();
        
        if (cycles.size() >= 3) {
            double avgCycleLength = cycles.stream()
                .filter(c -> c.getCycleLength() != null)
                .mapToInt(MenstrualCycle::getCycleLength)
                .average()
                .orElse(28);
                
            if (avgCycleLength < 21) {
                insights.add("Your cycles are shorter than average. Consider consulting a healthcare provider.");
            } else if (avgCycleLength > 35) {
                insights.add("Your cycles are longer than average. This might be normal for you, but consider tracking symptoms.");
            } else {
                insights.add("Your cycle length is within the normal range (21-35 days).");
            }
            
            // Check for regularity
            String regularity = calculateRegularity(cycles);
            switch (regularity) {
                case "VERY_REGULAR":
                    insights.add("Your cycles are very regular - great for planning!");
                    break;
                case "REGULAR":
                    insights.add("Your cycles are quite regular with minor variations.");
                    break;
                case "SOMEWHAT_IRREGULAR":
                    insights.add("Your cycles show some irregularity. Track symptoms and lifestyle factors.");
                    break;
                case "IRREGULAR":
                    insights.add("Your cycles are irregular. Consider discussing with a healthcare provider.");
                    break;
            }
        }
        
        // Check period length
        OptionalDouble avgPeriodLength = cycles.stream()
            .filter(c -> c.getPeriodLength() != null)
            .mapToInt(MenstrualCycle::getPeriodLength)
            .average();
            
        if (avgPeriodLength.isPresent()) {
            double avgPeriod = avgPeriodLength.getAsDouble();
            if (avgPeriod < 3) {
                insights.add("Your periods are shorter than average. Consider tracking flow intensity.");
            } else if (avgPeriod > 7) {
                insights.add("Your periods are longer than average. Monitor heavy flow days.");
            } else {
                insights.add("Your period length is within the normal range (3-7 days).");
            }
        }
        
        return insights;
    }
}

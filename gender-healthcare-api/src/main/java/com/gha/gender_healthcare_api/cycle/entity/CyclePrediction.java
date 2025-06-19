package com.gha.gender_healthcare_api.cycle.entity;

import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cycle_predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CyclePrediction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "predicted_ovulation_date")
    private LocalDate predictedOvulationDate;
    
    @Column(name = "fertile_window_start")
    private LocalDate fertileWindowStart;
    
    @Column(name = "fertile_window_end")
    private LocalDate fertileWindowEnd;
    
    @Column(name = "next_period_date")
    private LocalDate nextPeriodDate;
    
    @Column(name = "pregnancy_likelihood")
    private Double pregnancyLikelihood; // Percentage
    
    @Column(name = "calculation_date")
    private LocalDateTime calculationDate;
    
    @PrePersist
    protected void onCreate() {
        this.calculationDate = LocalDateTime.now();
    }
}

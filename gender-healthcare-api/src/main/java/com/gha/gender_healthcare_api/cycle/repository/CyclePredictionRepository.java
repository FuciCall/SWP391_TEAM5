package com.gha.gender_healthcare_api.cycle.repository;

import com.gha.gender_healthcare_api.cycle.entity.CyclePrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CyclePredictionRepository extends JpaRepository<CyclePrediction, Long> {
    
    Optional<CyclePrediction> findTopByUserUserIdOrderByCalculationDateDesc(Long userId);
}

package com.gha.gender_healthcare_api.cycle.repository;

import com.gha.gender_healthcare_api.cycle.entity.ContraceptiveReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContraceptiveReminderRepository extends JpaRepository<ContraceptiveReminder, Long> {
    
    List<ContraceptiveReminder> findByUserUserIdAndIsActiveTrue(Long userId);
    
    Optional<ContraceptiveReminder> findTopByUserUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);
    
    List<ContraceptiveReminder> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}

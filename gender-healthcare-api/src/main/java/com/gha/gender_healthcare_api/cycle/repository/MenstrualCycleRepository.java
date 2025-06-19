package com.gha.gender_healthcare_api.cycle.repository;

import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Long> {
    
    List<MenstrualCycle> findByUserUserIdOrderByStartDateDesc(Long userId);
    
    @Query("SELECT mc FROM MenstrualCycle mc WHERE mc.user.userId = :userId AND mc.startDate >= :fromDate ORDER BY mc.startDate DESC")
    List<MenstrualCycle> findByUserIdAndStartDateAfter(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);
    
    Optional<MenstrualCycle> findTopByUserUserIdOrderByStartDateDesc(Long userId);
    
    @Query("SELECT AVG(mc.cycleLength) FROM MenstrualCycle mc WHERE mc.user.userId = :userId AND mc.cycleLength IS NOT NULL")
    Optional<Double> findAverageCycleLengthByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(mc) FROM MenstrualCycle mc WHERE mc.user.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}

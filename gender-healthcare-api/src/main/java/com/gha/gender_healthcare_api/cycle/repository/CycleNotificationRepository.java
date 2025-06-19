package com.gha.gender_healthcare_api.cycle.repository;

import com.gha.gender_healthcare_api.cycle.entity.CycleNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CycleNotificationRepository extends JpaRepository<CycleNotification, Long> {
    
    List<CycleNotification> findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT n FROM CycleNotification n WHERE n.scheduledTime <= :now AND n.isSent = false")
    List<CycleNotification> findPendingNotifications(@Param("now") LocalDateTime now);
    
    List<CycleNotification> findByUserUserIdAndTypeOrderByCreatedAtDesc(Long userId, CycleNotification.NotificationType type);
    
    List<CycleNotification> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}

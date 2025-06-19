package com.gha.gender_healthcare_api.cycle.service;

import com.gha.gender_healthcare_api.cycle.entity.CycleNotification;
import com.gha.gender_healthcare_api.cycle.entity.CyclePrediction;
import com.gha.gender_healthcare_api.cycle.repository.CycleNotificationRepository;
import com.gha.gender_healthcare_api.cycle.repository.CyclePredictionRepository;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CycleNotificationService {
    
    @Autowired
    private CycleNotificationRepository notificationRepository;
    
    @Autowired
    private CyclePredictionRepository cyclePredictionRepository;
    
    @Autowired
    private EmailService emailService;
    
    public void scheduleOvulationReminders(Long userId) {
        Optional<CyclePrediction> predictionOpt = cyclePredictionRepository.findTopByUserUserIdOrderByCalculationDateDesc(userId);
        
        if (predictionOpt.isEmpty()) return;
        
        CyclePrediction prediction = predictionOpt.get();
        
        // Schedule ovulation reminder (1 day before)
        LocalDateTime ovulationReminder = prediction.getPredictedOvulationDate()
            .minusDays(1)
            .atTime(9, 0); // 9 AM
            
        scheduleNotification(
            userId,
            CycleNotification.NotificationType.OVULATION_REMINDER,
            "Ovulation Tomorrow",
            "Your ovulation is predicted for tomorrow. Your fertile window is active!",
            ovulationReminder
        );
        
        // Schedule fertile window start reminder
        LocalDateTime fertileWindowReminder = prediction.getFertileWindowStart()
            .atTime(8, 0); // 8 AM
            
        scheduleNotification(
            userId,
            CycleNotification.NotificationType.FERTILITY_WINDOW,
            "Fertile Window Started",
            "Your fertile window has started. Good luck if you're trying to conceive!",
            fertileWindowReminder
        );
        
        // Schedule next period reminder (2 days before)
        LocalDateTime periodReminder = prediction.getNextPeriodDate()
            .minusDays(2)
            .atTime(18, 0); // 6 PM
            
        scheduleNotification(
            userId,
            CycleNotification.NotificationType.PERIOD_REMINDER,
            "Period Expected Soon",
            "Your next period is expected in 2 days. Be prepared!",
            periodReminder
        );
    }
    
    public void scheduleNotification(Long userId, CycleNotification.NotificationType type, String title, String message, LocalDateTime scheduledTime) {
        CycleNotification notification = new CycleNotification();
        
        // Set User reference
        User user = new User();
        user.setUserId(userId);
        notification.setUser(user);
        
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setScheduledTime(scheduledTime);
        notification.setIsRead(false);
        notification.setIsSent(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    @Scheduled(fixedRate = 60000) // Check every minute
    public void processPendingNotifications() {
        List<CycleNotification> pendingNotifications = notificationRepository.findPendingNotifications(LocalDateTime.now());
        
        for (CycleNotification notification : pendingNotifications) {
            try {
                sendNotification(notification);
                notification.setIsSent(true);
                notification.setSentTime(LocalDateTime.now());
                notificationRepository.save(notification);
            } catch (Exception e) {
                log.error("Failed to send notification: " + notification.getId(), e);
            }
        }
    }
    
    private void sendNotification(CycleNotification notification) {
        User user = notification.getUser();
        
        // Send email notification (assuming EmailService exists)
        try {
            if (user.getEmail() != null) {
                emailService.sendCycleNotificationEmail(
                    user.getEmail(),
                    notification.getTitle(),
                    notification.getMessage()
                );
            }
        } catch (Exception e) {
            log.error("Failed to send email notification to user {}: {}", user.getUserId(), e.getMessage());
        }
        
        // TODO: Send push notification (if mobile app integration)
        // if (user.getPushToken() != null) {
        //     pushNotificationService.sendPushNotification(
        //         user.getPushToken(),
        //         notification.getTitle(),
        //         notification.getMessage()
        //     );
        // }
    }
    
    public List<CycleNotification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    public void markAsRead(Long notificationId, Long userId) {
        CycleNotification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
            
        if (!notification.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    public void cancelNotifications(Long userId, CycleNotification.NotificationType type) {
        List<CycleNotification> notifications = notificationRepository.findByUserUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        notifications.stream()
            .filter(n -> !n.getIsSent() && n.getScheduledTime().isAfter(LocalDateTime.now()))
            .forEach(n -> notificationRepository.delete(n));
    }
}

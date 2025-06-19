package com.gha.gender_healthcare_api.cycle.service;

import com.gha.gender_healthcare_api.cycle.dto.ContraceptiveReminderRequest;
import com.gha.gender_healthcare_api.cycle.entity.ContraceptiveReminder;
import com.gha.gender_healthcare_api.cycle.repository.ContraceptiveReminderRepository;
import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ContraceptiveReminderService {
    
    @Autowired
    private ContraceptiveReminderRepository reminderRepository;
    
    @Autowired
    private CycleNotificationService notificationService;
    
    public ContraceptiveReminder setupPillReminder(Long userId, ContraceptiveReminderRequest request) {
        // Deactivate existing reminders
        List<ContraceptiveReminder> existingReminders = reminderRepository.findByUserUserIdAndIsActiveTrue(userId);
        existingReminders.forEach(r -> r.setIsActive(false));
        reminderRepository.saveAll(existingReminders);
        
        ContraceptiveReminder reminder = new ContraceptiveReminder();
        
        // Set User reference
        User user = new User();
        user.setUserId(userId);
        reminder.setUser(user);
        
        reminder.setPillName(request.getPillName());
        reminder.setReminderTime(request.getReminderTime());
        reminder.setPackStartDate(request.getPackStartDate());
        reminder.setPackDuration(request.getPackDuration());
        reminder.setBreakDuration(request.getBreakDuration());
        reminder.setTimezone(request.getTimezone());
        reminder.setIsActive(true);
        reminder.setCreatedAt(LocalDateTime.now());
        
        ContraceptiveReminder savedReminder = reminderRepository.save(reminder);
        
        // Schedule daily pill reminders
        try {
            schedulePillReminders(savedReminder);
        } catch (Exception e) {
            log.error("Error scheduling pill reminders for user {}: {}", userId, e.getMessage());
        }
        
        return savedReminder;
    }
    
    private void schedulePillReminders(ContraceptiveReminder reminder) {
        LocalDate currentDate = reminder.getPackStartDate();
        LocalDate endDate = currentDate.plusMonths(3); // Schedule for 3 months
        
        while (currentDate.isBefore(endDate)) {
            // Check if it's a pill day (not break days)
            if (isPillDay(currentDate, reminder)) {
                LocalDateTime reminderDateTime = currentDate.atTime(reminder.getReminderTime());
                
                notificationService.scheduleNotification(
                    reminder.getUser().getUserId(),
                    com.gha.gender_healthcare_api.cycle.entity.CycleNotification.NotificationType.CONTRACEPTIVE_PILL_REMINDER,
                    "Time for your pill!",
                    String.format("Don't forget to take your %s", reminder.getPillName()),
                    reminderDateTime
                );
            }
            currentDate = currentDate.plusDays(1);
        }
    }
    
    private boolean isPillDay(LocalDate date, ContraceptiveReminder reminder) {
        long daysSinceStart = ChronoUnit.DAYS.between(reminder.getPackStartDate(), date);
        int cycleLength = reminder.getPackDuration() + reminder.getBreakDuration();
        int dayInCycle = (int) (daysSinceStart % cycleLength);
        
        return dayInCycle < reminder.getPackDuration(); // Active pill days
    }
    
    public ContraceptiveReminder getUserActiveReminder(Long userId) {
        Optional<ContraceptiveReminder> reminder = reminderRepository.findTopByUserUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
        return reminder.orElse(null);
    }
    
    public void deactivateReminder(Long userId, Long reminderId) {
        ContraceptiveReminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new EntityNotFoundException("Reminder not found"));
            
        if (!reminder.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        
        reminder.setIsActive(false);
        reminderRepository.save(reminder);
        
        // Cancel future notifications
        notificationService.cancelNotifications(userId, com.gha.gender_healthcare_api.cycle.entity.CycleNotification.NotificationType.CONTRACEPTIVE_PILL_REMINDER);
    }
}

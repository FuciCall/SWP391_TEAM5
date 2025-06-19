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

/**
 * Service class cho chức năng nhắc nhở thuốc tránh thai
 * Xử lý các hoạt động liên quan đến việc thiết lập, lên lịch và quản lý nhắc nhở uống thuốc tránh thai
 */
@Service
@Transactional
@Slf4j
public class ContraceptiveReminderService {
    
    @Autowired
    private ContraceptiveReminderRepository reminderRepository;
    
    @Autowired
    private CycleNotificationService notificationService;
      /**
     * Thiết lập nhắc nhở uống thuốc tránh thai cho người dùng
     * 
     * @param userId ID của người dùng
     * @param request Thông tin cấu hình nhắc nhở (tên thuốc, giờ nhắc, chu kỳ, ...)
     * @return ContraceptiveReminder đã được tạo và lưu vào database
     * 
     * Quy trình:
     * 1. Tắt tất cả nhắc nhở cũ của user (để tránh trùng lặp)
     * 2. Tạo nhắc nhở mới với thông tin từ request
     * 3. Lưu vào database
     * 4. Lên lịch các thông báo hàng ngày
     */
    public ContraceptiveReminder setupPillReminder(Long userId, ContraceptiveReminderRequest request) {
        // Deactivate existing reminders to avoid duplicates
        List<ContraceptiveReminder> existingReminders = reminderRepository.findByUserUserIdAndIsActiveTrue(userId);
        existingReminders.forEach(r -> r.setIsActive(false));
        reminderRepository.saveAll(existingReminders);
        
        // Create new reminder
        ContraceptiveReminder reminder = new ContraceptiveReminder();
        
        // Set User reference (lazy loading - only need ID)
        User user = new User();
        user.setUserId(userId);
        reminder.setUser(user);
        
        // Fill information from request
        reminder.setPillName(request.getPillName());               // Pill name
        reminder.setReminderTime(request.getReminderTime());       // Daily reminder time
        reminder.setPackStartDate(request.getPackStartDate());     // Pack start date
        reminder.setPackDuration(request.getPackDuration());       // Days to take pills in cycle
        reminder.setBreakDuration(request.getBreakDuration());     // Break days between cycles
        reminder.setTimezone(request.getTimezone());               // User timezone
        reminder.setIsActive(true);                                // Activate reminder
        reminder.setCreatedAt(LocalDateTime.now());                // Creation timestamp
        
        // Save to database
        ContraceptiveReminder savedReminder = reminderRepository.save(reminder);
        
        // Schedule daily pill reminders
        try {
            schedulePillReminders(savedReminder);
            log.info("Successfully scheduled pill reminders for user {} with pill {}", userId, request.getPillName());
        } catch (Exception e) {
            log.error("Error scheduling pill reminders for user {}: {}", userId, e.getMessage());
        }
        
        return savedReminder;
    }    /**
     * Lên lịch các thông báo nhắc nhở uống thuốc cho 3 tháng tới
     * 
     * @param reminder Đối tượng ContraceptiveReminder chứa thông tin cấu hình
     * 
     * Quy trình:
     * 1. Tính toán các ngày cần nhắc nhở (loại bỏ ngày nghỉ)
     * 2. Tạo thông báo cho mỗi ngày uống thuốc
     * 3. Gửi lệnh lên lịch đến NotificationService
     */
    private void schedulePillReminders(ContraceptiveReminder reminder) {
        LocalDate currentDate = reminder.getPackStartDate();      // Start date
        LocalDate endDate = currentDate.plusMonths(3);            // Schedule for 3 months ahead
        
        log.debug("Scheduling pill reminders from {} to {} for user {}", 
                  currentDate, endDate, reminder.getUser().getUserId());
        
        // Loop through each day in the time range
        while (currentDate.isBefore(endDate)) {
            // Check if current date is a pill day (not break day)
            if (isPillDay(currentDate, reminder)) {
                // Create reminder datetime: date + configured time
                LocalDateTime reminderDateTime = currentDate.atTime(reminder.getReminderTime());
                
                // Create notification reminder                notificationService.scheduleNotification(
                    reminder.getUser().getUserId(),
                    com.gha.gender_healthcare_api.cycle.entity.CycleNotification.NotificationType.CONTRACEPTIVE_PILL_REMINDER,
                    "Time for your pill!", // Tiêu đề thông báo bằng tiếng Anh
                    String.format("Don't forget to take your %s", reminder.getPillName()), // Nội dung bằng tiếng Anh
                    reminderDateTime
                );
            }
            currentDate = currentDate.plusDays(1); // Move to next day
        }
        
        log.info("Completed scheduling pill reminders for user {}", reminder.getUser().getUserId());
    }    /**
     * Kiểm tra xem một ngày cụ thể có phải là ngày uống thuốc hay không
     * 
     * @param date Ngày cần kiểm tra
     * @param reminder Cấu hình nhắc nhở chứa thông tin chu kỳ
     * @return true nếu là ngày uống thuốc, false nếu là ngày nghỉ
     * 
     * Logic:
     * 1. Tính số ngày từ ngày bắt đầu đến ngày hiện tại
     * 2. Tìm vị trí trong chu kỳ (chu kỳ = ngày uống + ngày nghỉ)
     * 3. Nếu vị trí < số ngày uống thuốc → là ngày uống thuốc
     * 4. Ngược lại → là ngày nghỉ
     * 
     * Ví dụ: Uống 21 ngày, nghỉ 7 ngày
     * - Ngày 1-21: uống thuốc (return true)
     * - Ngày 22-28: nghỉ (return false) 
     * - Ngày 29-49: uống thuốc chu kỳ mới (return true)
     */
    private boolean isPillDay(LocalDate date, ContraceptiveReminder reminder) {
        // Calculate days since pack start date
        long daysSinceStart = ChronoUnit.DAYS.between(reminder.getPackStartDate(), date);
        
        // Calculate complete cycle length (pill days + break days)
        int cycleLength = reminder.getPackDuration() + reminder.getBreakDuration();
        
        // Find position in current cycle (0 to cycleLength-1)
        int dayInCycle = (int) (daysSinceStart % cycleLength);
        
        // If position < pill duration → this is a pill day
        boolean isPillDay = dayInCycle < reminder.getPackDuration();
        
        log.trace("Date {}: daysSinceStart={}, dayInCycle={}, isPillDay={}", 
                  date, daysSinceStart, dayInCycle, isPillDay);
        
        return isPillDay;
    }    /**
     * Lấy thông tin nhắc nhở thuốc tránh thai đang hoạt động của người dùng
     * 
     * @param userId ID của người dùng
     * @return ContraceptiveReminder nếu có, null nếu không có nhắc nhở nào đang hoạt động
     * 
     * Chức năng:
     * - Tìm nhắc nhở mới nhất đang hoạt động (isActive = true)
     * - Sắp xếp theo thời gian tạo giảm dần để lấy cái mới nhất
     * - Trả về null nếu user chưa thiết lập nhắc nhở nào
     */
    public ContraceptiveReminder getUserActiveReminder(Long userId) {
        log.debug("Getting active contraceptive reminder for user {}", userId);
        
        Optional<ContraceptiveReminder> reminder = reminderRepository
            .findTopByUserUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
        
        if (reminder.isPresent()) {
            log.info("Found active reminder for user {}: pill {}", userId, reminder.get().getPillName());
            return reminder.get();
        } else {
            log.info("No active contraceptive reminder found for user {}", userId);
            return null;
        }
    }    /**
     * Vô hiệu hóa nhắc nhở thuốc tránh thai
     * 
     * @param userId ID của người dùng (để kiểm tra quyền sở hữu)
     * @param reminderId ID của nhắc nhở cần vô hiệu hóa
     * @throws EntityNotFoundException nếu không tìm thấy nhắc nhở
     * @throws AccessDeniedException nếu user không phải chủ sở hữu nhắc nhở
     * 
     * Quy trình:
     * 1. Tìm nhắc nhở theo ID
     * 2. Kiểm tra quyền sở hữu (bảo mật)
     * 3. Đặt isActive = false
     * 4. Hủy tất cả thông báo tương lai
     */
    public void deactivateReminder(Long userId, Long reminderId) {
        log.debug("Deactivating contraceptive reminder {} for user {}", reminderId, userId);
        
        // Find reminder by ID        ContraceptiveReminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> {
                log.error("Reminder not found: {}", reminderId);
                return new EntityNotFoundException("Reminder not found with ID: " + reminderId);
            });
        
        // Check ownership (only owner can deactivate)
        if (!reminder.getUser().getUserId().equals(userId)) {
            log.error("Access denied: user {} trying to deactivate reminder {} owned by user {}", 
                      userId, reminderId, reminder.getUser().getUserId());
            throw new AccessDeniedException("Access denied to deactivate this reminder");
        }
        
        // Deactivate reminder
        reminder.setIsActive(false);
        reminderRepository.save(reminder);
        
        // Cancel all future contraceptive pill reminder notifications
        notificationService.cancelNotifications(
            userId, 
            com.gha.gender_healthcare_api.cycle.entity.CycleNotification.NotificationType.CONTRACEPTIVE_PILL_REMINDER
        );
        
        log.info("Successfully deactivated contraceptive reminder {} for user {}", reminderId, userId);
    }
}

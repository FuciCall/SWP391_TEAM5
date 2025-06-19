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

/**
 * Service quản lý hệ thống thông báo tự động cho chu kỳ sinh sản
 * Chức năng chính:
 * 1. Lên lịch thông báo dựa trên dự đoán chu kỳ
 * 2. Xử lý gửi email/push notification theo schedule
 * 3. Quản lý trạng thái đọc/chưa đọc của notification
 * 4. Tích hợp với Spring Scheduler để chạy background job
 */
@Service
@Transactional
@Slf4j
public class CycleNotificationService {
      /**
     * Repository để lưu và truy cập thông báo chu kỳ
     */
    @Autowired
    private CycleNotificationRepository notificationRepository;
    
    /**
     * Repository để truy cập dự đoán chu kỳ (cần để lên lịch thông báo)
     */
    @Autowired
    private CyclePredictionRepository cyclePredictionRepository;
    
    /**
     * Service gửi email - tích hợp với existing EmailService
     */
    @Autowired
    private EmailService emailService;    /**
     * Lên lịch các thông báo liên quan đến rụng trứng và chu kỳ
     * Được gọi sau khi có dự đoán chu kỳ mới để tạo các reminder tự động
     * 
     * Tạo 3 loại thông báo chính:
     * 1. Nhắc nhở rụng trứng (1 ngày trước ovulation) - 9:00 AM
     * 2. Thông báo cửa sổ sinh sản bắt đầu - 8:00 AM  
     * 3. Nhắc nhở chu kỳ sắp đến (2 ngày trước period) - 6:00 PM
     * 
     * @param userId ID của user cần lên lịch thông báo
     */
    public void scheduleOvulationReminders(Long userId) {
        log.debug("Scheduling ovulation reminders for user {}", userId);
        
        // Lấy dự đoán chu kỳ mới nhất cho user
        Optional<CyclePrediction> predictionOpt = cyclePredictionRepository
            .findTopByUserUserIdOrderByCalculationDateDesc(userId);
        
        if (predictionOpt.isEmpty()) {
            log.warn("No prediction found for user {}, cannot schedule ovulation reminders", userId);
            return;
        }
        
        CyclePrediction prediction = predictionOpt.get();
        log.info("Scheduling reminders based on prediction: ovulation={}, fertile window={}-{}, next period={}", 
                 prediction.getPredictedOvulationDate(), 
                 prediction.getFertileWindowStart(), 
                 prediction.getFertileWindowEnd(),
                 prediction.getNextPeriodDate());
          // 1. Lên lịch nhắc nhở rụng trứng (1 ngày trước rụng trứng)
        // Thời gian: 9:00 sáng để user chuẩn bị
        LocalDateTime ovulationReminder = prediction.getPredictedOvulationDate()
            .minusDays(1)
            .atTime(9, 0);
              scheduleNotification(
            userId,
            CycleNotification.NotificationType.OVULATION_REMINDER,
            "Ovulation Tomorrow", // Tiêu đề bằng tiếng Anh
            "Your ovulation is predicted for tomorrow. Your fertile window is active!", // Nội dung bằng tiếng Anh
            ovulationReminder
        );
          // 2. Lên lịch thông báo bắt đầu cửa sổ sinh sản
        // Thời gian: 8:00 sáng vào ngày đầu của cửa sổ sinh sản
        LocalDateTime fertileWindowReminder = prediction.getFertileWindowStart()
            .atTime(8, 0);
              scheduleNotification(
            userId,
            CycleNotification.NotificationType.FERTILITY_WINDOW,
            "Fertile Window Started", // Tiêu đề bằng tiếng Anh
            "Your fertile window has started. Good luck if you're trying to conceive!", // Nội dung bằng tiếng Anh
            fertileWindowReminder
        );
          // 3. Lên lịch nhắc nhở kinh nguyệt (2 ngày trước)
        // Thời gian: 6:00 chiều để user chuẩn bị
        LocalDateTime periodReminder = prediction.getNextPeriodDate()
            .minusDays(2)
            .atTime(18, 0);
              scheduleNotification(
            userId,
            CycleNotification.NotificationType.PERIOD_REMINDER,
            "Period Expected Soon", // Tiêu đề bằng tiếng Anh
            "Your next period is expected in 2 days. Be prepared!", // Nội dung bằng tiếng Anh
            periodReminder
        );
        
        log.info("Successfully scheduled 3 ovulation reminders for user {}", userId);
    }    /**
     * Tạo và lưu một thông báo mới vào database
     * Thông báo sẽ được xử lý bởi background scheduler để gửi đúng thời gian
     * 
     * @param userId ID của user nhận thông báo
     * @param type Loại thông báo (OVULATION_REMINDER, FERTILITY_WINDOW, PERIOD_REMINDER, CONTRACEPTIVE_PILL_REMINDER)
     * @param title Tiêu đề thông báo (hiển thị trên notification)
     * @param message Nội dung chi tiết thông báo
     * @param scheduledTime Thời gian dự kiến gửi thông báo
     * 
     * Luồng hoạt động:
     * 1. Tạo entity CycleNotification mới
     * 2. Set các thuộc tính cần thiết
     * 3. Lưu vào database với trạng thái chưa gửi (isSent = false)
     * 4. Background job sẽ periodically check và gửi khi đến thời gian
     */
    public void scheduleNotification(Long userId, CycleNotification.NotificationType type, 
                                   String title, String message, LocalDateTime scheduledTime) {
        log.debug("Scheduling notification for user {}: type={}, scheduledTime={}", userId, type, scheduledTime);
        
        CycleNotification notification = new CycleNotification();
        
        // Thiết lập tham chiếu User (lazy loading - chỉ cần ID)
        User user = new User();
        user.setUserId(userId);
        notification.setUser(user);
          // Thiết lập thông tin thông báo
        notification.setType(type);                                 // Loại thông báo
        notification.setTitle(title);                              // Tiêu đề
        notification.setMessage(message);                          // Nội dung
        notification.setScheduledTime(scheduledTime);              // Thời gian gửi đã lên lịch
        notification.setIsRead(false);                             // Chưa đọc
        notification.setIsSent(false);                             // Chưa gửi
        notification.setCreatedAt(LocalDateTime.now());            // Timestamp tạo
        
        CycleNotification savedNotification = notificationRepository.save(notification);
        log.info("Successfully scheduled notification {} for user {} at {}", 
                 savedNotification.getId(), userId, scheduledTime);
    }
      /**
     * Background job chạy mỗi phút để kiểm tra và gửi thông báo đến hạn
     * Sử dụng Spring @Scheduled annotation để tự động chạy
     * 
     * Quy trình xử lý:
     * 1. Query database tìm notifications có scheduled_time <= hiện tại và chưa gửi (isSent = false)
     * 2. Với mỗi notification: gửi email, cập nhật trạng thái thành đã gửi
     * 3. Log lỗi nếu gửi thất bại nhưng không throw exception (để không block các notification khác)
     * 4. Retry mechanism có thể được thêm vào sau này
     * 
     * Lưu ý:
     * - Job chạy mỗi 60 giây (60000ms) để đảm bảo không miss notification
     * - Sử dụng @Transactional để đảm bảo consistency
     * - Có exception handling để job không bị crash
     */
    @Scheduled(fixedRate = 60000) // Kiểm tra mỗi phút
    public void processPendingNotifications() {
        log.debug("Processing pending notifications at {}", LocalDateTime.now());
        
        // Tìm tất cả notifications đến hạn và chưa gửi
        List<CycleNotification> pendingNotifications = notificationRepository
            .findPendingNotifications(LocalDateTime.now());
        
        if (pendingNotifications.isEmpty()) {
            log.trace("No pending notifications found");
            return;
        }
        
        log.info("Found {} pending notifications to process", pendingNotifications.size());
        
        int successCount = 0;
        int failCount = 0;
        
        // Xử lý từng notification
        for (CycleNotification notification : pendingNotifications) {
            try {
                log.debug("Processing notification {} for user {} (type: {})", 
                         notification.getId(), notification.getUser().getUserId(), notification.getType());
                
                // Gửi notification (email/push)
                sendNotification(notification);
                
                // Cập nhật trạng thái đã gửi
                notification.setIsSent(true);
                notification.setSentTime(LocalDateTime.now());
                notificationRepository.save(notification);
                
                successCount++;
                log.info("Successfully sent notification {} to user {}", 
                        notification.getId(), notification.getUser().getUserId());
                
            } catch (Exception e) {
                failCount++;
                log.error("Failed to send notification {} to user {}: {}", 
                         notification.getId(), notification.getUser().getUserId(), e.getMessage(), e);
                // Không throw exception để tiếp tục xử lý các notification khác
            }
        }
        
        log.info("Notification processing completed: {} success, {} failed", successCount, failCount);
    }
      /**
     * Gửi thông báo thực tế qua email (và push notification nếu có)
     * Hiện tại chỉ hỗ trợ email, có thể mở rộng cho push notification mobile app
     * 
     * @param notification CycleNotification cần gửi
     * @throws Exception nếu gửi email thất bại
     * 
     * Luồng hoạt động:
     * 1. Lấy thông tin user từ notification
     * 2. Gửi email sử dụng existing EmailService
     * 3. TODO: Gửi push notification cho mobile app (nếu có)
     * 4. TODO: Gửi SMS notification (nếu user enable)
     */
    private void sendNotification(CycleNotification notification) {
        User user = notification.getUser();
        log.debug("Sending notification to user {}: {}", user.getUserId(), notification.getTitle());
        
        // Gửi email thông báo (sử dụng EmailService hiện có)
        try {
            if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                emailService.sendCycleNotificationEmail(
                    user.getEmail(),
                    notification.getTitle(),
                    notification.getMessage()
                );
                log.info("Successfully sent email notification to {}", user.getEmail());
            } else {
                log.warn("User {} has no email address, skipping email notification", user.getUserId());
            }
        } catch (Exception e) {
            log.error("Failed to send email notification to user {}: {}", user.getUserId(), e.getMessage());
            throw e; // Ném lại để caller biết có lỗi
        }
        
        // TODO: Gửi thông báo đẩy (khi có tích hợp ứng dụng di động)
        // if (user.getPushToken() != null && !user.getPushToken().trim().isEmpty()) {
        //     try {
        //         pushNotificationService.sendPushNotification(
        //             user.getPushToken(),
        //             notification.getTitle(),
        //             notification.getMessage()
        //         );
        //         log.info("Successfully sent push notification to user {}", user.getUserId());
        //     } catch (Exception e) {
        //         log.error("Failed to send push notification to user {}: {}", user.getUserId(), e.getMessage());
        //         // Không throw exception cho push notification để email vẫn được gửi
        //     }
        // }
        
        // TODO: Gửi thông báo SMS (nếu user bật và có số điện thoại)
        // if (user.getPhoneNumber() != null && user.isSmsNotificationEnabled()) {
        //     try {
        //         smsService.sendSMS(
        //             user.getPhoneNumber(),
        //             notification.getTitle() + ": " + notification.getMessage()
        //         );
        //         log.info("Successfully sent SMS notification to user {}", user.getUserId());
        //     } catch (Exception e) {
        //         log.error("Failed to send SMS notification to user {}: {}", user.getUserId(), e.getMessage());
        //     }
        // }
    }
      /**
     * Lấy danh sách thông báo chưa đọc của user
     * Dùng để hiển thị notification center trong UI hoặc mobile app
     * 
     * @param userId ID của user
     * @return Danh sách CycleNotification chưa đọc, sắp xếp theo thời gian tạo giảm dần (mới nhất trước)
     * 
     * Chức năng:
     * - Trả về tất cả notifications có isRead = false
     * - Sắp xếp theo created_at DESC để notification mới nhất ở đầu
     * - Chỉ trả về notifications của user cụ thể (bảo mật)
     * - UI có thể hiển thị số lượng unread notifications như badge
     */
    public List<CycleNotification> getUserNotifications(Long userId) {
        log.debug("Getting unread notifications for user {}", userId);
        
        List<CycleNotification> notifications = notificationRepository
            .findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        
        log.info("Found {} unread notifications for user {}", notifications.size(), userId);
        return notifications;
    }
      /**
     * Đánh dấu một thông báo là đã đọc
     * Có kiểm tra quyền truy cập (user chỉ có thể mark notification của mình)
     * 
     * @param notificationId ID của notification cần đánh dấu đã đọc
     * @param userId ID của user (để kiểm tra quyền sở hữu)
     * @throws EntityNotFoundException nếu notification không tồn tại
     * @throws AccessDeniedException nếu user không có quyền truy cập notification này
     * 
     * Quy trình:
     * 1. Tìm notification theo ID
     * 2. Kiểm tra xem notification có thuộc về user không (security check)
     * 3. Cập nhật isRead = true
     * 4. Lưu vào database
     * 
     * Sử dụng:
     * - Khi user click vào notification trong UI
     * - Khi user xem chi tiết notification
     * - API endpoint: PUT /api/cycle/notifications/{id}/mark-read
     */
    public void markAsRead(Long notificationId, Long userId) {
        log.debug("Marking notification {} as read for user {}", notificationId, userId);
        
        // Tìm thông báo theo ID        CycleNotification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> {
                log.error("Notification not found: {}", notificationId);
                return new EntityNotFoundException("Notification not found with ID: " + notificationId);
            });
        
        // Kiểm tra quyền truy cập (chỉ chủ sở hữu mới được đánh dấu đã đọc)
        if (!notification.getUser().getUserId().equals(userId)) {
            log.error("Access denied: user {} trying to mark notification {} owned by user {}", 
                      userId, notificationId, notification.getUser().getUserId());
            throw new AccessDeniedException("Access denied to this notification");
        }
        
        // Đánh dấu đã đọc
        notification.setIsRead(true);
        notificationRepository.save(notification);
        
        log.info("Successfully marked notification {} as read for user {}", notificationId, userId);
    }
      /**
     * Hủy các thông báo chưa gửi của một loại cụ thể
     * Dùng khi user tắt reminder hoặc thay đổi cấu hình notification
     * 
     * @param userId ID của user
     * @param type Loại notification cần hủy (OVULATION_REMINDER, CONTRACEPTIVE_PILL_REMINDER, etc.)
     * 
     * Quy trình:
     * 1. Tìm tất cả notifications của user với type cụ thể
     * 2. Lọc ra những notifications chưa gửi (isSent = false) và scheduled_time > hiện tại
     * 3. Xóa những notifications này khỏi database
     * 
     * Sử dụng:
     * - Khi user vô hiệu hóa contraceptive reminder
     * - Khi user thay đổi chu kỳ và cần hủy predictions cũ
     * - Khi user tắt specific type của notification trong settings
     * 
     * Lưu ý:
     * - Chỉ xóa notifications chưa gửi để không ảnh hưởng lịch sử
     * - Không xóa notifications đã gửi để giữ audit trail
     */
    public void cancelNotifications(Long userId, CycleNotification.NotificationType type) {
        log.debug("Canceling notifications for user {} with type {}", userId, type);
        
        // Tìm tất cả thông báo của user với loại cụ thể
        List<CycleNotification> notifications = notificationRepository
            .findByUserUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        
        // Lọc và đếm thông báo cần hủy
        List<CycleNotification> notificationsToCancel = notifications.stream()
            .filter(n -> !n.getIsSent() && n.getScheduledTime().isAfter(LocalDateTime.now()))
            .toList();
        
        if (notificationsToCancel.isEmpty()) {
            log.info("No future notifications to cancel for user {} with type {}", userId, type);
            return;
        }
        
        log.info("Canceling {} notifications for user {} with type {}", 
                 notificationsToCancel.size(), userId, type);
        
        // Xóa thông báo khỏi database
        notificationsToCancel.forEach(notification -> {
            log.debug("Canceling notification {} scheduled for {}", 
                     notification.getId(), notification.getScheduledTime());
            notificationRepository.delete(notification);
        });
        
        log.info("Successfully canceled {} notifications for user {} with type {}", 
                 notificationsToCancel.size(), userId, type);
    }
}

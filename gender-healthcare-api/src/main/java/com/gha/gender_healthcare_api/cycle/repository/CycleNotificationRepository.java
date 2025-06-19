package com.gha.gender_healthcare_api.cycle.repository;

import com.gha.gender_healthcare_api.cycle.entity.CycleNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface cho thực thể CycleNotification
 * 
 * Cung cấp các phương thức truy cập dữ liệu thông báo chu kỳ từ database.
 * Kế thừa JpaRepository để có sẵn các CRUD operations cơ bản.
 * 
 * Các custom query methods:
 * - Tìm notifications chưa đọc của user
 * - Tìm notifications đến hạn gửi (cho background job)
 * - Lọc notifications theo loại và user
 * - Lấy toàn bộ lịch sử notifications của user
 * 
 * Được sử dụng bởi:
 * - CycleNotificationService (business logic)
 * - Background scheduler (xử lý gửi notification)
 * - Controller (API endpoints)
 */
@Repository
public interface CycleNotificationRepository extends JpaRepository<CycleNotification, Long> {
    
    /**
     * Tìm tất cả thông báo chưa đọc của user, sắp xếp theo thời gian tạo giảm dần
     * 
     * @param userId ID của user
     * @return Danh sách CycleNotification chưa đọc (isRead = false), mới nhất trước
     * 
     * Sử dụng:
     * - Hiển thị notification center trong UI
     * - Đếm số notifications chưa đọc (badge)
     * - API endpoint GET /api/cycle/notifications
     */
    List<CycleNotification> findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    /**
     * Tìm tất cả notifications đến hạn gửi và chưa được gửi
     * 
     * @param now Thời gian hiện tại
     * @return Danh sách CycleNotification có scheduled_time <= now và isSent = false
     * 
     * Sử dụng:
     * - Background job @Scheduled chạy mỗi phút
     * - Xử lý gửi email/push notification đúng thời gian
     * - Đảm bảo không gửi notification muộn
     * 
     * Quan trọng: Query này là core của hệ thống notification scheduling
     */
    @Query("SELECT n FROM CycleNotification n WHERE n.scheduledTime <= :now AND n.isSent = false")
    List<CycleNotification> findPendingNotifications(@Param("now") LocalDateTime now);
    
    /**
     * Tìm tất cả notifications của user theo loại cụ thể
     * 
     * @param userId ID của user
     * @param type Loại notification (OVULATION_REMINDER, CONTRACEPTIVE_PILL_REMINDER, etc.)
     * @return Danh sách CycleNotification của loại cụ thể, sắp xếp theo thời gian tạo giảm dần
     * 
     * Sử dụng:
     * - Hủy notifications cụ thể khi user tắt reminder
     * - Lọc notifications theo type trong UI
     * - Thống kê số lượng notifications theo loại
     */
    List<CycleNotification> findByUserUserIdAndTypeOrderByCreatedAtDesc(Long userId, CycleNotification.NotificationType type);
    
    /**
     * Tìm tất cả notifications của user (kể cả đã đọc và chưa đọc)
     * 
     * @param userId ID của user
     * @return Danh sách tất cả CycleNotification của user, sắp xếp theo thời gian tạo giảm dần
     * 
     * Sử dụng:
     * - Hiển thị toàn bộ lịch sử notifications
     * - Admin panel để xem notifications của user
     * - Export data cho phân tích
     * - API endpoint GET /api/cycle/notifications/all
     */
    List<CycleNotification> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}

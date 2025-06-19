package com.gha.gender_healthcare_api.cycle.repository;

import com.gha.gender_healthcare_api.cycle.entity.ContraceptiveReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho thực thể ContraceptiveReminder
 * 
 * Cung cấp các phương thức truy cập dữ liệu nhắc nhở thuốc tránh thai từ database.
 * Kế thừa JpaRepository để có sẵn các CRUD operations cơ bản.
 * 
 * Đặc điểm quan trọng:
 * - Mỗi user chỉ nên có 1 reminder active tại một thời điểm
 * - Khi tạo reminder mới, reminder cũ sẽ được đặt isActive = false
 * - Sử dụng soft delete (isActive) thay vì hard delete để giữ lịch sử
 * 
 * Các custom query methods:
 * - Tìm reminders đang hoạt động của user
 * - Lấy reminder mới nhất đang hoạt động
 * - Lấy toàn bộ lịch sử reminders (cả active và inactive)
 */
@Repository
public interface ContraceptiveReminderRepository extends JpaRepository<ContraceptiveReminder, Long> {
    
    /**
     * Tìm tất cả reminders đang hoạt động của user
     * 
     * @param userId ID của user
     * @return Danh sách ContraceptiveReminder có isActive = true
     * 
     * Sử dụng:
     * - Vô hiệu hóa reminders cũ khi tạo reminder mới
     * - Kiểm tra xem user có reminders đang hoạt động không
     * - Admin panel để xem reminders active của user
     * 
     * Lưu ý: Thông thường sẽ chỉ có 0 hoặc 1 kết quả, nhưng dùng List để đảm bảo
     * có thể xử lý trường hợp có nhiều reminders active (data inconsistency)
     */
    List<ContraceptiveReminder> findByUserUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Tìm reminder mới nhất đang hoạt động của user
     * 
     * @param userId ID của user
     * @return Optional<ContraceptiveReminder> reminder mới nhất có isActive = true, empty nếu không có
     * 
     * Sử dụng:
     * - Lấy thông tin reminder hiện tại để hiển thị trong UI
     * - Kiểm tra xem user đã thiết lập reminder chưa
     * - API endpoint GET /api/cycle/contraceptive/active
     * 
     * Quan trọng: Sắp xếp theo created_at DESC để lấy reminder mới nhất
     * trong trường hợp có nhiều reminders active
     */
    Optional<ContraceptiveReminder> findTopByUserUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);
    
    /**
     * Tìm tất cả reminders của user (cả active và inactive)
     * 
     * @param userId ID của user
     * @return Danh sách tất cả ContraceptiveReminder của user, sắp xếp theo thời gian tạo giảm dần
     * 
     * Sử dụng:
     * - Hiển thị lịch sử reminders trong UI
     * - Admin panel để audit reminders của user
     * - Export data cho phân tích
     * - Tracking việc thay đổi thuốc tránh thai của user
     */
    List<ContraceptiveReminder> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}

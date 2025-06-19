package com.gha.gender_healthcare_api.cycle.entity;

import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity quản lý nhắc nhở uống thuốc tránh thai hàng ngày
 * 
 * Chức năng chính:
 * - Lưu trữ thông tin thuốc và thời gian nhắc nhở của user
 * - Tự động tính toán ngày bắt đầu pack mới theo chu kỳ thuốc
 * - Hỗ trợ các loại pack: 21 ngày uống + 7 ngày nghỉ, 28 ngày liên tục
 * - Tracking việc uống thuốc và số viên còn lại trong pack
 * 
 * Hoạt động:
 * - Scheduler sẽ check daily để gửi notification theo reminder_time
 * - Tự động reset pack khi hết thuốc theo pack_duration
 * - User có thể mark đã uống thuốc để update remaining_pills
 * - Cảnh báo khi sắp hết thuốc (< 3 viên)
 */
@Entity
@Table(name = "contraceptive_reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContraceptiveReminder {
    /**
     * Khóa chính của bảng contraceptive_reminders
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Quan hệ nhiều-một với thực thể User
     * Mỗi user có thể có nhiều reminder nhưng chỉ một cái đang hoạt động
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Tên thuốc tránh thai (ví dụ: Yasmin, Diane-35, Mercilon)
     * Hiển thị trong thông báo để dễ nhận biết
     */
    @Column(name = "pill_name", nullable = false)
    private String pillName;
    
    /**
     * Thời gian nhắc nhở hàng ngày theo định dạng HH:mm
     * Dùng để lên lịch thông báo
     */
    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime;
    
    /**
     * Trạng thái hoạt động của nhắc nhở
     * Chỉ một reminder được phép hoạt động cho mỗi user
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Ngày bắt đầu vỉ thuốc hiện tại
     * Dùng để tính toán ngày uống thuốc và ngày nghỉ
     */
    @Column(name = "pack_start_date")
    private LocalDate packStartDate;    /**
     * Số ngày uống thuốc trong một vỉ (thường là 21 hoặc 28 ngày)
     * 21 ngày: uống 21 ngày, nghỉ 7 ngày
     * 28 ngày: uống liên tục không nghỉ
     */
    @Column(name = "pack_duration")
    private Integer packDuration;
    
    /**
     * Số ngày nghỉ giữa các vỉ (thường là 7 ngày)
     * 7 ngày: cho vỉ 21 viên
     * 0 ngày: cho vỉ 28 viên (uống liên tục)
     */
    @Column(name = "break_duration")
    private Integer breakDuration;
    
    /**
     * Múi giờ của người dùng để lên lịch nhắc nhở chính xác
     * Ví dụ: "Asia/Ho_Chi_Minh", "UTC", "America/New_York"
     */
    @Column(name = "timezone")
    private String timezone;
    
    /**
     * Thời gian tạo nhắc nhở
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Phương thức callback JPA để tự động gán thời gian created_at
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

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
public class ContraceptiveReminder {    /**
     * Primary key of contraceptive_reminders table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Many-to-One relationship with User entity
     * Each user can have multiple reminders but only one active at a time
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Name of contraceptive pill (e.g., Yasmin, Diane-35, Mercilon)
     * Displayed in notifications for easy identification
     */
    @Column(name = "pill_name", nullable = false)
    private String pillName;
    
    /**
     * Daily reminder time in HH:mm format
     * Used to schedule notifications
     */
    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime;
    
    /**
     * Active status of the reminder
     * Only one reminder should be active per user
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Start date of current pill pack
     * Used to calculate pill days vs break days
     */
    @Column(name = "pack_start_date")
    private LocalDate packStartDate;
      /**
     * Number of days to take pills in one pack (typically 21 or 28)
     * 21 days: take for 21 days, break for 7 days
     * 28 days: continuous pills with no break
     */
    @Column(name = "pack_duration")
    private Integer packDuration;
    
    /**
     * Number of break days between packs (typically 7 days)
     * 7 days: for 21-pill packs
     * 0 days: for 28-pill packs (continuous)
     */
    @Column(name = "break_duration")
    private Integer breakDuration;
    
    /**
     * User's timezone for accurate reminder scheduling
     * Examples: "Asia/Ho_Chi_Minh", "UTC", "America/New_York"
     */
    @Column(name = "timezone")
    private String timezone;
    
    /**
     * Timestamp when reminder was created
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * JPA callback to automatically set created_at timestamp
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

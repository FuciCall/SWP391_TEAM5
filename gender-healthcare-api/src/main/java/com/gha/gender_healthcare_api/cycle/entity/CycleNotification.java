package com.gha.gender_healthcare_api.cycle.entity;

import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity quản lý hệ thống thông báo tự động cho chu kỳ sinh sản
 * Hỗ trợ nhiều loại thông báo: rụng trứng, kinh nguyệt, nhắc nhở thuốc
 * Tích hợp với scheduling system để gửi thông báo đúng thời gian
 */
@Entity
@Table(name = "cycle_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CycleNotification {
      /**
     * ID tự động tăng - Primary key của bảng cycle_notifications
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Liên kết với User entity - Many notifications thuộc về One user
     * Mỗi user có thể có nhiều notification đang chờ và đã gửi
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Loại thông báo - enum định nghĩa các loại notification khác nhau
     * Giúp phân loại và xử lý notification theo từng mục đích cụ thể
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    /**
     * Tiêu đề của thông báo
     * Ví dụ: "Ovulation Tomorrow", "Time for your pill!"
     */
    @Column(name = "title", nullable = false)
    private String title;
    
    /**
     * Nội dung chi tiết của thông báo
     * Có thể chứa thông tin tư vấn sức khỏe và lời khuyên
     */
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    /**
     * Thời gian được lên lịch để gửi thông báo
     * Scheduler sẽ kiểm tra và gửi các notification đến thời gian này
     */
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    /**
     * Thời gian thực tế đã gửi thông báo
     * null nếu chưa gửi, có giá trị khi đã gửi thành công
     */
    @Column(name = "sent_time")
    private LocalDateTime sentTime;
    
    /**
     * Trạng thái đã đọc của notification
     * false: Chưa đọc, true: Đã đọc
     * Dùng để hiển thị số notification chưa đọc
     */
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    /**
     * Trạng thái đã gửi của notification
     * false: Chưa gửi, true: Đã gửi
     * Scheduler chỉ xử lý những notification chưa gửi
     */
    @Column(name = "is_sent")
    private Boolean isSent = false;
    
    /**
     * Thời gian tạo notification - tự động set khi tạo
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Enum định nghĩa các loại thông báo trong hệ thống
     * Mỗi loại có logic xử lý và template riêng
     */
    public enum NotificationType {
        OVULATION_REMINDER,           // Nhắc nhở rụng trứng (1 ngày trước)
        PERIOD_REMINDER,              // Nhắc nhở kinh nguyệt sắp đến (2 ngày trước)
        CONTRACEPTIVE_PILL_REMINDER,  // Nhắc nhở uống thuốc tránh thai (hàng ngày)
        FERTILITY_WINDOW,             // Thông báo cửa sổ sinh sản bắt đầu
        PERIOD_LATE_WARNING           // Cảnh báo kinh nguyệt trễ (nếu cần)
    }
    
    /**
     * Callback method tự động set created_at khi tạo notification mới
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

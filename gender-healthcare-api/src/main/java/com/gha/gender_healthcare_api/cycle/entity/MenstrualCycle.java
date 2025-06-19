package com.gha.gender_healthcare_api.cycle.entity;

import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho chu kỳ kinh nguyệt của người dùng
 * Lưu trữ thông tin về ngày bắt đầu, kết thúc, cường độ kinh nguyệt và các triệu chứng
 * Được sử dụng để tính toán dự đoán chu kỳ tiếp theo và phân tích sức khỏe sinh sản
 */
@Entity
@Table(name = "menstrual_cycles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenstrualCycle {    /**
     * Khóa chính tự động tăng của bảng menstrual_cycles
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Liên kết với bảng users - Many cycles thuộc về One user
     * Sử dụng LAZY loading để tối ưu hiệu suất
     * CASCADE DELETE: Xóa user sẽ xóa tất cả cycles của user đó
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Ngày bắt đầu chu kỳ kinh nguyệt (bắt buộc)
     * Được sử dụng để tính toán độ dài chu kỳ và dự đoán chu kỳ tiếp theo
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    /**
     * Ngày kết thúc chu kỳ kinh nguyệt (tùy chọn)
     * Có thể null nếu user chưa kết thúc chu kỳ hoặc quên update
     */
    @Column(name = "end_date")
    private LocalDate endDate;
    
    /**
     * Độ dài chu kỳ tính bằng ngày (từ ngày bắt đầu chu kỳ này đến chu kỳ tiếp theo)
     * Được tính tự động từ chu kỳ trước đó
     * Giá trị bình thường: 21-35 ngày
     */
    @Column(name = "cycle_length")
    private Integer cycleLength; // Độ dài chu kỳ trung bình
    
    /**
     * Độ dài kinh nguyệt tính bằng ngày (từ start_date đến end_date)
     * Được tính tự động khi có end_date
     * Giá trị bình thường: 3-7 ngày
     */
    @Column(name = "period_length")
    private Integer periodLength; // Thời gian kinh nguyệt
    
    /**
     * Cường độ kinh nguyệt - enum với 3 mức độ
     * LIGHT: Ít, NORMAL: Bình thường, HEAVY: Nhiều
     * Được sử dụng để phân tích sức khỏe và đưa ra lời khuyên
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "flow_intensity")
    private FlowIntensity flowIntensity;
    
    /**
     * Các triệu chứng kèm theo trong chu kỳ
     * Lưu dưới dạng chuỗi JSON: "cramps,bloating,mood_swings"
     * Có thể mở rộng thành bảng riêng nếu cần phân tích chi tiết
     */
    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms; // Chuỗi JSON lưu trữ triệu chứng
    
    /**
     * Ghi chú cá nhân của user về chu kỳ này
     * Cho phép user lưu thông tin bổ sung (stress, thuốc, thay đổi lối sống...)
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * Thời gian tạo record - được set tự động khi tạo mới
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Thời gian cập nhật cuối - được update tự động mỗi lần chỉnh sửa
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Enum định nghĩa các mức độ cường độ kinh nguyệt
     * Được sử dụng trong form input và phân tích dữ liệu
     */
    public enum FlowIntensity {
        LIGHT,    // Kinh nguyệt ít
        NORMAL,   // Kinh nguyệt bình thường  
        HEAVY     // Kinh nguyệt nhiều
    }
    
    /**
     * Callback method được JPA gọi trước khi persist entity mới
     * Tự động set created_at và updated_at
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Callback method được JPA gọi trước khi update entity
     * Tự động update updated_at timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

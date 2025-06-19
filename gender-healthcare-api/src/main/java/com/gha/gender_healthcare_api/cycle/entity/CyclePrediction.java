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
 * Entity chứa dự đoán chu kỳ kinh nguyệt và thông tin sinh sản
 * Được tính toán dựa trên lịch sử chu kỳ của user và các thuật toán dự đoán
 * Cung cấp thông tin về ngày rụng trứng, cửa sổ sinh sản và khả năng có thai
 */
@Entity
@Table(name = "cycle_predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CyclePrediction {    /**
     * Khóa chính tự động tăng của bảng cycle_predictions
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Liên kết nhiều-một với thực thể User - mỗi user có nhiều predictions theo thời gian
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Ngày dự đoán rụng trứng - thường là 14 ngày trước chu kỳ tiếp theo
     */
    @Column(name = "predicted_ovulation_date")
    private LocalDate predictedOvulationDate;
    
    /**
     * Ngày bắt đầu cửa sổ sinh sản - 5 ngày trước rụng trứng
     */
    @Column(name = "fertile_window_start")
    private LocalDate fertileWindowStart;
    
    /**
     * Ngày kết thúc cửa sổ sinh sản - 1 ngày sau rụng trứng
     */
    @Column(name = "fertile_window_end")
    private LocalDate fertileWindowEnd;
    
    /**
     * Ngày dự đoán chu kỳ kinh nguyệt tiếp theo
     */
    @Column(name = "next_period_date")
    private LocalDate nextPeriodDate;
    
    /**
     * Khả năng có thai tính theo phần trăm - dựa trên vị trí hiện tại trong cửa sổ sinh sản
     */
    @Column(name = "pregnancy_likelihood")
    private Double pregnancyLikelihood;
      /**
     * Thời gian thực hiện tính toán dự đoán này
     */
    @Column(name = "calculation_date")
    private LocalDateTime calculationDate;
    
    /**
     * Phương thức callback tự động gán thời gian tính toán khi tạo mới
     */
    @PrePersist
    protected void onCreate() {
        this.calculationDate = LocalDateTime.now();
    }
}

package com.gha.gender_healthcare_api.cycle.repository;

import com.gha.gender_healthcare_api.cycle.entity.CyclePrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface cho thực thể CyclePrediction
 * 
 * Cung cấp các phương thức truy cập dữ liệu dự đoán chu kỳ từ database.
 * Kế thừa JpaRepository để có sẵn các CRUD operations cơ bản.
 * 
 * Đặc điểm quan trọng:
 * - Mỗi user có thể có nhiều predictions theo thời gian
 * - Prediction mới nhất là prediction hiện hành
 * - Predictions cũ được giữ lại để tracking độ chính xác
 * - Prediction được tạo lại mỗi khi user khai báo chu kỳ mới
 * 
 * Các custom query methods:
 * - Lấy prediction mới nhất của user
 * - Có thể mở rộng: lấy predictions trong khoảng thời gian, so sánh độ chính xác
 */
@Repository
public interface CyclePredictionRepository extends JpaRepository<CyclePrediction, Long> {
    
    /**
     * Tìm dự đoán chu kỳ mới nhất của user
     * 
     * @param userId ID của user
     * @return Optional<CyclePrediction> dự đoán mới nhất có calculation_date lớn nhất, empty nếu chưa có
     * 
     * Sử dụng:
     * - Hiển thị thông tin dự đoán hiện tại trong UI (ngày rụng trứng, chu kỳ tiếp theo...)
     * - Tính toán khả năng có thai hiện tại
     * - Lên lịch notifications (ovulation reminders, period reminders)
     * - API endpoint GET /api/cycle/prediction/current
     * 
     * Quan trọng:
     * - Sắp xếp theo calculation_date DESC để lấy prediction mới nhất
     * - Prediction được cập nhật mỗi khi user khai báo chu kỳ mới
     * - Độ chính xác tăng dần theo số lượng chu kỳ đã khai báo
     */
    Optional<CyclePrediction> findTopByUserUserIdOrderByCalculationDateDesc(Long userId);
}

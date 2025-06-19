package com.gha.gender_healthcare_api.cycle.repository;

import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho thực thể MenstrualCycle
 * 
 * Cung cấp các phương thức truy cập dữ liệu chu kỳ kinh nguyệt từ database.
 * Kế thừa JpaRepository để có sẵn các CRUD operations cơ bản.
 * 
 * Các custom query methods:
 * - Tìm chu kỳ theo user và sắp xếp theo thời gian
 * - Tính toán thống kê chu kỳ (độ dài trung bình, số lượng)
 * - Lọc chu kỳ theo khoảng thời gian
 * 
 * Sử dụng Spring Data JPA conventions và @Query annotation
 * để tạo các truy vấn phức tạp.
 */
@Repository
public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Long> {
    
    /**
     * Tìm tất cả chu kỳ kinh nguyệt của một user, sắp xếp theo ngày bắt đầu giảm dần
     * 
     * @param userId ID của user
     * @return Danh sách MenstrualCycle, chu kỳ mới nhất trước
     * 
     * Sử dụng:
     * - Hiển thị lịch sử chu kỳ trong UI
     * - Tính toán dự đoán chu kỳ tiếp theo
     * - Phân tích patterns và trends
     */
    List<MenstrualCycle> findByUserUserIdOrderByStartDateDesc(Long userId);
    
    /**
     * Tìm chu kỳ của user từ một ngày cụ thể trở đi
     * 
     * @param userId ID của user
     * @param fromDate Ngày bắt đầu lọc (inclusive)
     * @return Danh sách MenstrualCycle từ fromDate trở đi, sắp xếp giảm dần
     * 
     * Sử dụng:
     * - Lấy chu kỳ trong 6 tháng gần nhất để phân tích
     * - Lọc chu kỳ theo năm hoặc quý
     * - Tạo báo cáo theo khoảng thời gian
     */
    @Query("SELECT mc FROM MenstrualCycle mc WHERE mc.user.userId = :userId AND mc.startDate >= :fromDate ORDER BY mc.startDate DESC")
    List<MenstrualCycle> findByUserIdAndStartDateAfter(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);
    
    /**
     * Tìm chu kỳ kinh nguyệt gần nhất của user
     * 
     * @param userId ID của user
     * @return Optional<MenstrualCycle> chu kỳ mới nhất, empty nếu user chưa có chu kỳ nào
     * 
     * Sử dụng:
     * - Tính toán dự đoán chu kỳ tiếp theo
     * - Hiển thị thông tin chu kỳ hiện tại
     * - Kiểm tra xem user đã khai báo chu kỳ chưa
     */
    Optional<MenstrualCycle> findTopByUserUserIdOrderByStartDateDesc(Long userId);
    
    /**
     * Tính độ dài chu kỳ trung bình của user
     * 
     * @param userId ID của user
     * @return Optional<Double> độ dài trung bình (ngày), empty nếu chưa có dữ liệu cycle_length
     * 
     * Chú ý:
     * - Chỉ tính từ những chu kỳ có cycle_length != null
     * - cycle_length được tính khi có chu kỳ tiếp theo
     * - Cần ít nhất 2 chu kỳ để có cycle_length
     * 
     * Sử dụng:
     * - Tạo dự đoán chu kỳ tiếp theo chính xác hơn
     * - Phân tích tính đều đặn của chu kỳ
     * - Cung cấp insight sức khỏe cho user
     */
    @Query("SELECT AVG(mc.cycleLength) FROM MenstrualCycle mc WHERE mc.user.userId = :userId AND mc.cycleLength IS NOT NULL")
    Optional<Double> findAverageCycleLengthByUserId(@Param("userId") Long userId);
    
    /**
     * Đếm tổng số chu kỳ đã ghi nhận của user
     * 
     * @param userId ID của user
     * @return Số lượng chu kỳ (long)
     * 
     * Sử dụng:
     * - Đánh giá độ tin cậy của phân tích (càng nhiều chu kỳ càng chính xác)
     * - Hiển thị thống kê trong dashboard
     * - Quyết định có đủ dữ liệu để tạo dự đoán không
     */
    @Query("SELECT COUNT(mc) FROM MenstrualCycle mc WHERE mc.user.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}

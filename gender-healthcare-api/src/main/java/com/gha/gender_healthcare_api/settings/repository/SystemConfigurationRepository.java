package com.gha.gender_healthcare_api.settings.repository;

import com.gha.gender_healthcare_api.settings.entity.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

/**
 * Repository interface cho thao tác với SystemConfiguration entity
 * 
 * Cung cấp các phương thức truy vấn dữ liệu cấu hình hệ thống:
 * - Tìm cấu hình theo key
 * - Tìm cấu hình theo nhóm
 * - Quản lý cấu hình có hiệu lực
 * - Thống kê và báo cáo
 * 
 * Extends JpaRepository để có sẵn các CRUD operations cơ bản
 */
@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
    
    /**
     * Tìm cấu hình theo config key
     * 
     * @param configKey key của cấu hình cần tìm
     * @return Optional chứa SystemConfiguration nếu tìm thấy
     */
    Optional<SystemConfiguration> findByConfigKey(String configKey);
    
    /**
     * Kiểm tra xem config key đã tồn tại chưa
     * 
     * @param configKey key cần kiểm tra
     * @return true nếu key đã tồn tại
     */
    boolean existsByConfigKey(String configKey);
    
    /**
     * Tìm tất cả cấu hình theo nhóm
     * 
     * @param configGroup nhóm cấu hình
     * @return danh sách SystemConfiguration thuộc nhóm
     */
    List<SystemConfiguration> findByConfigGroup(String configGroup);
    
    /**
     * Tìm cấu hình có thể chỉnh sửa được
     * 
     * @return danh sách SystemConfiguration có isEditable = true
     */
    List<SystemConfiguration> findByIsEditableTrue();
    
    /**
     * Tìm cấu hình nhạy cảm (cần mã hóa)
     * 
     * @return danh sách SystemConfiguration có isSensitive = true
     */
    List<SystemConfiguration> findByIsSensitiveTrue();
    
    /**
     * Tìm cấu hình cần restart sau khi thay đổi
     * 
     * @return danh sách SystemConfiguration có requiresRestart = true
     */
    List<SystemConfiguration> findByRequiresRestartTrue();
    
    /**
     * Tìm cấu hình theo kiểu dữ liệu
     * 
     * @param dataType kiểu dữ liệu
     * @return danh sách SystemConfiguration có dataType tương ứng
     */
    List<SystemConfiguration> findByDataType(SystemConfiguration.DataType dataType);
    
    /**
     * Tìm cấu hình đang có hiệu lực tại thời điểm hiện tại
     * 
     * @param now thời điểm hiện tại
     * @return danh sách SystemConfiguration đang có hiệu lực
     */
    @Query("SELECT sc FROM SystemConfiguration sc WHERE " +
           "(sc.effectiveFrom IS NULL OR sc.effectiveFrom <= :now) AND " +
           "(sc.effectiveTo IS NULL OR sc.effectiveTo > :now)")
    List<SystemConfiguration> findEffectiveConfigurations(@Param("now") LocalDateTime now);
    
    /**
     * Tìm cấu hình có hiệu lực theo key
     * 
     * @param configKey key của cấu hình
     * @param now thời điểm hiện tại
     * @return Optional chứa SystemConfiguration có hiệu lực
     */
    @Query("SELECT sc FROM SystemConfiguration sc WHERE " +
           "sc.configKey = :configKey AND " +
           "(sc.effectiveFrom IS NULL OR sc.effectiveFrom <= :now) AND " +
           "(sc.effectiveTo IS NULL OR sc.effectiveTo > :now)")
    Optional<SystemConfiguration> findEffectiveConfigurationByKey(@Param("configKey") String configKey, 
                                                                 @Param("now") LocalDateTime now);
    
    /**
     * Tìm cấu hình có hiệu lực theo nhóm
     * 
     * @param configGroup nhóm cấu hình
     * @param now thời điểm hiện tại
     * @return danh sách SystemConfiguration có hiệu lực trong nhóm
     */
    @Query("SELECT sc FROM SystemConfiguration sc WHERE " +
           "sc.configGroup = :configGroup AND " +
           "(sc.effectiveFrom IS NULL OR sc.effectiveFrom <= :now) AND " +
           "(sc.effectiveTo IS NULL OR sc.effectiveTo > :now)")
    List<SystemConfiguration> findEffectiveConfigurationsByGroup(@Param("configGroup") String configGroup, 
                                                                @Param("now") LocalDateTime now);
    
    /**
     * Tìm cấu hình được tạo bởi admin cụ thể
     * 
     * @param createdBy ID của admin tạo cấu hình
     * @return danh sách SystemConfiguration được tạo bởi admin
     */
    List<SystemConfiguration> findByCreatedBy(Long createdBy);
    
    /**
     * Tìm cấu hình được cập nhật gần đây
     * 
     * @param fromDate thời điểm bắt đầu
     * @return danh sách SystemConfiguration được cập nhật sau fromDate
     */
    @Query("SELECT sc FROM SystemConfiguration sc WHERE sc.updatedAt >= :fromDate ORDER BY sc.updatedAt DESC")
    List<SystemConfiguration> findRecentlyUpdated(@Param("fromDate") LocalDateTime fromDate);
    
    /**
     * Đếm số lượng cấu hình theo nhóm
     * 
     * @param configGroup nhóm cấu hình
     * @return số lượng cấu hình trong nhóm
     */
    long countByConfigGroup(String configGroup);
    
    /**
     * Đếm số lượng cấu hình nhạy cảm
     * 
     * @return số lượng cấu hình có isSensitive = true
     */
    @Query("SELECT COUNT(sc) FROM SystemConfiguration sc WHERE sc.isSensitive = true")
    long countSensitiveConfigurations();
    
    /**
     * Tìm tất cả nhóm cấu hình distinct
     * 
     * @return danh sách tất cả config groups
     */
    @Query("SELECT DISTINCT sc.configGroup FROM SystemConfiguration sc WHERE sc.configGroup IS NOT NULL ORDER BY sc.configGroup")
    List<String> findAllConfigGroups();
    
    /**
     * Tìm cấu hình theo pattern của key
     * 
     * @param keyPattern pattern để search (sử dụng LIKE)
     * @return danh sách SystemConfiguration có key match pattern
     */
    @Query("SELECT sc FROM SystemConfiguration sc WHERE sc.configKey LIKE :keyPattern")
    List<SystemConfiguration> findByConfigKeyPattern(@Param("keyPattern") String keyPattern);
    
    /**
     * Tìm cấu hình sẽ hết hiệu lực trong khoảng thời gian
     * 
     * @param fromDate thời điểm bắt đầu
     * @param toDate thời điểm kết thúc
     * @return danh sách SystemConfiguration sẽ hết hiệu lực
     */
    @Query("SELECT sc FROM SystemConfiguration sc WHERE " +
           "sc.effectiveTo IS NOT NULL AND " +
           "sc.effectiveTo BETWEEN :fromDate AND :toDate " +
           "ORDER BY sc.effectiveTo ASC")
    List<SystemConfiguration> findConfigurationsExpiringBetween(@Param("fromDate") LocalDateTime fromDate, 
                                                               @Param("toDate") LocalDateTime toDate);
    
    /**
     * Xóa cấu hình theo config key
     * 
     * @param configKey key của cấu hình cần xóa
     * @return số lượng records đã xóa
     */
    int deleteByConfigKey(String configKey);
}

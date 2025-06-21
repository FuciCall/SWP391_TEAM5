package com.gha.gender_healthcare_api.settings.repository;

import com.gha.gender_healthcare_api.settings.entity.ConfigurationAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface cho thao tác với ConfigurationAuditLog entity
 * 
 * Cung cấp các phương thức truy vấn audit logs:
 * - Theo dõi lịch sử thay đổi
 * - Tìm kiếm theo nhiều tiêu chí
 * - Phân tích hành vi user
 * - Báo cáo audit
 * 
 * Extends JpaRepository để có sẵn các CRUD operations cơ bản
 */
@Repository
public interface ConfigurationAuditLogRepository extends JpaRepository<ConfigurationAuditLog, Long> {
    
    /**
     * Tìm audit logs theo loại cấu hình
     * 
     * @param configurationType loại cấu hình
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog
     */
    Page<ConfigurationAuditLog> findByConfigurationType(ConfigurationAuditLog.ConfigurationType configurationType, 
                                                       Pageable pageable);
    
    /**
     * Tìm audit logs theo ID record cấu hình
     * 
     * @param configRecordId ID của record cấu hình
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog
     */
    Page<ConfigurationAuditLog> findByConfigRecordId(Long configRecordId, Pageable pageable);
    
    /**
     * Tìm audit logs theo user thực hiện thay đổi
     * 
     * @param changedBy ID của user thực hiện thay đổi
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog
     */
    Page<ConfigurationAuditLog> findByChangedBy(Long changedBy, Pageable pageable);
    
    /**
     * Tìm audit logs theo loại hành động
     * 
     * @param actionType loại hành động
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog
     */
    Page<ConfigurationAuditLog> findByActionType(ConfigurationAuditLog.ActionType actionType, Pageable pageable);
    
    /**
     * Tìm audit logs trong khoảng thời gian
     * 
     * @param fromDate thời điểm bắt đầu
     * @param toDate thời điểm kết thúc
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog
     */
    Page<ConfigurationAuditLog> findByChangedAtBetween(LocalDateTime fromDate, LocalDateTime toDate, 
                                                      Pageable pageable);
    
    /**
     * Tìm audit logs thành công/thất bại
     * 
     * @param isSuccessful trạng thái thành công
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog
     */
    Page<ConfigurationAuditLog> findByIsSuccessful(Boolean isSuccessful, Pageable pageable);
    
    /**
     * Tìm audit logs theo field name
     * 
     * @param fieldName tên field được thay đổi
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog
     */
    Page<ConfigurationAuditLog> findByFieldName(String fieldName, Pageable pageable);
    
    /**
     * Tìm audit logs theo IP address
     * 
     * @param ipAddress địa chỉ IP
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog
     */
    Page<ConfigurationAuditLog> findByIpAddress(String ipAddress, Pageable pageable);
    
    /**
     * Tìm audit logs theo session ID
     * 
     * @param sessionId ID của session
     * @return danh sách ConfigurationAuditLog trong session
     */
    List<ConfigurationAuditLog> findBySessionId(String sessionId);
    
    /**
     * Tìm lịch sử thay đổi của một record cấu hình cụ thể
     * 
     * @param configurationType loại cấu hình
     * @param configRecordId ID của record
     * @param pageable thông tin phân trang
     * @return Page chứa ConfigurationAuditLog theo thứ tự thời gian
     */
    @Query("SELECT cal FROM ConfigurationAuditLog cal WHERE " +
           "cal.configurationType = :configurationType AND " +
           "cal.configRecordId = :configRecordId " +
           "ORDER BY cal.changedAt DESC")
    Page<ConfigurationAuditLog> findConfigurationHistory(@Param("configurationType") ConfigurationAuditLog.ConfigurationType configurationType,
                                                         @Param("configRecordId") Long configRecordId,
                                                         Pageable pageable);
    
    /**
     * Tìm các thay đổi gần đây của user
     * 
     * @param changedBy ID của user
     * @param fromDate thời điểm bắt đầu
     * @return danh sách ConfigurationAuditLog gần đây
     */
    @Query("SELECT cal FROM ConfigurationAuditLog cal WHERE " +
           "cal.changedBy = :changedBy AND " +
           "cal.changedAt >= :fromDate " +
           "ORDER BY cal.changedAt DESC")
    List<ConfigurationAuditLog> findRecentChangesByUser(@Param("changedBy") Long changedBy,
                                                       @Param("fromDate") LocalDateTime fromDate);
    
    /**
     * Đếm số lượng thay đổi theo user trong khoảng thời gian
     * 
     * @param changedBy ID của user
     * @param fromDate thời điểm bắt đầu
     * @param toDate thời điểm kết thúc
     * @return số lượng thay đổi
     */
    @Query("SELECT COUNT(cal) FROM ConfigurationAuditLog cal WHERE " +
           "cal.changedBy = :changedBy AND " +
           "cal.changedAt BETWEEN :fromDate AND :toDate")
    long countChangesByUserBetween(@Param("changedBy") Long changedBy,
                                  @Param("fromDate") LocalDateTime fromDate,
                                  @Param("toDate") LocalDateTime toDate);
    
    /**
     * Thống kê số lượng thay đổi theo loại hành động
     * 
     * @param fromDate thời điểm bắt đầu
     * @param toDate thời điểm kết thúc
     * @return danh sách thống kê [ActionType, Count]
     */
    @Query("SELECT cal.actionType, COUNT(cal) FROM ConfigurationAuditLog cal WHERE " +
           "cal.changedAt BETWEEN :fromDate AND :toDate " +
           "GROUP BY cal.actionType " +
           "ORDER BY COUNT(cal) DESC")
    List<Object[]> getActionTypeStatistics(@Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate);
    
    /**
     * Thống kê số lượng thay đổi theo user
     * 
     * @param fromDate thời điểm bắt đầu
     * @param toDate thời điểm kết thúc
     * @param limit số lượng user top
     * @return danh sách thống kê [UserId, Count]
     */
    @Query("SELECT cal.changedBy, COUNT(cal) FROM ConfigurationAuditLog cal WHERE " +
           "cal.changedAt BETWEEN :fromDate AND :toDate " +
           "GROUP BY cal.changedBy " +
           "ORDER BY COUNT(cal) DESC")
    List<Object[]> getTopActiveUsers(@Param("fromDate") LocalDateTime fromDate,
                                    @Param("toDate") LocalDateTime toDate,
                                    Pageable pageable);
    
    /**
     * Tìm các thay đổi thất bại gần đây
     * 
     * @param fromDate thời điểm bắt đầu
     * @return danh sách ConfigurationAuditLog thất bại
     */
    @Query("SELECT cal FROM ConfigurationAuditLog cal WHERE " +
           "cal.isSuccessful = false AND " +
           "cal.changedAt >= :fromDate " +
           "ORDER BY cal.changedAt DESC")
    List<ConfigurationAuditLog> findRecentFailures(@Param("fromDate") LocalDateTime fromDate);
    
    /**
     * Tìm các field được thay đổi nhiều nhất
     * 
     * @param fromDate thời điểm bắt đầu
     * @param toDate thời điểm kết thúc
     * @return danh sách thống kê [FieldName, Count]
     */
    @Query("SELECT cal.fieldName, COUNT(cal) FROM ConfigurationAuditLog cal WHERE " +
           "cal.changedAt BETWEEN :fromDate AND :toDate " +
           "GROUP BY cal.fieldName " +
           "ORDER BY COUNT(cal) DESC")
    List<Object[]> getMostChangedFields(@Param("fromDate") LocalDateTime fromDate,
                                       @Param("toDate") LocalDateTime toDate);
    
    /**
     * Xóa audit logs cũ trước một thời điểm
     * 
     * @param beforeDate thời điểm cắt
     * @return số lượng records đã xóa
     */
    @Query("DELETE FROM ConfigurationAuditLog cal WHERE cal.changedAt < :beforeDate")
    int deleteOldLogs(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * Tìm các IP có hoạt động đáng ngờ (nhiều thay đổi trong thời gian ngắn)
     * 
     * @param fromDate thời điểm bắt đầu
     * @param toDate thời điểm kết thúc
     * @param threshold số lượng thay đổi tối thiểu để coi là đáng ngờ
     * @return danh sách thống kê [IPAddress, Count]
     */
    @Query("SELECT cal.ipAddress, COUNT(cal) FROM ConfigurationAuditLog cal WHERE " +
           "cal.changedAt BETWEEN :fromDate AND :toDate AND " +
           "cal.ipAddress IS NOT NULL " +
           "GROUP BY cal.ipAddress " +
           "HAVING COUNT(cal) >= :threshold " +
           "ORDER BY COUNT(cal) DESC")
    List<Object[]> findSuspiciousIpActivity(@Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate,
                                          @Param("threshold") long threshold);
}

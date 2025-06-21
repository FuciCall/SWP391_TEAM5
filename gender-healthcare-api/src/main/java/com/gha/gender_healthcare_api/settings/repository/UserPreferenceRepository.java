package com.gha.gender_healthcare_api.settings.repository;

import com.gha.gender_healthcare_api.settings.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository interface cho thao tác với UserPreference entity
 * 
 * Cung cấp các phương thức truy vấn dữ liệu preferences của user:
 * - Tìm preferences theo user ID
 * - Tìm users theo các tùy chọn cụ thể
 * - Thống kê usage của các tùy chọn
 * 
 * Extends JpaRepository để có sẵn các CRUD operations cơ bản
 */
@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    
    /**
     * Tìm user preference theo user ID
     * 
     * @param userId ID của user cần tìm preferences
     * @return Optional chứa UserPreference nếu tìm thấy
     */
    Optional<UserPreference> findByUserId(Long userId);
    
    /**
     * Kiểm tra xem user đã có preferences chưa
     * 
     * @param userId ID của user cần kiểm tra
     * @return true nếu user đã có preferences
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Xóa preferences của user
     * 
     * @param userId ID của user cần xóa preferences
     * @return số lượng records đã xóa
     */
    int deleteByUserId(Long userId);
    
    /**
     * Tìm tất cả users có bật email notifications
     * 
     * @return danh sách UserPreference có email notifications enabled
     */
    List<UserPreference> findByEmailNotificationsEnabledTrue();
    
    /**
     * Tìm tất cả users có bật SMS notifications
     * 
     * @return danh sách UserPreference có SMS notifications enabled
     */
    List<UserPreference> findBySmsNotificationsEnabledTrue();
    
    /**
     * Tìm tất cả users có bật push notifications
     * 
     * @return danh sách UserPreference có push notifications enabled
     */
    List<UserPreference> findByPushNotificationsEnabledTrue();
    
    /**
     * Tìm users theo theme preference
     * 
     * @param theme theme cần tìm
     * @return danh sách UserPreference có theme preference tương ứng
     */
    List<UserPreference> findByThemePreference(UserPreference.ThemePreference theme);
    
    /**
     * Tìm users theo language preference
     * 
     * @param language ngôn ngữ cần tìm
     * @return danh sách UserPreference có language preference tương ứng
     */
    List<UserPreference> findByLanguagePreference(UserPreference.LanguagePreference language);
    
    /**
     * Tìm users có bật two-factor authentication
     * 
     * @return danh sách UserPreference có 2FA enabled
     */
    List<UserPreference> findByTwoFactorEnabledTrue();
    
    /**
     * Tìm users theo timezone
     * 
     * @param timezone múi giờ cần tìm
     * @return danh sách UserPreference có timezone tương ứng
     */
    List<UserPreference> findByTimezone(String timezone);
    
    /**
     * Đếm số lượng users theo theme preference
     * 
     * @param theme theme cần đếm
     * @return số lượng users sử dụng theme này
     */
    @Query("SELECT COUNT(up) FROM UserPreference up WHERE up.themePreference = :theme")
    long countByThemePreference(@Param("theme") UserPreference.ThemePreference theme);
    
    /**
     * Đếm số lượng users có bật email notifications
     * 
     * @return số lượng users có email notifications enabled
     */
    @Query("SELECT COUNT(up) FROM UserPreference up WHERE up.emailNotificationsEnabled = true")
    long countUsersWithEmailNotifications();
    
    /**
     * Đếm số lượng users có bật 2FA
     * 
     * @return số lượng users có 2FA enabled
     */
    @Query("SELECT COUNT(up) FROM UserPreference up WHERE up.twoFactorEnabled = true")
    long countUsersWithTwoFactor();
    
    /**
     * Tìm users có session timeout trong khoảng thời gian
     * 
     * @param minTimeout thời gian timeout tối thiểu (phút)
     * @param maxTimeout thời gian timeout tối đa (phút)
     * @return danh sách UserPreference có session timeout trong khoảng
     */
    @Query("SELECT up FROM UserPreference up WHERE up.sessionTimeoutMinutes BETWEEN :minTimeout AND :maxTimeout")
    List<UserPreference> findBySessionTimeoutBetween(@Param("minTimeout") Integer minTimeout, 
                                                     @Param("maxTimeout") Integer maxTimeout);
    
    /**
     * Tìm users cho phép chia sẻ dữ liệu nghiên cứu
     * 
     * @return danh sách UserPreference cho phép research data sharing
     */
    List<UserPreference> findByResearchDataSharingTrue();
    
    /**
     * Tìm users có public profile visible
     * 
     * @return danh sách UserPreference có public profile visible
     */
    List<UserPreference> findByPublicProfileVisibleTrue();
    
    /**
     * Tìm users theo measurement unit
     * 
     * @param unit đơn vị đo lường
     * @return danh sách UserPreference sử dụng đơn vị này
     */
    List<UserPreference> findByMeasurementUnit(UserPreference.MeasurementUnit unit);
    
    /**
     * Thống kê preferences được sử dụng nhiều nhất
     * 
     * @return danh sách thống kê usage
     */
    @Query("SELECT " +
           "up.themePreference as theme, " +
           "up.languagePreference as language, " +
           "up.measurementUnit as unit, " +
           "COUNT(up) as count " +
           "FROM UserPreference up " +
           "GROUP BY up.themePreference, up.languagePreference, up.measurementUnit " +
           "ORDER BY COUNT(up) DESC")
    List<Object[]> getPreferenceUsageStatistics();
}

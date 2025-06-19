package com.gha.gender_healthcare_api.cycle.controller;

import com.gha.gender_healthcare_api.cycle.entity.CycleNotification;
import com.gha.gender_healthcare_api.cycle.service.CycleNotificationService;
import com.gha.gender_healthcare_api.dto.response.ApiResponse;
import com.gha.gender_healthcare_api.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cycle-notifications")
@PreAuthorize("hasRole('CUSTOMER')")
@CrossOrigin(origins = "*")
public class CycleNotificationController {
    
    @Autowired
    private CycleNotificationService notificationService;    /**
     * Lấy tất cả thông báo liên quan đến chu kỳ cho người dùng đã xác thực.
     * 
     * Endpoint này cung cấp cho người dùng quyền truy cập vào lịch sử thông báo:
     * - Lấy thông báo về dự đoán chu kỳ, lời nhắc và cảnh báo sức khỏe
     * - Trả về cả thông báo đã đọc và chưa đọc để có lịch sử đầy đủ
     * - Đảm bảo người dùng chỉ truy cập dữ liệu thông báo của riêng họ
     * - Hỗ trợ tính năng hiển thị và quản lý thông báo của frontend
     * 
     * Thông báo có thể bao gồm lời nhắc về:
     * - Chu kỳ kinh nguyệt sắp tới
     * - Dự đoán cửa sổ sinh sản
     * - Lời nhắc thuốc tránh thai
     * - Khuyến nghị kiểm tra sức khỏe
     * 
     * @param auth Đối tượng xác thực để xác định người dùng đang yêu cầu
     * @return ResponseEntity chứa danh sách thông báo chu kỳ của người dùng
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getNotifications(Authentication auth) {
        try {
            // Trích xuất ID người dùng để truy cập dữ liệu an toàn
            Long userId = getCurrentUserId(auth);
            
            // Lấy tất cả thông báo liên kết với người dùng này
            List<CycleNotification> notifications = notificationService.getUserNotifications(userId);
            
            // Trả về danh sách thông báo hoàn chỉnh
            return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
        } catch (Exception e) {
            // Xử lý bất kỳ lỗi truy cập dữ liệu hoặc xử lý nào
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get notifications: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long notificationId, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            notificationService.markAsRead(notificationId, userId);
            
            return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to mark notification as read: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getId();
    }
}

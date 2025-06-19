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
    private CycleNotificationService notificationService;
      /**
     * Retrieves all cycle-related notifications for the authenticated user.
     * 
     * This endpoint provides users with access to their notification history:
     * - Fetches notifications for cycle predictions, reminders, and health alerts
     * - Returns both read and unread notifications for complete history
     * - Ensures users only access their own notification data
     * - Supports frontend notification display and management features
     * 
     * Notifications may include reminders for:
     * - Upcoming menstrual periods
     * - Fertile window predictions
     * - Contraceptive pill reminders
     * - Health check recommendations
     * 
     * @param auth Authentication object to identify the requesting user
     * @return ResponseEntity containing list of user's cycle notifications
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getNotifications(Authentication auth) {
        try {
            // Extract user ID for secure data access
            Long userId = getCurrentUserId(auth);
            
            // Retrieve all notifications associated with this user
            List<CycleNotification> notifications = notificationService.getUserNotifications(userId);
            
            // Return the complete notification list
            return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
        } catch (Exception e) {
            // Handle any data access or processing errors
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

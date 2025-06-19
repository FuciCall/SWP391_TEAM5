package com.gha.gender_healthcare_api.cycle.controller;

import com.gha.gender_healthcare_api.cycle.dto.ContraceptiveReminderRequest;
import com.gha.gender_healthcare_api.cycle.entity.ContraceptiveReminder;
import com.gha.gender_healthcare_api.cycle.service.ContraceptiveReminderService;
import com.gha.gender_healthcare_api.dto.response.ApiResponse;
import com.gha.gender_healthcare_api.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contraceptive")
@PreAuthorize("hasRole('CUSTOMER')")
@CrossOrigin(origins = "*")
public class ContraceptiveController {
    
    @Autowired
    private ContraceptiveReminderService reminderService;
      /**
     * Sets up a new contraceptive pill reminder for the authenticated user.
     * 
     * This endpoint allows users to create personalized pill reminder schedules with:
     * - Custom notification times (e.g., daily at 8:00 AM)
     * - Pill type and dosage information
     * - Duration of the contraceptive cycle
     * - Email notification preferences
     * 
     * The system validates the request data and creates a new reminder entry
     * with automatic scheduling for future notifications.
     * 
     * @param request Validated request object containing pill reminder details
     * @param auth Authentication object containing user credentials and permissions
     * @return ResponseEntity with success/error message and created reminder data
     */
    @PostMapping("/pill-reminder/setup")
    public ResponseEntity<ApiResponse> setupPillReminder(@Valid @RequestBody ContraceptiveReminderRequest request, Authentication auth) {
        try {
            // Extract user ID from authentication token for security
            Long userId = getCurrentUserId(auth);
            
            // Delegate to service layer to handle business logic and data persistence
            ContraceptiveReminder reminder = reminderService.setupPillReminder(userId, request);
            
            // Return success response with the created reminder object
            return ResponseEntity.ok(ApiResponse.success("Pill reminder set up successfully", reminder));
        } catch (Exception e) {
            // Handle any validation or business logic errors gracefully
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to setup reminder: " + e.getMessage()));
        }
    }
      /**
     * Retrieves the current active contraceptive reminder for the authenticated user.
     * 
     * This endpoint allows users to check their existing pill reminder settings:
     * - Returns the currently active reminder if one exists
     * - Provides null response if no active reminder is found
     * - Ensures users can only access their own reminder data
     * 
     * The system searches for active reminders associated with the user's ID
     * and returns the most recent or current reminder configuration.
     * 
     * @param auth Authentication object to identify the requesting user
     * @return ResponseEntity with the active reminder data or null if none exists
     */
    @GetMapping("/pill-reminder")
    public ResponseEntity<ApiResponse> getActiveReminder(Authentication auth) {
        try {
            // Extract user ID to ensure data privacy and security
            Long userId = getCurrentUserId(auth);
            
            // Query service layer for the user's active reminder
            ContraceptiveReminder reminder = reminderService.getUserActiveReminder(userId);
            
            // Handle case where no active reminder exists
            if (reminder == null) {
                return ResponseEntity.ok(ApiResponse.success("No active reminder found", null));
            }
            
            // Return the found active reminder
            return ResponseEntity.ok(ApiResponse.success("Active reminder retrieved", reminder));
        } catch (Exception e) {
            // Handle any data access or processing errors
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get reminder: " + e.getMessage()));
        }
    }
      /**
     * Deactivates a specific contraceptive reminder for the authenticated user.
     * 
     * This endpoint allows users to stop or cancel their pill reminders:
     * - Validates that the reminder belongs to the requesting user
     * - Marks the reminder as inactive to stop future notifications
     * - Maintains reminder history for potential future reference
     * - Ensures secure access to only user-owned reminders
     * 
     * The system performs authorization checks to prevent users from
     * deactivating reminders that don't belong to them.
     * 
     * @param reminderId The unique ID of the reminder to deactivate
     * @param auth Authentication object to verify user ownership
     * @return ResponseEntity with success confirmation or error message
     */
    @DeleteMapping("/pill-reminder/{reminderId}")
    public ResponseEntity<ApiResponse> deactivateReminder(@PathVariable Long reminderId, Authentication auth) {
        try {
            // Extract user ID for ownership validation
            Long userId = getCurrentUserId(auth);
            
            // Delegate to service layer for secure deactivation logic
            reminderService.deactivateReminder(userId, reminderId);
            
            // Confirm successful deactivation
            return ResponseEntity.ok(ApiResponse.success("Reminder deactivated successfully", null));
        } catch (Exception e) {
            // Handle authorization errors or data access issues
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to deactivate reminder: " + e.getMessage()));
        }
    }
      /**
     * Extracts the user ID from the Spring Security authentication context.
     * 
     * This utility method provides secure access to the authenticated user's ID:
     * - Casts the authentication principal to UserPrincipal type
     * - Retrieves the user ID for database queries and authorization
     * - Ensures consistent user identification across all endpoints
     * - Maintains security by using Spring Security's authentication context
     * 
     * This method is used throughout the controller to link operations
     * to the specific authenticated user making the request.
     * 
     * @param auth Spring Security Authentication object from the request context
     * @return Long user ID extracted from the authentication principal
     */
    private Long getCurrentUserId(Authentication auth) {
        // Cast authentication principal to custom UserPrincipal type
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        
        // Extract and return the user's unique identifier
        return userPrincipal.getId();
    }
}

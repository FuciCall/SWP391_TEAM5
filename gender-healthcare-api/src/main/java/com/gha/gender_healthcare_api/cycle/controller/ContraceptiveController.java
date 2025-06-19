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
    
    @PostMapping("/pill-reminder/setup")
    public ResponseEntity<ApiResponse> setupPillReminder(@Valid @RequestBody ContraceptiveReminderRequest request, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            ContraceptiveReminder reminder = reminderService.setupPillReminder(userId, request);
            
            return ResponseEntity.ok(ApiResponse.success("Pill reminder set up successfully", reminder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to setup reminder: " + e.getMessage()));
        }
    }
    
    @GetMapping("/pill-reminder")
    public ResponseEntity<ApiResponse> getActiveReminder(Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            ContraceptiveReminder reminder = reminderService.getUserActiveReminder(userId);
            
            if (reminder == null) {
                return ResponseEntity.ok(ApiResponse.success("No active reminder found", null));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Active reminder retrieved", reminder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get reminder: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/pill-reminder/{reminderId}")
    public ResponseEntity<ApiResponse> deactivateReminder(@PathVariable Long reminderId, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            reminderService.deactivateReminder(userId, reminderId);
            
            return ResponseEntity.ok(ApiResponse.success("Reminder deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to deactivate reminder: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getId();
    }
}

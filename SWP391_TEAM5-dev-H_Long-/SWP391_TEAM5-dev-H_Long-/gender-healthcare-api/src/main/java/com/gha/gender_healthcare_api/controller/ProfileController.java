package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.response.UserProfileDTO;
import com.gha.gender_healthcare_api.dto.response.HistoryDTO;
import com.gha.gender_healthcare_api.security.CustomUserDetails;
import com.gha.gender_healthcare_api.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile() {
        Long userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        UserProfileDTO profile = profileService.getUserProfile(userId);
        if (profile == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfileDTO> updateProfile(@RequestBody UserProfileDTO profileDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        UserProfileDTO updated = profileService.updateUserProfile(userId, profileDTO);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/history")
    public ResponseEntity<List<HistoryDTO>> getHistory() {
        Long userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        List<HistoryDTO> history = profileService.getUserHistory(userId);
        return ResponseEntity.ok(history);
    }
}
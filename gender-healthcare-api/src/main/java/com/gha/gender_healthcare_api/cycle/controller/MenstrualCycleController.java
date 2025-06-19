package com.gha.gender_healthcare_api.cycle.controller;

import com.gha.gender_healthcare_api.cycle.dto.CycleAnalytics;
import com.gha.gender_healthcare_api.cycle.dto.MenstrualCycleRequest;
import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import com.gha.gender_healthcare_api.cycle.entity.CyclePrediction;
import com.gha.gender_healthcare_api.cycle.service.MenstrualCycleService;
import com.gha.gender_healthcare_api.cycle.service.CyclePredictionService;
import com.gha.gender_healthcare_api.dto.response.ApiResponse;
import com.gha.gender_healthcare_api.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menstrual-cycle")
@PreAuthorize("hasRole('CUSTOMER')")
@CrossOrigin(origins = "*")
public class MenstrualCycleController {
    
    @Autowired
    private MenstrualCycleService menstrualCycleService;
    
    @Autowired
    private CyclePredictionService cyclePredictionService;
    
    @PostMapping("/declare")
    public ResponseEntity<ApiResponse> declareCycle(@Valid @RequestBody MenstrualCycleRequest request, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            MenstrualCycle cycle = menstrualCycleService.declareMenstrualCycle(userId, request);
            
            return ResponseEntity.ok(ApiResponse.success("Menstrual cycle recorded successfully", cycle));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to record cycle: " + e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getCycleHistory(@RequestParam(defaultValue = "12") int months, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            List<MenstrualCycle> cycles = menstrualCycleService.getUserCycles(userId, months);
            
            return ResponseEntity.ok(ApiResponse.success("Cycle history retrieved", cycles));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get cycle history: " + e.getMessage()));
        }
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse> getCycleAnalytics(Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            CycleAnalytics analytics = menstrualCycleService.getCycleAnalytics(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Cycle analytics retrieved", analytics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get analytics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/predictions")
    public ResponseEntity<ApiResponse> getCurrentPredictions(Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            CyclePrediction prediction = cyclePredictionService.getCurrentPrediction(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Predictions retrieved", prediction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get predictions: " + e.getMessage()));
        }
    }
    
    @PostMapping("/predictions/refresh")
    public ResponseEntity<ApiResponse> refreshPredictions(Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            CyclePrediction prediction = cyclePredictionService.generatePredictions(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Predictions updated", prediction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update predictions: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getId();
    }
}

package com.gha.gender_healthcare_api.settings.controller;

import com.gha.gender_healthcare_api.settings.dto.UserPreferenceResponse;
import com.gha.gender_healthcare_api.settings.dto.UserPreferenceUpdateRequest;
import com.gha.gender_healthcare_api.settings.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller quản lý tùy chọn cá nhân của người dùng
 * Cung cấp API để người dùng cập nhật và lấy các thiết lập cá nhân
 */
@RestController
@RequestMapping("/api/v1/user-preferences")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Preferences", description = "API quản lý tùy chọn cá nhân người dùng")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    @Operation(
        summary = "Lấy tùy chọn cá nhân của người dùng",
        description = "Lấy tất cả tùy chọn cá nhân của người dùng hiện tại"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy tùy chọn thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy tùy chọn của người dùng"),
        @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'CONSULTANT', 'MANAGER', 'STAFF')")
    public ResponseEntity<UserPreferenceResponse> getCurrentUserPreferences(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId) {
        
        log.info("Getting preferences for user ID: {}", userId);
        
        try {
            UserPreferenceResponse preferences = userPreferenceService.getUserPreferences(userId);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            log.error("Error getting user preferences for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Cập nhật tùy chọn cá nhân",
        description = "Cập nhật tùy chọn cá nhân của người dùng hiện tại"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'CONSULTANT', 'MANAGER', 'STAFF')")
    public ResponseEntity<UserPreferenceResponse> updateCurrentUserPreferences(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Valid @RequestBody UserPreferenceUpdateRequest request) {
        
        log.info("Updating preferences for user ID: {}", userId);
        
        try {
            UserPreferenceResponse updatedPreferences = userPreferenceService.updateUserPreferences(userId, request);
            return ResponseEntity.ok(updatedPreferences);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for updating user preferences: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating user preferences for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Khôi phục tùy chọn mặc định",
        description = "Khôi phục tất cả tùy chọn về giá trị mặc định"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Khôi phục thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @PostMapping("/me/reset")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'CONSULTANT', 'MANAGER', 'STAFF')")
    public ResponseEntity<UserPreferenceResponse> resetCurrentUserPreferences(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId) {
        
        log.info("Resetting preferences to default for user ID: {}", userId);
        
        try {
            UserPreferenceResponse defaultPreferences = userPreferenceService.resetToDefaultPreferences(userId);
            return ResponseEntity.ok(defaultPreferences);
        } catch (Exception e) {
            log.error("Error resetting user preferences for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Lấy tùy chọn theo key cụ thể",
        description = "Lấy giá trị của một tùy chọn cụ thể theo preference key"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy giá trị thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy tùy chọn"),
        @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @GetMapping("/me/{preferenceKey}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'CONSULTANT', 'MANAGER', 'STAFF')")
    public ResponseEntity<Map<String, String>> getCurrentUserPreferenceByKey(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "Key của tùy chọn cần lấy") @PathVariable String preferenceKey) {
        
        log.info("Getting preference '{}' for user ID: {}", preferenceKey, userId);
        
        try {
            String preferenceValue = userPreferenceService.getUserPreferenceValue(userId, preferenceKey);
            
            if (preferenceValue != null) {
                return ResponseEntity.ok(Map.of("key", preferenceKey, "value", preferenceValue));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error getting preference '{}' for user ID: {}", preferenceKey, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Cập nhật tùy chọn theo key cụ thể",
        description = "Cập nhật giá trị của một tùy chọn cụ thể theo preference key"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @PutMapping("/me/{preferenceKey}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'CONSULTANT', 'MANAGER', 'STAFF')")
    public ResponseEntity<Map<String, String>> updateCurrentUserPreferenceByKey(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "Key của tùy chọn cần cập nhật") @PathVariable String preferenceKey,
            @RequestBody Map<String, String> requestBody) {
        
        String preferenceValue = requestBody.get("value");
        log.info("Updating preference '{}' = '{}' for user ID: {}", preferenceKey, preferenceValue, userId);
        
        try {
            if (preferenceValue == null) {
                return ResponseEntity.badRequest().build();
            }
            
            userPreferenceService.updateSingleUserPreference(userId, preferenceKey, preferenceValue);
            return ResponseEntity.ok(Map.of("key", preferenceKey, "value", preferenceValue, "message", "Updated successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for updating preference '{}': {}", preferenceKey, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating preference '{}' for user ID: {}", preferenceKey, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    @Operation(
        summary = "[ADMIN] Lấy tùy chọn của người dùng cụ thể",
        description = "Chỉ admin mới có thể xem tùy chọn của người dùng khác"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy tùy chọn thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserPreferenceResponse> getUserPreferences(
            @Parameter(description = "ID của người dùng cần xem tùy chọn") @PathVariable Long userId) {
        
        log.info("Admin getting preferences for user ID: {}", userId);
        
        try {
            UserPreferenceResponse preferences = userPreferenceService.getUserPreferences(userId);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            log.error("Error getting user preferences for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "[ADMIN] Khôi phục tùy chọn mặc định cho người dùng",
        description = "Admin có thể khôi phục tùy chọn mặc định cho bất kỳ người dùng nào"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Khôi phục thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PostMapping("/users/{userId}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserPreferenceResponse> resetUserPreferences(
            @Parameter(description = "ID của người dùng cần khôi phục tùy chọn") @PathVariable Long userId) {
        
        log.info("Admin resetting preferences for user ID: {}", userId);
        
        try {
            UserPreferenceResponse defaultPreferences = userPreferenceService.resetToDefaultPreferences(userId);
            return ResponseEntity.ok(defaultPreferences);
        } catch (Exception e) {
            log.error("Error resetting user preferences for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "[ADMIN] Lấy thống kê tùy chọn người dùng",
        description = "Lấy thống kê về việc sử dụng các tùy chọn của người dùng"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thống kê thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPreferenceStatistics() {
        log.info("Getting user preference statistics");
        
        try {
            Map<String, Object> statistics = userPreferenceService.getPreferenceStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error getting preference statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

package com.gha.gender_healthcare_api.settings.controller;

import com.gha.gender_healthcare_api.settings.dto.SystemConfigurationRequest;
import com.gha.gender_healthcare_api.settings.dto.SystemConfigurationResponse;
import com.gha.gender_healthcare_api.settings.entity.ConfigurationAuditLog;
import com.gha.gender_healthcare_api.settings.service.SystemConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller quản lý cấu hình hệ thống
 * Chỉ admin mới có quyền truy cập các API này
 */
@RestController
@RequestMapping("/api/v1/system-configurations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "System Configuration", description = "API quản lý cấu hình hệ thống (Admin only)")
@PreAuthorize("hasRole('ADMIN')")
public class SystemConfigurationController {

    private final SystemConfigurationService systemConfigurationService;

    @Operation(
        summary = "Lấy danh sách cấu hình hệ thống",
        description = "Lấy tất cả cấu hình hệ thống với phân trang và sắp xếp"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping
    public ResponseEntity<Page<SystemConfigurationResponse>> getAllConfigurations(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng item mỗi trang") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "configKey") String sortBy,
            @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Getting all system configurations - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                 page, size, sortBy, sortDir);
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<SystemConfigurationResponse> configurations = systemConfigurationService.getAllConfigurations(pageable);
            return ResponseEntity.ok(configurations);
        } catch (Exception e) {
            log.error("Error getting system configurations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Lấy cấu hình theo key",
        description = "Lấy thông tin chi tiết của một cấu hình theo config key"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy cấu hình thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy cấu hình"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/key/{configKey}")
    public ResponseEntity<SystemConfigurationResponse> getConfigurationByKey(
            @Parameter(description = "Key của cấu hình") @PathVariable String configKey) {
        
        log.info("Getting system configuration by key: {}", configKey);
        
        try {
            return systemConfigurationService.getConfigurationByKey(configKey)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting configuration by key: {}", configKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Lấy cấu hình theo ID",
        description = "Lấy thông tin chi tiết của một cấu hình theo ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy cấu hình thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy cấu hình"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SystemConfigurationResponse> getConfigurationById(
            @Parameter(description = "ID của cấu hình") @PathVariable Long id) {
        
        log.info("Getting system configuration by ID: {}", id);
        
        try {
            return systemConfigurationService.getConfigurationByKey(String.valueOf(id))
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting configuration by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Lấy cấu hình theo nhóm",
        description = "Lấy tất cả cấu hình thuộc một nhóm/category cụ thể"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy cấu hình thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SystemConfigurationResponse>> getConfigurationsByCategory(
            @Parameter(description = "Nhóm cấu hình") @PathVariable String category) {
        
        log.info("Getting system configurations by category: {}", category);
        
        try {
            List<SystemConfigurationResponse> configurations = systemConfigurationService.getConfigurationsByCategory(category);
            return ResponseEntity.ok(configurations);
        } catch (Exception e) {
            log.error("Error getting configurations by category: {}", category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Tìm kiếm cấu hình",
        description = "Tìm kiếm cấu hình theo từ khóa trong key hoặc description"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm kiếm thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<SystemConfigurationResponse>> searchConfigurations(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam String keyword,
            @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng item mỗi trang") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Searching system configurations with keyword: {}", keyword);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SystemConfigurationResponse> configurations = systemConfigurationService.searchConfigurations(keyword, pageable);
            return ResponseEntity.ok(configurations);
        } catch (Exception e) {
            log.error("Error searching configurations with keyword: {}", keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Tạo cấu hình mới",
        description = "Tạo một cấu hình hệ thống mới"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo cấu hình thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "409", description = "Config key đã tồn tại"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PostMapping
    public ResponseEntity<SystemConfigurationResponse> createConfiguration(
            @Parameter(hidden = true) @RequestAttribute("userId") Long adminUserId,
            @Valid @RequestBody SystemConfigurationRequest request) {
        
        log.info("Creating new system configuration with key: {}", request.getConfigKey());
        
        try {
            SystemConfigurationResponse configuration = systemConfigurationService.createConfiguration(request, adminUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(configuration);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for creating configuration: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating system configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Cập nhật cấu hình",
        description = "Cập nhật thông tin của một cấu hình hệ thống"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy cấu hình"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SystemConfigurationResponse> updateConfiguration(
            @Parameter(description = "ID của cấu hình") @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute("userId") Long adminUserId,
            @Valid @RequestBody SystemConfigurationRequest request) {
        
        log.info("Updating system configuration with ID: {}", id);
        
        try {
            SystemConfigurationResponse configuration = systemConfigurationService.updateConfiguration(id, request, adminUserId);
            return ResponseEntity.ok(configuration);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for updating configuration ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating system configuration with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Xóa cấu hình",
        description = "Xóa một cấu hình hệ thống (không thể xóa cấu hình hệ thống core)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "400", description = "Không thể xóa cấu hình hệ thống"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy cấu hình"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfiguration(
            @Parameter(description = "ID của cấu hình") @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute("userId") Long adminUserId) {
        
        log.info("Deleting system configuration with ID: {}", id);
        
        try {
            systemConfigurationService.deleteConfiguration(id, adminUserId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Cannot delete configuration ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error deleting system configuration with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Kích hoạt/vô hiệu hóa cấu hình",
        description = "Thay đổi trạng thái kích hoạt của một cấu hình"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thay đổi trạng thái thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy cấu hình"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleConfigurationStatus(
            @Parameter(description = "ID của cấu hình") @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute("userId") Long adminUserId,
            @RequestBody Map<String, Boolean> requestBody) {
        
        Boolean isActive = requestBody.get("isActive");
        log.info("Toggling configuration status for ID: {} to {}", id, isActive);
        
        try {
            if (isActive == null) {
                return ResponseEntity.badRequest().build();
            }
            
            systemConfigurationService.toggleConfigurationStatus(id, isActive, adminUserId);
            return ResponseEntity.ok(Map.of(
                "id", id,
                "isActive", isActive,
                "message", "Configuration status updated successfully"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Error toggling configuration status for ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error toggling configuration status for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Lấy lịch sử thay đổi cấu hình",
        description = "Lấy lịch sử tất cả thay đổi của một cấu hình cụ thể"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy lịch sử thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy cấu hình"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/{id}/history")
    public ResponseEntity<Page<ConfigurationAuditLog>> getConfigurationHistory(
            @Parameter(description = "ID của cấu hình") @PathVariable Long id,
            @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng item mỗi trang") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting configuration history for ID: {}", id);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ConfigurationAuditLog> history = systemConfigurationService.getConfigurationHistory(id, pageable);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error getting configuration history for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Lấy giá trị cấu hình theo key",
        description = "API công khai để lấy giá trị cấu hình (dành cho các service khác sử dụng)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy giá trị thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy cấu hình")
    })
    @GetMapping("/value/{configKey}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'CONSULTANT')")
    public ResponseEntity<Map<String, String>> getConfigurationValue(
            @Parameter(description = "Key của cấu hình") @PathVariable String configKey,
            @Parameter(description = "Giá trị mặc định nếu không tìm thấy") @RequestParam(required = false) String defaultValue) {
        
        log.debug("Getting configuration value for key: {}", configKey);
        
        try {
            String value = systemConfigurationService.getConfigurationValue(configKey, defaultValue);
            
            if (value != null) {
                return ResponseEntity.ok(Map.of("key", configKey, "value", value));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error getting configuration value for key: {}", configKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

/*
 * Controller cho admin/manager quản lý dịch vụ (service): thêm, sửa, xóa.
 * Đường dẫn: /api/admin/services
 * Chỉ cho phép role ADMIN hoặc MANAGER truy cập.
 */

package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.request.UpdatePriceRequest;
import com.gha.gender_healthcare_api.dto.request.CreateServiceRequest;
import com.gha.gender_healthcare_api.entity.Service;
import com.gha.gender_healthcare_api.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/services")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
public class AdminServiceController {

    @Autowired
    private ServiceService serviceService;

    // Thêm mới dịch vụ
    @PostMapping
    public Service add(@Valid @RequestBody CreateServiceRequest request) {
        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setType(request.getType());
        service.setPrice(request.getPrice());
        return serviceService.addService(service);
    }

    // Sửa dịch vụ
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody CreateServiceRequest request) {
        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setType(request.getType());
        service.setPrice(request.getPrice());
        serviceService.updateService(id, service);
        return ResponseEntity.ok("Cập nhật dịch vụ thành công!");
    }

    // Xóa dịch vụ
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok("Xóa dịch vụ thành công!");
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<String> updatePrice(@PathVariable Long id, @RequestBody @Valid UpdatePriceRequest request) {
        serviceService.updatePrice(id, request.getPrice());
        return ResponseEntity.ok("Cập nhật giá thành công!");
    }
}
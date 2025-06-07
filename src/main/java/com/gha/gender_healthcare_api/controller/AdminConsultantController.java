package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.request.CreateConsultantRequest;
import com.gha.gender_healthcare_api.entity.Consultant;
import com.gha.gender_healthcare_api.service.ConsultantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/consultants")
public class AdminConsultantController {

    @Autowired
    private ConsultantService consultantService;

    @PostMapping
    public ResponseEntity<?> createConsultant(@Valid @RequestBody CreateConsultantRequest request) {
        try {
            Consultant consultant = consultantService.createConsultant(request);
            return ResponseEntity.ok(consultant);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Tạo consultant thất bại: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateConsultant(@PathVariable Long id, @Valid @RequestBody Consultant consultant) {
        try {
            Consultant updated = consultantService.updateConsultant(id, consultant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Cập nhật consultant thất bại: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConsultant(@PathVariable Long id) {
        try {
            consultantService.deleteConsultant(id);
            return ResponseEntity.ok("Xóa consultant thành công!");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Xóa consultant thất bại: " + ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Consultant>> getAllConsultants() {
        return ResponseEntity.ok(consultantService.getAllConsultants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consultant> getConsultantById(@PathVariable Long id) {
        return ResponseEntity.ok(consultantService.getConsultantById(id));
    }
}
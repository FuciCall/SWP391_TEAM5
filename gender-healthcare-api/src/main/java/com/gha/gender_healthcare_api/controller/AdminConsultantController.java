package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.request.CreateConsultantRequest;
import com.gha.gender_healthcare_api.dto.request.UpdateConsultantRequest;
import com.gha.gender_healthcare_api.dto.response.ConsultantDTO;
import com.gha.gender_healthcare_api.entity.Consultant;
import com.gha.gender_healthcare_api.mapper.ConsultantMapper;
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
        Consultant consultant = consultantService.createConsultant(request);
        return ResponseEntity.ok(consultant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateConsultant(
            @PathVariable Long id,
            @Valid @RequestBody UpdateConsultantRequest request) {
        Consultant updated = consultantService.updateConsultant(id, request);
        return ResponseEntity.ok(ConsultantMapper.toDTO(updated));
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
    public ResponseEntity<List<ConsultantDTO>> getAllConsultants() {
        List<Consultant> consultants = consultantService.getAllConsultants();
        List<ConsultantDTO> dtos = consultants.stream()
                .map(ConsultantMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getConsultantById(@PathVariable Long id) {
        Consultant consultant = consultantService.getConsultantById(id);
        if (consultant == null) {
            return ResponseEntity.status(404).body("Không tìm thấy chuyên gia với id: " + id);
        }
        return ResponseEntity.ok(ConsultantMapper.toDTO(consultant));
    }
}
package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.response.ConsultantDTO;
import com.gha.gender_healthcare_api.entity.Consultant;
import com.gha.gender_healthcare_api.mapper.ConsultantMapper;
import com.gha.gender_healthcare_api.repository.ConsultantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultants")
public class ConsultantController {

    @Autowired
    private ConsultantRepository consultantRepository;

    @GetMapping("/by-specialty/{specialtyId}")
    public ResponseEntity<?> getConsultantsBySpecialty(@PathVariable Long specialtyId) {
        List<Consultant> consultants = consultantRepository.findBySpecialtyId(specialtyId);
        if (consultants.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy chuyên gia cho chuyên khoa này!"));
        }
        List<ConsultantDTO> consultantDTOs = consultants.stream()
                .map(ConsultantMapper::toDTO)
                .toList();
        return ResponseEntity.ok(consultantDTOs);
    }
}

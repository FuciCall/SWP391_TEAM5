//: Controller xử lý các API đặt lịch tư vấn online, lấy lịch tư vấn của khách hàng/chuyên gia.

package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.entity.Consultation;
import com.gha.gender_healthcare_api.service.ConsultationService;
import com.gha.gender_healthcare_api.security.JwtTokenProvider;
import com.gha.gender_healthcare_api.dto.response.ConsultationDTO;
import com.gha.gender_healthcare_api.mapper.ConsultationMapper;
import com.gha.gender_healthcare_api.dto.request.ConsultationBookingRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 1. Đặt lịch tư vấn (Book Consultation)
    @PostMapping("/book")
    public ResponseEntity<?> bookConsultation(@RequestBody ConsultationBookingRequest request,
            HttpServletRequest httpRequest) {
        Long customerUserId = getCurrentUserId(httpRequest);
        LocalDateTime dateTime = LocalDateTime.parse(request.getDateTime());
        Consultation booked = consultationService.bookConsultation(
                customerUserId,
                request.getConsultantId(),
                request.getTopic(),
                dateTime);
        ConsultationDTO dto = ConsultationMapper.toDTO(booked);
        return ResponseEntity.ok(dto);
    }

    // 2. Lấy lịch tư vấn của khách hàng hiện tại
    @GetMapping("/my")
    public ResponseEntity<?> getMyConsultations(HttpServletRequest request) {
        Long customerUserId = getCurrentUserId(request);
        List<Consultation> consultations = consultationService.getConsultationsByCustomer(customerUserId);
        List<ConsultationDTO> dtos = consultations.stream()
                .map(ConsultationMapper::toDTO)
                .toList();
        if (dtos.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Không có lịch tư vấn nào.");
            response.put("data", dtos);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(dtos);
    }

    // 3. Lấy lịch tư vấn của chuyên gia theo ngày
    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<?> getConsultationsByConsultantAndDate(
            @PathVariable Long consultantId,
            @RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        List<Consultation> consultations = consultationService.getConsultationsByConsultantAndDate(consultantId, date);
        List<ConsultationDTO> dtos = consultations.stream()
                .map(ConsultationMapper::toDTO)
                .toList();
        if (dtos.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Không có lịch tư vấn nào.");
            response.put("data", dtos);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(dtos);
    }

    // Hàm lấy userId từ JWT
    public Long getCurrentUserId(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtTokenProvider.getJwtSecret().getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Long.class);
        }
        throw new RuntimeException("Không tìm thấy userId trong JWT");
    }
}
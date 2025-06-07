//: Controller xử lý các API đặt lịch tư vấn online, lấy lịch tư vấn của khách hàng/chuyên gia.

package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.request.ConsultationBookingRequest;
import com.gha.gender_healthcare_api.entity.Consultation;
import com.gha.gender_healthcare_api.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @PostMapping("/book")
    public Consultation bookConsultation(@RequestBody ConsultationBookingRequest request, Principal principal) {
        Long customerUserId = getCurrentUserId(principal); // TODO: Lấy userId từ principal hoặc JWT
        return consultationService.bookConsultation(
                customerUserId,
                request.getConsultantId(),
                request.getTopic(),
                request.getDateTime());
    }

    @GetMapping("/my")
    public List<Consultation> getMyConsultations(Principal principal) {
        Long customerUserId = getCurrentUserId(principal);
        return consultationService.getConsultationsByCustomer(customerUserId);
    }

    // Nếu muốn lấy lịch của chuyên gia:
    @GetMapping("/consultant/{consultantId}")
    public List<Consultation> getConsultationsByConsultant(@PathVariable Long consultantId) {
        return consultationService.getConsultationsByConsultant(consultantId);
    }

    private Long getCurrentUserId(Principal principal) {
        // TODO: Lấy userId từ principal hoặc JWT
        return 1L; // Tạm thời trả về 1L để test, bạn cần sửa lại cho đúng
    }
}
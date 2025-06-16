//: Xử lý nghiệp vụ đặt lịch, lấy lịch tư vấn online.

package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.entity.Consultation;
import com.gha.gender_healthcare_api.entity.Consultant;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.repository.ConsultationRepository;
import com.gha.gender_healthcare_api.repository.ConsultantRepository;
import com.gha.gender_healthcare_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Đặt lịch tư vấn (book consultation) với kiểm tra trùng lịch
    public Consultation bookConsultation(Long customerUserId, Long consultantId, String topic, LocalDateTime dateTime) {
        User customer = userRepository.findById(customerUserId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        // Kiểm tra trùng lịch
        boolean exists = consultationRepository.existsByConsultantConsultantIdAndDateTime(consultantId, dateTime);
        if (exists) {
            throw new RuntimeException("Khung giờ này đã có lịch tư vấn!");
        }

        Consultation consultation = new Consultation();
        consultation.setCustomer(customer);
        consultation.setConsultant(consultant);
        consultation.setTopic(topic);
        consultation.setDateTime(dateTime);
        consultation.setStatus("PENDING");

        return consultationRepository.save(consultation);
    }

    // 2. Lấy lịch tư vấn của khách hàng
    public List<Consultation> getConsultationsByCustomer(Long customerUserId) {
        return consultationRepository.findByCustomerUserId(customerUserId);
    }

    // 3. Lấy lịch tư vấn của consultant
    public List<Consultation> getConsultationsByConsultant(Long consultantId) {
        return consultationRepository.findByConsultantConsultantId(consultantId);
    }

    // 4. Lấy danh sách lịch tư vấn của consultant theo ngày (dùng cho lấy slot
    // trống)
    public List<Consultation> getConsultationsByConsultantAndDate(Long consultantId, LocalDate date) {
        return consultationRepository.findByConsultantConsultantIdAndDateTimeBetween(
                consultantId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay());
    }
}
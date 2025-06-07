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

    public Consultation bookConsultation(Long customerUserId, Long consultantId, String topic, LocalDateTime dateTime) {
        User customer = userRepository.findById(customerUserId).orElseThrow();
        Consultant consultant = consultantRepository.findById(consultantId).orElseThrow();

        Consultation consultation = new Consultation();
        consultation.setCustomer(customer);
        consultation.setConsultant(consultant);
        consultation.setTopic(topic);
        consultation.setDateTime(dateTime);
        consultation.setStatus("PENDING");

        return consultationRepository.save(consultation);
    }

    public List<Consultation> getConsultationsByCustomer(Long customerUserId) {
        return consultationRepository.findByCustomerUserId(customerUserId);
    }

    public List<Consultation> getConsultationsByConsultant(Long consultantId) {
        return consultationRepository.findByConsultantConsultantId(consultantId);
    }
}
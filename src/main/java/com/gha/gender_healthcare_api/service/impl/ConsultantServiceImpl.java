package com.gha.gender_healthcare_api.service.impl;

import com.gha.gender_healthcare_api.dto.request.CreateConsultantRequest;
import com.gha.gender_healthcare_api.entity.Consultant;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.repository.ConsultantRepository;
import com.gha.gender_healthcare_api.repository.UserRepository;
import com.gha.gender_healthcare_api.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultantServiceImpl implements ConsultantService {

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Consultant addConsultant(Consultant consultant) {
        return consultantRepository.save(consultant);
    }

    @Override
    public Consultant updateConsultant(Long id, Consultant consultant) {
        Consultant existing = consultantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        existing.setName(consultant.getName());
        existing.setEmail(consultant.getEmail());
        existing.setPhone(consultant.getPhone());
        existing.setDegree(consultant.getDegree());
        existing.setExperienceYears(consultant.getExperienceYears());
        existing.setSpecialty(consultant.getSpecialty());
        existing.setBio(consultant.getBio());
        existing.setStatus(consultant.getStatus());
        // Không cập nhật user ở đây để tránh lỗi liên kết
        return consultantRepository.save(existing);
    }

    @Override
    public void deleteConsultant(Long id) {
        consultantRepository.deleteById(id);
    }

    @Override
    public List<Consultant> getAllConsultants() {
        return consultantRepository.findAll();
    }

    @Override
    public Consultant getConsultantById(Long id) {
        return consultantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
    }

    @Override
    public Consultant createConsultant(CreateConsultantRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Consultant consultant = new Consultant();
        consultant.setUser(user);
        consultant.setName(user.getFullName());
        consultant.setEmail(user.getEmail());
        consultant.setPhone(user.getPhoneNumber());
        consultant.setGender(user.getGender().toString()); // Nếu setGender nhận String
        consultant.setDegree(request.getDegree());
        consultant.setExperienceYears(request.getExperienceYears());
        consultant.setSpecialty(request.getSpecialty());
        consultant.setBio(request.getBio());
        consultant.setStatus(request.getStatus());

        return consultantRepository.save(consultant);
    }
}
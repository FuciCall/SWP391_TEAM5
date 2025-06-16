package com.gha.gender_healthcare_api.service.impl;

import com.gha.gender_healthcare_api.dto.request.CreateConsultantRequest;
import com.gha.gender_healthcare_api.dto.request.UpdateConsultantRequest;
import com.gha.gender_healthcare_api.dto.request.UpdateUserRequest;
import com.gha.gender_healthcare_api.entity.Consultant;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.entity.Specialty;
import com.gha.gender_healthcare_api.repository.ConsultantRepository;
import com.gha.gender_healthcare_api.repository.UserRepository;
import com.gha.gender_healthcare_api.repository.SpecialtyRepository;
import com.gha.gender_healthcare_api.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultantServiceImpl implements ConsultantService {

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Override
    public Consultant addConsultant(Consultant consultant) {
        return consultantRepository.save(consultant);
    }

    @Override
    public Consultant updateConsultant(Long id, UpdateConsultantRequest request) {
        Consultant existing = consultantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        // Cập nhật thông tin chuyên môn Consultant
        if (request.getDegree() != null)
            existing.setDegree(request.getDegree());
        if (request.getExperienceYears() != null)
            existing.setExperienceYears(request.getExperienceYears());
        if (request.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new RuntimeException("Specialty not found"));
            existing.setSpecialty(specialty);
        }
        if (request.getBio() != null)
            existing.setBio(request.getBio());
        if (request.getStatus() != null)
            existing.setStatus(request.getStatus());

        // Cập nhật thông tin User nếu có
        if (request.getUser() != null) {
            User user = existing.getUser();
            UpdateUserRequest userReq = request.getUser();
            if (userReq.getFullName() != null)
                user.setFullName(userReq.getFullName());
            if (userReq.getGender() != null) {
                try {
                    user.setGender(User.Gender.valueOf(userReq.getGender().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Giới tính không hợp lệ. Chỉ chấp nhận MALE, FEMALE, OTHER.");
                }
            }
            if (userReq.getDateOfBirth() != null)
                user.setDateOfBirth(userReq.getDateOfBirth());
            if (userReq.getEmail() != null)
                user.setEmail(userReq.getEmail());
            if (userReq.getPhoneNumber() != null)
                user.setPhoneNumber(userReq.getPhoneNumber());
            userRepository.save(user);

            // ĐỒNG BỘ lại thông tin name, phone, email cho consultant
            existing.setName(user.getFullName());
            existing.setPhone(user.getPhoneNumber());
            existing.setEmail(user.getEmail());
        }

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

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new RuntimeException("Specialty not found"));

        Consultant consultant = new Consultant();
        consultant.setUser(user);
        consultant.setName(user.getFullName());
        consultant.setPhone(user.getPhoneNumber());
        consultant.setEmail(user.getEmail());
        consultant.setGender(user.getGender() != null ? user.getGender().toString() : null);
        consultant.setDegree(request.getDegree());
        consultant.setExperienceYears(request.getExperienceYears());
        consultant.setSpecialty(specialty);
        consultant.setBio(request.getBio());
        consultant.setStatus(request.getStatus());

        return consultantRepository.save(consultant);
    }
}
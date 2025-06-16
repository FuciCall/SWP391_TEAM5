package com.gha.gender_healthcare_api.mapper;

import com.gha.gender_healthcare_api.dto.response.ConsultantDTO;
import com.gha.gender_healthcare_api.entity.Consultant;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.entity.Specialty;

public class ConsultantMapper {
    public static ConsultantDTO toDTO(Consultant consultant) {
        ConsultantDTO dto = new ConsultantDTO();
        dto.setDegree(consultant.getDegree());
        dto.setExperienceYears(consultant.getExperienceYears());

        // Map Specialty to SpecialtyDTO
        Specialty specialty = consultant.getSpecialty();
        if (specialty != null) {
            ConsultantDTO.SpecialtyDTO specialtyDTO = new ConsultantDTO.SpecialtyDTO();
            specialtyDTO.setId(specialty.getId());
            specialtyDTO.setName(specialty.getName());
            specialtyDTO.setDescription(specialty.getDescription());
            dto.setSpecialty(specialtyDTO);
        } else {
            dto.setSpecialty(null);
        }

        dto.setBio(consultant.getBio());
        dto.setStatus(consultant.getStatus());

        User user = consultant.getUser();
        if (user != null) {
            ConsultantDTO.UserDTO userDTO = new ConsultantDTO.UserDTO();
            userDTO.setFullName(user.getFullName());
            userDTO.setGender(user.getGender() != null ? user.getGender().name() : null);
            userDTO.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setLastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null);
            userDTO.setLastActivityAt(user.getLastActivityAt() != null ? user.getLastActivityAt().toString() : null);
            dto.setUser(userDTO);
        }
        return dto;
    }
}
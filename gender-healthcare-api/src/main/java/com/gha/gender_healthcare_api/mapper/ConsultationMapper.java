package com.gha.gender_healthcare_api.mapper;

import com.gha.gender_healthcare_api.dto.response.ConsultationDTO;
import com.gha.gender_healthcare_api.entity.Consultation;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.entity.Consultant;
import com.gha.gender_healthcare_api.entity.Specialty;

public class ConsultationMapper {
    public static ConsultationDTO toDTO(Consultation consultation) {
        ConsultationDTO dto = new ConsultationDTO();
        dto.setConsultationId(consultation.getConsultationId());
        dto.setTopic(consultation.getTopic());
        dto.setDateTime(consultation.getDateTime() != null ? consultation.getDateTime().toString() : null);
        dto.setStatus(consultation.getStatus());

        // Map customer
        User customer = consultation.getCustomer();
        if (customer != null) {
            ConsultationDTO.SimpleUserDTO customerDTO = new ConsultationDTO.SimpleUserDTO();
            customerDTO.setUserId(customer.getUserId());
            customerDTO.setFullName(customer.getFullName());
            customerDTO.setEmail(customer.getEmail());
            customerDTO.setPhoneNumber(customer.getPhoneNumber());
            dto.setCustomer(customerDTO);
        }

        // Map consultant
        Consultant consultant = consultation.getConsultant();
        if (consultant != null) {
            ConsultationDTO.SimpleConsultantDTO consultantDTO = new ConsultationDTO.SimpleConsultantDTO();
            consultantDTO.setConsultantId(consultant.getConsultantId());
            consultantDTO.setName(consultant.getName());
            consultantDTO.setEmail(consultant.getEmail());
            consultantDTO.setPhone(consultant.getPhone());
            consultantDTO.setGender(consultant.getGender());
            consultantDTO.setDegree(consultant.getDegree());
            consultantDTO.setExperienceYears(consultant.getExperienceYears());
            consultantDTO.setBio(consultant.getBio());
            consultantDTO.setStatus(consultant.getStatus());

            // Map specialty
            Specialty specialty = consultant.getSpecialty();
            if (specialty != null) {
                ConsultationDTO.SpecialtyDTO specialtyDTO = new ConsultationDTO.SpecialtyDTO();
                specialtyDTO.setId(specialty.getId());
                specialtyDTO.setName(specialty.getName());
                specialtyDTO.setDescription(specialty.getDescription());
                consultantDTO.setSpecialty(specialtyDTO);
            }
            dto.setConsultant(consultantDTO);
        }

        return dto;
    }
}

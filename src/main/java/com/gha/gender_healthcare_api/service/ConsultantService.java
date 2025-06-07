package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.dto.request.CreateConsultantRequest;
import com.gha.gender_healthcare_api.entity.Consultant;
import java.util.List;

public interface ConsultantService {
    Consultant addConsultant(Consultant consultant);

    Consultant updateConsultant(Long id, Consultant consultant);

    void deleteConsultant(Long id);

    List<Consultant> getAllConsultants();

    Consultant getConsultantById(Long id);

    Consultant createConsultant(CreateConsultantRequest request);
}
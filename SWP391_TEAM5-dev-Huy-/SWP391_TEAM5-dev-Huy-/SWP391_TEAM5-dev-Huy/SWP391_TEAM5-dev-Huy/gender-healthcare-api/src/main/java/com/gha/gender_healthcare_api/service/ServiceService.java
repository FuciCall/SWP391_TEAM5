package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.dto.request.ServiceRequest;
import com.gha.gender_healthcare_api.dto.response.ServiceResponse;

import java.util.List;

public interface ServiceService {
    ServiceResponse createService(ServiceRequest request);
    ServiceResponse updateService(Long id, ServiceRequest request);
    void deleteService(Long id);
    List<ServiceResponse> getAllServices();
    ServiceResponse updatePrice(Long id, Double newPrice);
}

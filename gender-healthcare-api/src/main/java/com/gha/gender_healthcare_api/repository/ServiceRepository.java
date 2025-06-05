package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}

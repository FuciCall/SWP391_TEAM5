package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}
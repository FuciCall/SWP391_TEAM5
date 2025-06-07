package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.ClinicInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicInfoRepository extends JpaRepository<ClinicInfo, Long> {
}

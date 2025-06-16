//: Repository thao tác với bảng Consultant (chuyên gia tư vấn).
package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    List<Consultant> findBySpecialtyId(Long specialtyId);
}
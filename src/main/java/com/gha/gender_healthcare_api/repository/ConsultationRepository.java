//: Repository thao tác với bảng Consultation (đặt lịch tư vấn online).

package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByCustomerUserId(Long userId);

    List<Consultation> findByConsultantConsultantId(Long consultantId);
}
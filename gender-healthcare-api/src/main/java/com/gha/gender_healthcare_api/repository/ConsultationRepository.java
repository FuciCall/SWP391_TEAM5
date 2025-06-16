//: Repository thao tác với bảng Consultation (đặt lịch tư vấn online).

package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByConsultantConsultantId(Long consultantId);

    List<Consultation> findByCustomerUserId(Long customerUserId);

    boolean existsByConsultantConsultantIdAndDateTime(Long consultantId, LocalDateTime dateTime);

    List<Consultation> findByConsultantConsultantIdAndDateTimeBetween(Long consultantId, LocalDateTime start,
            LocalDateTime end);
}
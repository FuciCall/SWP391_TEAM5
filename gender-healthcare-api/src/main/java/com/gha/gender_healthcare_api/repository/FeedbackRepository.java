package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByServiceId(Long serviceId);
    List<Feedback> findByRatingLessThanEqual(Integer rating);
}

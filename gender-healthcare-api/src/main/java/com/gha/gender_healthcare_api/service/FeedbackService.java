package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.dto.request.FeedbackRequest;
import com.gha.gender_healthcare_api.dto.response.FeedbackResponse;
import com.gha.gender_healthcare_api.entity.Feedback;
import com.gha.gender_healthcare_api.mapper.FeedbackMapper;
import com.gha.gender_healthcare_api.repository.FeedbackRepository;
import com.gha.gender_healthcare_api.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final ServiceRepository serviceRepository;


    public List<FeedbackResponse> getAllFeedback() {
        return feedbackRepository.findAll()
                .stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<FeedbackResponse> getFeedbackByServiceId(Long serviceId) {
        return feedbackRepository.findByServiceId(serviceId)
                .stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<FeedbackResponse> getLowRatingFeedbacks(int maxRating) {
        return feedbackRepository.findByRatingLessThanEqual(maxRating)
                .stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    public FeedbackResponse createFeedback(FeedbackRequest feedbackRequest) {
        Feedback feedback = feedbackMapper.toEntity(feedbackRequest);
        feedback.setDate(LocalDateTime.now());
        Feedback saved = feedbackRepository.save(feedback);
        return feedbackMapper.toResponse(saved);
    }

    public FeedbackResponse updateFeedback(Long id, FeedbackRequest feedbackRequest) {
        return feedbackRepository.findById(id).map(f -> {
            f.setComment(feedbackRequest.getComment());
            f.setRating(feedbackRequest.getRating());
            // Đúng cú pháp:
            com.gha.gender_healthcare_api.entity.Service service = serviceRepository.findById(feedbackRequest.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));
            f.setService(service);
            Feedback updated = feedbackRepository.save(f);
            return feedbackMapper.toResponse(updated);
        }).orElseThrow(() -> new RuntimeException("Feedback not found"));
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
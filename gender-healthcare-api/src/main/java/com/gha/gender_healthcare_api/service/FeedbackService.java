package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.entity.Feedback;
import com.gha.gender_healthcare_api.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public List<Feedback> getAllFeedback(){
        return feedbackRepository.findAll();
    }

    public List<Feedback> getFeedbackByServiceId(Long serviceId){
        return feedbackRepository.findByServiceId(serviceId);
    }

    public List<Feedback> getLowRatingFeedbacks(int maxRating){
        return feedbackRepository.findByRatingLessThanEqual(maxRating);
    }

    public Feedback createFeedback(Feedback feedback){
        return feedbackRepository.save(feedback);
    }

    public Feedback updateFeedback(Long id, Feedback updateFeedback){
        return feedbackRepository.findById(id).map(f -> {
            f.setComment(updateFeedback.getComment());
            f.setRating(updateFeedback.getRating());
            f.setDate(updateFeedback.getDate());
            return feedbackRepository.save(f);
                }).orElseThrow(() -> new RuntimeException("Feedback not found"));
    }

    public void deleteFeedback(Long id){
        feedbackRepository.deleteById(id);
    }
}

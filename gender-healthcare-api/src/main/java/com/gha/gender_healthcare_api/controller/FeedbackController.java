package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.entity.Feedback;
import com.gha.gender_healthcare_api.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback(){
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<Feedback>> getFeedbackByServiceId(Long serviceId){
        return ResponseEntity.ok(feedbackService.getFeedbackByServiceId(serviceId));
    }

    @GetMapping("/low-rating/{maxRating}")
    public ResponseEntity<List<Feedback>> getLowRatingFeedbacks(@PathVariable Integer maxRating){
        return ResponseEntity.ok(feedbackService.getLowRatingFeedbacks(maxRating));
    }

    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@RequestBody Feedback feedback){
        return ResponseEntity.ok(feedbackService.createFeedback(feedback));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long id, @RequestBody Feedback updateFeedback){
        return ResponseEntity.ok(feedbackService.updateFeedback(id, updateFeedback));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id){
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}

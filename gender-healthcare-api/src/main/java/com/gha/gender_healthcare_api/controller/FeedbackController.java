package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.request.FeedbackRequest;
import com.gha.gender_healthcare_api.dto.response.FeedbackResponse;
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
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackByServiceId(@PathVariable Long serviceId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByServiceId(serviceId));
    }

    @GetMapping("/low-rating/{maxRating}")
    public ResponseEntity<List<FeedbackResponse>> getLowRatingFeedbacks(@PathVariable Integer maxRating) {
        return ResponseEntity.ok(feedbackService.getLowRatingFeedbacks(maxRating));
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        return ResponseEntity.ok(feedbackService.createFeedback(feedbackRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(@PathVariable Long id, @RequestBody FeedbackRequest feedbackRequest) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, feedbackRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}

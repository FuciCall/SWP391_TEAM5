package com.gha.gender_healthcare_api.dto.response;

import com.gha.gender_healthcare_api.entity.Feedback;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {
    Long id;
    String comment;
    Integer rating;
    LocalDateTime date;
    Long serviceId;
}

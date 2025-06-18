package com.gha.gender_healthcare_api.mapper;

import com.gha.gender_healthcare_api.dto.request.FeedbackRequest;
import com.gha.gender_healthcare_api.dto.response.FeedbackResponse;
import com.gha.gender_healthcare_api.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    Feedback toEntity(FeedbackRequest request);

    @Mapping(target = "id", source = "feedbackId")
    @Mapping(target = "serviceId", source = "service.id")
    FeedbackResponse toResponse(Feedback feedback);
}

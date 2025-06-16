//: DTO nhận dữ liệu từ client khi đặt lịch tư vấn online.

package com.gha.gender_healthcare_api.dto.request;

import lombok.Data;

@Data
public class ConsultationBookingRequest {
    private Long consultantId;
    private String topic;
    private String dateTime; // ISO string, sẽ convert sang LocalDateTime trong service
}
//: DTO nhận dữ liệu từ client khi đặt lịch tư vấn online.

package com.gha.gender_healthcare_api.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConsultationBookingRequest {
    private Long consultantId;
    private String topic;
    private LocalDateTime dateTime;
}
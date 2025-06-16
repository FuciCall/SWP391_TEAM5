package com.gha.gender_healthcare_api.dto.response;

import lombok.Data;

@Data
public class ConsultationDTO {
    private Long consultationId;
    private String topic;
    private String dateTime;
    private String status;
    private SimpleUserDTO customer;
    private SimpleConsultantDTO consultant;

    @Data
    public static class SimpleUserDTO {
        private Long userId;
        private String fullName;
        private String email;
        private String phoneNumber;
    }

    @Data
    public static class SimpleConsultantDTO {
        private Long consultantId;
        private String name;
        private String email;
        private String phone;
        private String gender;
        private String degree;
        private Integer experienceYears;
        private String bio;
        private String status;
        private SpecialtyDTO specialty;
    }

    @Data
    public static class SpecialtyDTO {
        private Long id;
        private String name;
        private String description;
    }
}

package com.gha.gender_healthcare_api.dto.response;

import lombok.Data;

@Data
public class ConsultantDTO {
    private String degree;
    private Integer experienceYears;
    private SpecialtyDTO specialty; // Đổi từ String sang SpecialtyDTO
    private String bio;
    private String status;
    private UserDTO user; // Lồng thông tin user

    @Data
    public static class UserDTO {
        private String fullName;
        private String gender;
        private String dateOfBirth;
        private String email;
        private String phoneNumber;
        private String lastLoginAt;
        private String lastActivityAt;
    }

    @Data
    public static class SpecialtyDTO {
        private Long id;
        private String name;
        private String description;
    }
}
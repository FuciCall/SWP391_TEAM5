package com.gha.gender_healthcare_api.dto.response;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDTO {
    private Long id;
    private String username;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
}
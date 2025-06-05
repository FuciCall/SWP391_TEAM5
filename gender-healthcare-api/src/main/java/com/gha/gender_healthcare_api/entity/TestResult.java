package com.gha.gender_healthcare_api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long testResultId;

    String resultData;

    String resultStatus;

    LocalDateTime resultDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_booking_id", nullable = false, unique = true)
    TestBooking testBooking;

}

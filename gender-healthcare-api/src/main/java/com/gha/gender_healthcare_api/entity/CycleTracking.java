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
public class CycleTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long cycleId;

    LocalDate startDate;

    LocalDate endDate;

    LocalDate ovulationDate;

    LocalDateTime pillTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;
}

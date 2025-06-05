package com.gha.gender_healthcare_api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long consultationId;

    String topic;

    LocalDateTime dateTime;

    String status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    User customer;

    @ManyToOne
    @JoinColumn(name = "consultant_id")
    Consultant consultant;
}

package com.gha.gender_healthcare_api.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(nullable = false)
    String topic;

    @Column(nullable = false)
    LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    User customer;

    @ManyToOne
    @JoinColumn(name = "consultant_id")
    // @ToString.Exclude
    Consultant consultant;

    public enum ConsultationStatus{
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ConsultationStatus status;



}

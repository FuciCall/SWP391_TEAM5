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
public class TestBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long bookingId;

    LocalDateTime bookingDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    Service service;

    @OneToOne(mappedBy = "testBooking", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    TestResult testResult;

    public enum BookingStatus{
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    BookingStatus status;
}

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

    String status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    User customer;

    @ManyToOne
    @JoinColumn(name = "service_id")
    Service service;

    @OneToOne(mappedBy = "testBooking", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    TestResult testResult;

}

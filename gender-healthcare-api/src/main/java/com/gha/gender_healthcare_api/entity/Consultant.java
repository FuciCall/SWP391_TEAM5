package com.gha.gender_healthcare_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Consultant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long consultantId;

    @Column(length = 255, nullable = false)
    String degree;

    @Column(nullable = false)
    Integer experienceYears;

    @Column(length = 255, nullable = false)
    String specialty;

    @Column(columnDefinition = "TEXT")
    String bio;

    @ToString.Exclude
    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Consultation> consultations = new ArrayList<>();


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Answer> answers = new ArrayList<>();


}

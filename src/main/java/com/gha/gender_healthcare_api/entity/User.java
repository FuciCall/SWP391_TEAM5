//: Entity ánh xạ bảng users, lưu thông tin người dùng (local & OAuth2) cho hệ thống.
package com.gha.gender_healthcare_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id", unique = true, nullable = true)
    private String providerId;

    // THÊM TRƯỜNG NÀY ĐỂ HỖ TRỢ createdDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public enum Role {
        CUSTOMER, CONSULTANT, STAFF, MANAGER, ADMIN
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    // Constructor cho người dùng đăng ký thông thường (local)
    public User(String username, String password, String fullName, Gender gender, LocalDate dateOfBirth, String email,
            String phoneNumber, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.provider = "local";
        this.providerId = null;
    }

    // Constructor mới cho người dùng OAuth2 (ví dụ: Google)
    public User(String username, String email, String fullName, Gender gender, Role role, String provider,
            String providerId) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.password = "";
        this.dateOfBirth = null;
        this.phoneNumber = null;
    }

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
}
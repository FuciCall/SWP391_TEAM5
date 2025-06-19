package com.gha.gender_healthcare_api.cycle.entity;

import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "contraceptive_reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContraceptiveReminder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "pill_name", nullable = false)
    private String pillName;
    
    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "pack_start_date")
    private LocalDate packStartDate;
    
    @Column(name = "pack_duration") // 21 or 28 days
    private Integer packDuration;
    
    @Column(name = "break_duration") // 7 days break
    private Integer breakDuration;
    
    @Column(name = "timezone")
    private String timezone;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

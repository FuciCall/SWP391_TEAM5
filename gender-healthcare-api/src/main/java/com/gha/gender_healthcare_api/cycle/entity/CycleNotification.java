package com.gha.gender_healthcare_api.cycle.entity;

import com.gha.gender_healthcare_api.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cycle_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CycleNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    @Column(name = "sent_time")
    private LocalDateTime sentTime;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "is_sent")
    private Boolean isSent = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum NotificationType {
        OVULATION_REMINDER,
        PERIOD_REMINDER,
        CONTRACEPTIVE_PILL_REMINDER,
        FERTILITY_WINDOW,
        PERIOD_LATE_WARNING
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

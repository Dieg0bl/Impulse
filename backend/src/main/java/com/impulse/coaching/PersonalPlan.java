package com.impulse.coaching;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "personal_plan")
public class PersonalPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String coachId;
    private String planDetails;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    // getters y setters
}

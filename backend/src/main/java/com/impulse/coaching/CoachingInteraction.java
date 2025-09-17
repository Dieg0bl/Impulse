package com.impulse.coaching;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coaching_interaction")
public class CoachingInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String coachId;
    private String interactionType;
    private String content;
    private LocalDateTime createdAt;
    // getters y setters
}

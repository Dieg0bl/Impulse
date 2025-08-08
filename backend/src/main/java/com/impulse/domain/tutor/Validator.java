package com.impulse.domain.tutor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="validators")
public class Validator {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String expertise;
    private LocalDateTime createdAt = LocalDateTime.now();
    public Validator(){}
    public Validator(Long userId,String expertise){this.userId=userId;this.expertise=expertise;}
    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public String getExpertise(){return expertise;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}

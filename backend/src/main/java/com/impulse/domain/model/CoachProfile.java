package com.impulse.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "coach_profile")
public class CoachProfile {
    @Id
    private String id;
    private String name;
    private String photo;
    private String specializations;
    private String languages;
    private String timezone;
    private Integer yearsExperience;
    private Integer certificationsCount;
    private Integer totalStudents;
    private Double averageRating;
    private Double successRate;
    private Boolean isActive;
    // getters y setters
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }
}

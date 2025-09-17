package com.impulse.adapters.persistence.coaching.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "coach_profiles")
public class CoachProfileJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "photo")
    private String photo;

    @Column(name = "specializations", columnDefinition = "TEXT")
    private String specializations;

    @Column(name = "languages")
    private String languages;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_sessions")
    private Integer totalSessions;

    @Column(name = "is_available")
    private Boolean isAvailable;

    // Constructors
    public CoachProfileJpaEntity() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getSpecializations() { return specializations; }
    public void setSpecializations(String specializations) { this.specializations = specializations; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Integer getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}

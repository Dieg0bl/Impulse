package com.impulse.domain.pmf;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "surveys")
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, length = 40)
    private String surveyType;
    @Column(nullable = false, columnDefinition = "json")
    private String answersJson;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSurveyType() { return surveyType; }
    public void setSurveyType(String surveyType) { this.surveyType = surveyType; }
    public String getAnswersJson() { return answersJson; }
    public void setAnswersJson(String answersJson) { this.answersJson = answersJson; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

package com.impulse.domain.support;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="nps_responses")
public class NpsResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private int score;
    @Column(columnDefinition="TEXT")
    private String reason;
    private LocalDateTime createdAt = LocalDateTime.now();
    public NpsResponse(){}
    public NpsResponse(Long userId,int score,String reason){this.userId=userId;this.score=score;this.reason=reason;}
    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public int getScore(){return score;}
    public String getReason(){return reason;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}

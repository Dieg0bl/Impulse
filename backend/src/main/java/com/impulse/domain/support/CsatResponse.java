package com.impulse.domain.support;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="csat_responses")
public class CsatResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private int score;
    private LocalDateTime createdAt = LocalDateTime.now();
    public CsatResponse(){}
    public CsatResponse(Long userId,int score){this.userId=userId;this.score=score;}
    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public int getScore(){return score;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}

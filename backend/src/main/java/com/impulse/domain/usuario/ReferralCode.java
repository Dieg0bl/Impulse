package com.impulse.domain.usuario;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="referral_codes")
public class ReferralCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private Long userId;
    @Column(nullable=false, unique=true, length=32)
    private String code;
    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable=false)
    private boolean rewardReleased = false;
    private LocalDateTime rewardReleasedAt;

    public ReferralCode(){}
    public ReferralCode(Long userId, String code){this.userId=userId; this.code=code;}
    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public String getCode(){return code;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public boolean isRewardReleased(){return rewardReleased;}
    public LocalDateTime getRewardReleasedAt(){return rewardReleasedAt;}
    public void markRewardReleased(){ this.rewardReleased = true; this.rewardReleasedAt = LocalDateTime.now(); }
}

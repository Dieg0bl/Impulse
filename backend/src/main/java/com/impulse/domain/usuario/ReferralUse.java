package com.impulse.domain.usuario;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="referral_uses")
public class ReferralUse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private Long codeId;
    @Column(nullable=false)
    private Long referredUserId;
    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ReferralUse(){}
    public ReferralUse(Long codeId, Long referredUserId){this.codeId=codeId; this.referredUserId=referredUserId;}
    public Long getId(){return id;}
    public Long getCodeId(){return codeId;}
    public Long getReferredUserId(){return referredUserId;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}

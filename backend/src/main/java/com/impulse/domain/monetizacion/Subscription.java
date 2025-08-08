package com.impulse.domain.monetizacion;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="subscriptions")
public class Subscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan_id", nullable=false)
    private Plan plan;
    @Column(nullable=false,length=24)
    private String status; // ACTIVE, CANCELED, PAST_DUE
    private LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime endsAt;

    public Subscription(){}
    public Subscription(Long userId, Plan plan, String status){this.userId=userId;this.plan=plan;this.status=status;}
    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public Plan getPlan(){return plan;}
    public String getStatus(){return status;}
    public LocalDateTime getStartedAt(){return startedAt;}
    public LocalDateTime getEndsAt(){return endsAt;}
    public void setStatus(String s){this.status=s;}
    public void setEndsAt(LocalDateTime e){this.endsAt=e;}
}

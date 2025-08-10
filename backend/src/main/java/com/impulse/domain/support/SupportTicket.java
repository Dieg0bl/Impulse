package com.impulse.domain.support;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="support_tickets")
public class SupportTicket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String subject;
    @Column(columnDefinition="TEXT")
    private String body;
    private String status = "OPEN";
    private LocalDateTime createdAt = LocalDateTime.now();
    public SupportTicket(){}
    public SupportTicket(Long userId,String subject,String body){this.userId=userId;this.subject=subject;this.body=body;}
    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public String getSubject(){return subject;}
    public String getBody(){return body;}
    public String getStatus(){return status;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setStatus(String s){this.status=s;}
}

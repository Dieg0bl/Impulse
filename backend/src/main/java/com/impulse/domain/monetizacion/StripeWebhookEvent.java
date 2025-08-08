package com.impulse.domain.monetizacion;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="stripe_webhook_events")
public class StripeWebhookEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=80)
    private String eventId;
    @Column(nullable=false, length=80)
    private String type;
    @Column(nullable=false, columnDefinition="TEXT")
    private String payload;
    private LocalDateTime receivedAt = LocalDateTime.now();
    public StripeWebhookEvent(){}
    public StripeWebhookEvent(String eventId,String type,String payload){this.eventId=eventId;this.type=type;this.payload=payload;}
    public Long getId(){return id;}
    public String getEventId(){return eventId;}
    public String getType(){return type;}
    public String getPayload(){return payload;}
    public LocalDateTime getReceivedAt(){return receivedAt;}
}

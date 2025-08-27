package com.impulse.application.ports;

import com.impulse.domain.monetizacion.StripeWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface StripeWebhookEventPort extends JpaRepository<StripeWebhookEvent, Long> {
    Optional<StripeWebhookEvent> findByEventId(String eventId);
}

package com.impulse.infrastructure.monetizacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.monetizacion.StripeWebhookEvent;
import java.util.Optional;

@Repository
public interface StripeWebhookEventRepository extends JpaRepository<StripeWebhookEvent, Long> {
    Optional<StripeWebhookEvent> findByEventId(String eventId);
}

package com.impulse.features.billing.adapters.in.webhook;

import com.impulse.shared.annotations.Generated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Webhook Controller: Stripe events
 * Idempotent processing, signature verification
 */
@Generated
@RestController
@RequestMapping("/api/webhooks/stripe")
public class StripeWebhookController {
    // TODO: Implement webhook endpoint with signature verification
    // Store raw event metadata in webhook_events (idempotent by event_id)
}

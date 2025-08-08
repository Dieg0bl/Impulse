-- Phase 6: Stripe webhook events log
CREATE TABLE IF NOT EXISTS stripe_webhook_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id VARCHAR(80) NOT NULL UNIQUE,
  type VARCHAR(80) NOT NULL,
  payload TEXT NOT NULL,
  received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

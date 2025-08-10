-- Phase 12: email sends log
CREATE TABLE IF NOT EXISTS email_sends (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  template_code VARCHAR(64) NOT NULL,
  user_id BIGINT NOT NULL,
  rendered_body TEXT NOT NULL,
  sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_email_sends_user (user_id),
  INDEX idx_email_sends_template (template_code)
);

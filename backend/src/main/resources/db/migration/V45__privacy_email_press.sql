-- Phase 9: Privacy consents, email templates, press counter
CREATE TABLE IF NOT EXISTS privacy_consents (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  policy_version VARCHAR(32) NOT NULL,
  granted BOOLEAN NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS email_templates (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  subject VARCHAR(160) NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS press_live_counter (
  id INT PRIMARY KEY CHECK (id=1),
  value BIGINT NOT NULL
);
INSERT IGNORE INTO press_live_counter(id,value) VALUES (1,0);

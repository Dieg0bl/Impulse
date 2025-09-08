-- Privacy Phase 2 enhancements
ALTER TABLE privacy_request ADD COLUMN IF NOT EXISTS export_location VARCHAR(255) NULL AFTER updated_at;
ALTER TABLE privacy_request ADD COLUMN IF NOT EXISTS processed_at TIMESTAMP NULL AFTER updated_at;
ALTER TABLE privacy_request ADD COLUMN IF NOT EXISTS error TEXT NULL;

CREATE TABLE IF NOT EXISTS anonymization_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  field VARCHAR(64) NOT NULL,
  original_hash CHAR(64) NOT NULL,
  anonymized_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  request_id BIGINT NULL,
  INDEX idx_anonym_log_user (user_id)
);

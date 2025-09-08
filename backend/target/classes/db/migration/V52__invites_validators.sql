-- Invites & Validator invites (Phase 2)
CREATE TABLE IF NOT EXISTS invites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  referrer_id BIGINT NOT NULL,
  channel VARCHAR(40) NOT NULL,
  target VARCHAR(255),
  code VARCHAR(64) UNIQUE NOT NULL,
  accepted BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_invites_code (code)
);

CREATE TABLE IF NOT EXISTS validator_invites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  token VARCHAR(128) UNIQUE NOT NULL,
  invited_by BIGINT NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_validator_invites_email (email)
);

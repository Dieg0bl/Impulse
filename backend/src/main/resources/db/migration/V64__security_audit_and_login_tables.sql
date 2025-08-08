-- Security Phase (Fase 3) core tables
CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actor_user_id BIGINT NULL,
  actor_ip VARCHAR(64) NULL,
  event VARCHAR(64) NOT NULL,
  target_type VARCHAR(64) NULL,
  target_id VARCHAR(64) NULL,
  metadata JSON NULL,
  severity ENUM('low','medium','high') DEFAULT 'low',
  INDEX idx_audit_event_ts (event, ts),
  INDEX idx_audit_actor (actor_user_id, ts)
);

CREATE TABLE IF NOT EXISTS login_attempts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_identifier VARCHAR(128) NOT NULL,
  ip VARCHAR(64) NULL,
  success BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_login_user_time (user_identifier, created_at)
);

-- Optional future: API keys
CREATE TABLE IF NOT EXISTS api_keys (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  key_hash CHAR(64) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_used_at TIMESTAMP NULL,
  revoked_at TIMESTAMP NULL,
  UNIQUE KEY uniq_key_hash (key_hash)
);

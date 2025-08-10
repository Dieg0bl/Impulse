-- Privacy & Consents core tables (RGPD / governance)
CREATE TABLE IF NOT EXISTS consents (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  scope ENUM('exposure_public','analytics','marketing','parental','other') NOT NULL,
  decision BOOLEAN NOT NULL,
  surface VARCHAR(64) NOT NULL,
  version VARCHAR(32) NOT NULL,
  ip VARCHAR(64),
  ua VARCHAR(255),
  locale VARCHAR(16),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  revoked_at TIMESTAMP NULL,
  INDEX idx_consents_user_scope (user_id, scope)
);

CREATE TABLE IF NOT EXISTS consent_versions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  scope VARCHAR(64) NOT NULL,
  version VARCHAR(32) NOT NULL,
  text_hash CHAR(64) NOT NULL,
  published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uniq_scope_version (scope, version)
);

CREATE TABLE IF NOT EXISTS visibility_changes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  challenge_id BIGINT NOT NULL,
  from_visibility ENUM('private','validators','public') NOT NULL,
  to_visibility ENUM('private','validators','public') NOT NULL,
  reason VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_vis_user_challenge (user_id, challenge_id)
);

CREATE TABLE IF NOT EXISTS privacy_request (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type ENUM('access','rectification','erasure','restriction','portability','objection') NOT NULL,
  status ENUM('received','in_progress','completed','rejected') NOT NULL DEFAULT 'received',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  notes TEXT
);

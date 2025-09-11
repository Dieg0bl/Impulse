-- schema-h2.sql: single-file schema for local H2 initialization
-- Create tables in correct order and with constraints so Spring can initialize DB before app starts

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uuid VARCHAR(36) NOT NULL,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  phone VARCHAR(20),
  date_of_birth DATE,
  country VARCHAR(2),
  bio CLOB,
  avatar_url VARCHAR(255),
  role VARCHAR(32) NOT NULL DEFAULT 'USER',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
  two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
  two_factor_secret VARCHAR(32),
  privacy_consent BOOLEAN NOT NULL DEFAULT FALSE,
  marketing_consent BOOLEAN NOT NULL DEFAULT FALSE,
  gdpr_consent_date TIMESTAMP NULL,
  last_login_at TIMESTAMP NULL,
  failed_login_attempts INT NOT NULL DEFAULT 0,
  locked_until TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_users_uuid ON users(uuid);
CREATE UNIQUE INDEX ux_users_username ON users(username);
CREATE UNIQUE INDEX ux_users_email ON users(email);

CREATE TABLE challenges (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uuid VARCHAR(36) NOT NULL,
  creator_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  slug VARCHAR(250) NOT NULL,
  description CLOB NOT NULL,
  category VARCHAR(50) NOT NULL,
  difficulty VARCHAR(50) NOT NULL,
  duration_days INT NOT NULL,
  max_participants INT,
  reward_points INT NOT NULL DEFAULT 0,
  evidence_required BOOLEAN NOT NULL DEFAULT TRUE,
  evidence_description CLOB,
  validation_method VARCHAR(50) NOT NULL DEFAULT 'PEER',
  validation_criteria CLOB,
  tags CLOB,
  rules CLOB,
  featured BOOLEAN NOT NULL DEFAULT FALSE,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  start_date TIMESTAMP NULL,
  end_date TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_challenges_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ux_challenges_slug ON challenges(slug);

CREATE TABLE challenge_participations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  challenge_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ENROLLED',
  progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  current_day INT NOT NULL DEFAULT 0,
  points_earned INT NOT NULL DEFAULT 0,
  started_at TIMESTAMP NULL,
  completed_at TIMESTAMP NULL,
  last_activity_at TIMESTAMP NULL,
  notes CLOB,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_participations_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
  CONSTRAINT fk_participations_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ux_participation_challenge_user ON challenge_participations(challenge_id, user_id);

CREATE TABLE evidences (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uuid VARCHAR(36) NOT NULL,
  participation_id BIGINT NOT NULL,
  day_number INT NOT NULL,
  type VARCHAR(20) NOT NULL,
  content CLOB,
  file_path VARCHAR(500),
  file_size BIGINT,
  mime_type VARCHAR(100),
  description CLOB,
  metadata CLOB,
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  validation_score DECIMAL(3,2),
  rejection_reason CLOB,
  submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  validated_at TIMESTAMP NULL,
  CONSTRAINT fk_evidences_participation FOREIGN KEY (participation_id) REFERENCES challenge_participations(id) ON DELETE CASCADE
);

CREATE TABLE evidence_validations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  evidence_id BIGINT NOT NULL,
  validator_id BIGINT,
  validation_type VARCHAR(20) NOT NULL,
  score DECIMAL(3,2) NOT NULL,
  feedback CLOB,
  criteria_scores CLOB,
  confidence_level DECIMAL(3,2),
  validated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_validations_evidence FOREIGN KEY (evidence_id) REFERENCES evidences(id) ON DELETE CASCADE,
  CONSTRAINT fk_validations_validator FOREIGN KEY (validator_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE refresh_tokens (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(255) NOT NULL,
  username VARCHAR(100) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_refresh_token ON refresh_tokens(token);

CREATE TABLE idempotency_tokens (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_idempotency_token ON idempotency_tokens(token);

CREATE TABLE system_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  config_key VARCHAR(100) NOT NULL,
  config_value CLOB NOT NULL,
  config_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
  description CLOB,
  is_public BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_config (config_key, config_value, config_type, description, is_public) VALUES
('app.version', '1.0.0', 'STRING', 'Application version', TRUE),
('upload.max_file_size', '10485760', 'INTEGER', 'Max file size in bytes (10MB)', TRUE),
('features.registration_open', 'true', 'BOOLEAN', 'Whether registration is open', TRUE);
-- Minimal H2 schema for local smoke/e2e tests
-- This creates just the tables needed for the smoke flow (users, challenges,
-- challenge_participations, evidences) plus refresh and idempotency tables.

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uuid VARCHAR(36),
  username VARCHAR(50),
  email VARCHAR(100),
  password_hash VARCHAR(255),
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  status VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS challenges (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255),
  description TEXT,
  creator_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT FK_challenge_creator FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS challenge_participations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  challenge_id BIGINT,
  user_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT FK_participation_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id),
  CONSTRAINT FK_participation_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS evidences (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  object_key VARCHAR(512),
  participation_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT FK_evidence_participation FOREIGN KEY (participation_id) REFERENCES challenge_participations(id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(512) NOT NULL,
  user_id BIGINT NOT NULL,
  expiry_date TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT FK_refresh_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS idempotency_tokens (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

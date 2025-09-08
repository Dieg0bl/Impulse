-- =====================================================================
-- IMPULSE – Script Maestro MySQL 8.x (LEAN VERSION)
-- Hexagonal architecture implementation
-- =====================================================================

CREATE DATABASE IF NOT EXISTS impulse
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE impulse;

SET SESSION time_zone = '+00:00';
SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';
SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;
SET FOREIGN_KEY_CHECKS = 0;

-- Core Users table
CREATE TABLE IF NOT EXISTS users (
  id                 CHAR(36) PRIMARY KEY,
  email              VARCHAR(254) NOT NULL UNIQUE,
  username           VARCHAR(100) NOT NULL UNIQUE,
  password_hash      CHAR(60) NOT NULL,
  status             ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
  created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  last_login_at      DATETIME(3) NULL,
  KEY idx_users_status (status),
  KEY idx_users_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Core Challenges table
CREATE TABLE IF NOT EXISTS challenges (
  id                 CHAR(36) PRIMARY KEY,
  title              VARCHAR(200) NOT NULL,
  description        TEXT NOT NULL,
  creator_id         CHAR(36) NOT NULL,
  status             ENUM('DRAFT','ACTIVE','COMPLETED','CANCELLED') NOT NULL DEFAULT 'DRAFT',
  created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  completed_at       DATETIME(3) NULL,
  KEY idx_challenges_creator (creator_id),
  KEY idx_challenges_status (status),
  KEY idx_challenges_created (created_at),
  CONSTRAINT fk_challenges_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Evidence table
CREATE TABLE IF NOT EXISTS evidence (
  id                 CHAR(36) PRIMARY KEY,
  challenge_id       CHAR(36) NOT NULL,
  user_id            CHAR(36) NOT NULL,
  content            TEXT,
  media_url          VARCHAR(512),
  status             ENUM('PENDING','APPROVED','REJECTED','UNDER_REVIEW') NOT NULL DEFAULT 'PENDING',
  submitted_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  reviewed_at        DATETIME(3) NULL,
  KEY idx_evidence_challenge (challenge_id),
  KEY idx_evidence_user (user_id),
  KEY idx_evidence_status (status),
  KEY idx_evidence_submitted (submitted_at),
  CONSTRAINT fk_evidence_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
  CONSTRAINT fk_evidence_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Validations table
CREATE TABLE IF NOT EXISTS validations (
  id                 CHAR(36) PRIMARY KEY,
  challenge_id       CHAR(36) NOT NULL,
  validator_id       CHAR(36) NOT NULL,
  evidence_id        CHAR(36) NOT NULL,
  status             ENUM('ACCEPTED','REJECTED') NOT NULL,
  comment            VARCHAR(512),
  created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_validations_challenge (challenge_id),
  KEY idx_validations_validator (validator_id),
  KEY idx_validations_evidence (evidence_id),
  KEY idx_validations_status (status),
  CONSTRAINT fk_validations_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
  CONSTRAINT fk_validations_validator FOREIGN KEY (validator_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_validations_evidence FOREIGN KEY (evidence_id) REFERENCES evidence(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- Sample data for testing
INSERT INTO users (id, email, username, password_hash) VALUES 
('550e8400-e29b-41d4-a716-446655440000', 'admin@impulse.com', 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('550e8400-e29b-41d4-a716-446655440001', 'user@impulse.com', 'user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON DUPLICATE KEY UPDATE id = id;

SELECT 'IMPULSE Database - LEAN Schema Created Successfully' AS status;

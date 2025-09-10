-- =============================================================================
-- IMPULSE LEAN v1 - Database Schema Creation
-- Version: V1__Create_base_schema.sql
-- Author: IMPULSE Team
-- Date: September 2025
-- =============================================================================

-- Users table (Enhanced with all required fields)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    country VARCHAR(2), -- ISO 3166-1 alpha-2
    bio TEXT,
    avatar_url VARCHAR(255),
    role ENUM('GUEST', 'USER', 'VALIDATOR', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN') NOT NULL DEFAULT 'USER',
    status ENUM('PENDING', 'ACTIVE', 'SUSPENDED', 'BANNED', 'DELETED') NOT NULL DEFAULT 'PENDING',
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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_users_email (email),
    INDEX idx_users_username (username),
    INDEX idx_users_uuid (uuid),
    INDEX idx_users_role (role),
    INDEX idx_users_status (status),
    INDEX idx_users_created_at (created_at)
);

-- Challenges table
CREATE TABLE challenges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    creator_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(250) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    category ENUM('FITNESS', 'NUTRITION', 'MINDFULNESS', 'PRODUCTIVITY', 'LEARNING', 'SOCIAL', 'CREATIVE', 'ENVIRONMENTAL') NOT NULL,
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') NOT NULL,
    duration_days INT NOT NULL,
    max_participants INT,
    reward_points INT NOT NULL DEFAULT 0,
    evidence_required BOOLEAN NOT NULL DEFAULT TRUE,
    evidence_description TEXT,
    validation_method ENUM('AUTOMATIC', 'PEER', 'MODERATOR', 'SELF') NOT NULL DEFAULT 'PEER',
    validation_criteria TEXT,
    tags JSON,
    rules TEXT,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED', 'DELETED') NOT NULL DEFAULT 'DRAFT',
    start_date TIMESTAMP NULL,
    end_date TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_challenges_creator_id (creator_id),
    INDEX idx_challenges_slug (slug),
    INDEX idx_challenges_category (category),
    INDEX idx_challenges_difficulty (difficulty),
    INDEX idx_challenges_status (status),
    INDEX idx_challenges_start_date (start_date),
    INDEX idx_challenges_featured (featured),
    FULLTEXT idx_challenges_search (title, description)
);

-- Challenge Participations table
CREATE TABLE challenge_participations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challenge_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('ENROLLED', 'ACTIVE', 'COMPLETED', 'FAILED', 'WITHDRAWN') NOT NULL DEFAULT 'ENROLLED',
    progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    current_day INT NOT NULL DEFAULT 0,
    points_earned INT NOT NULL DEFAULT 0,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    last_activity_at TIMESTAMP NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_challenge_user (challenge_id, user_id),
    INDEX idx_participations_user_id (user_id),
    INDEX idx_participations_status (status),
    INDEX idx_participations_started_at (started_at)
);

-- Evidence Submissions table
CREATE TABLE evidences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    participation_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    type ENUM('IMAGE', 'VIDEO', 'TEXT', 'FILE', 'LINK') NOT NULL,
    content TEXT, -- URL for files, text content, or description
    file_path VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),
    description TEXT,
    metadata JSON,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'FLAGGED') NOT NULL DEFAULT 'PENDING',
    validation_score DECIMAL(3,2), -- 0.00 to 1.00
    rejection_reason TEXT,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    validated_at TIMESTAMP NULL,
    
    FOREIGN KEY (participation_id) REFERENCES challenge_participations(id) ON DELETE CASCADE,
    INDEX idx_evidences_participation_id (participation_id),
    INDEX idx_evidences_day_number (day_number),
    INDEX idx_evidences_status (status),
    INDEX idx_evidences_submitted_at (submitted_at)
);

-- Evidence Validations table
CREATE TABLE evidence_validations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evidence_id BIGINT NOT NULL,
    validator_id BIGINT,
    validation_type ENUM('PEER', 'MODERATOR', 'AUTOMATIC') NOT NULL,
    score DECIMAL(3,2) NOT NULL, -- 0.00 to 1.00
    feedback TEXT,
    criteria_scores JSON, -- Detailed scoring per criteria
    confidence_level DECIMAL(3,2), -- AI confidence or validator certainty
    validated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (evidence_id) REFERENCES evidences(id) ON DELETE CASCADE,
    FOREIGN KEY (validator_id) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_evidence_validator (evidence_id, validator_id),
    INDEX idx_validations_evidence_id (evidence_id),
    INDEX idx_validations_validator_id (validator_id),
    INDEX idx_validations_score (score)
);

-- User Stats table
CREATE TABLE user_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_challenges_created INT NOT NULL DEFAULT 0,
    total_challenges_completed INT NOT NULL DEFAULT 0,
    total_challenges_failed INT NOT NULL DEFAULT 0,
    total_points_earned INT NOT NULL DEFAULT 0,
    total_validations_done INT NOT NULL DEFAULT 0,
    streak_current INT NOT NULL DEFAULT 0,
    streak_best INT NOT NULL DEFAULT 0,
    reputation_score DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    level_current INT NOT NULL DEFAULT 1,
    experience_points INT NOT NULL DEFAULT 0,
    badges_earned JSON, -- Array of badge IDs
    achievements JSON, -- Array of achievement objects
    last_activity_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_stats (user_id),
    INDEX idx_stats_reputation (reputation_score),
    INDEX idx_stats_level (level_current),
    INDEX idx_stats_streak (streak_current)
);

-- System Configuration table
CREATE TABLE system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type ENUM('STRING', 'INTEGER', 'BOOLEAN', 'JSON') NOT NULL DEFAULT 'STRING',
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_config_key (config_key),
    INDEX idx_config_public (is_public)
);

-- Audit Log table
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action ENUM('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'VALIDATE') NOT NULL,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_user_id (user_id),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_created_at (created_at)
);

-- Insert default system configuration
INSERT INTO system_config (config_key, config_value, config_type, description, is_public) VALUES
('app.version', '1.0.0', 'STRING', 'Application version', true),
('challenges.max_duration_days', '365', 'INTEGER', 'Maximum challenge duration in days', true),
('challenges.min_duration_days', '1', 'INTEGER', 'Minimum challenge duration in days', true),
('validation.peer_required_count', '3', 'INTEGER', 'Number of peer validations required', false),
('validation.approval_threshold', '0.7', 'STRING', 'Approval threshold (0.0-1.0)', false),
('points.challenge_completion_base', '100', 'INTEGER', 'Base points for challenge completion', true),
('points.validation_reward', '10', 'INTEGER', 'Points for doing a validation', true),
('upload.max_file_size', '10485760', 'INTEGER', 'Max file size in bytes (10MB)', true),
('features.registration_open', 'true', 'BOOLEAN', 'Whether registration is open', true),
('features.challenge_creation_open', 'true', 'BOOLEAN', 'Whether challenge creation is open', true);

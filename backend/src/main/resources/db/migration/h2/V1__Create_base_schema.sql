-- H2-compatible base schema for local testing
-- Simplified from MySQL schema: use IDENTITY/DEFAULTs and avoid MySQL-specific clauses

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

CREATE UNIQUE INDEX uk_users_uuid ON users(uuid);
CREATE UNIQUE INDEX uk_users_username ON users(username);
CREATE UNIQUE INDEX uk_users_email ON users(email);

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_challenges_creator_id ON challenges(creator_id);
CREATE UNIQUE INDEX uk_challenges_slug ON challenges(slug);

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_challenge_user ON challenge_participations(challenge_id, user_id);
CREATE INDEX idx_participations_user_id ON challenge_participations(user_id);

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
    validation_score DECIMAL(5,2),
    rejection_reason CLOB,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    validated_at TIMESTAMP NULL
);

CREATE INDEX idx_evidences_participation_id ON evidences(participation_id);

CREATE TABLE evidence_validations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evidence_id BIGINT NOT NULL,
    validator_id BIGINT,
    validation_type VARCHAR(20) NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    feedback CLOB,
    criteria_scores CLOB,
    confidence_level DECIMAL(5,2),
    validated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

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

-- Insert a few base settings if not present
MERGE INTO system_config (config_key, config_value, config_type, description, is_public) KEY(config_key)
VALUES ('app.version', '1.0.0', 'STRING', 'Application version', TRUE);

MERGE INTO system_config (config_key, config_value, config_type, description, is_public) KEY(config_key)
VALUES ('upload.max_file_size', '10485760', 'INTEGER', 'Max file size in bytes (10MB)', TRUE);

MERGE INTO system_config (config_key, config_value, config_type, description, is_public) KEY(config_key)
VALUES ('features.registration_open', 'true', 'BOOLEAN', 'Whether registration is open', TRUE);

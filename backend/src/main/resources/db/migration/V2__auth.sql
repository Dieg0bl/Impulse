-- IMPULSE v1.0 Authentication Module - Anexo 1
-- Migration V2: Auth tables for JWT RS256, refresh rotation, email verification, sessions
-- Compliant with IMPULSE specification and hexagonal architecture

-- Refresh tokens table - implements token rotation per Anexo 1
CREATE TABLE auth_refresh_tokens (
    id BINARY(16) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL, -- SHA-256 hash
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP NULL,
    revoked_at TIMESTAMP NULL,
    replaced_by_token_id BINARY(16) NULL, -- For rotation tracking
    user_agent TEXT,
    ip_address VARCHAR(45), -- IPv6 compatible
    is_revoked BOOLEAN DEFAULT FALSE,

    INDEX idx_auth_refresh_tokens_user_id (user_id),
    INDEX idx_auth_refresh_tokens_token_hash (token_hash),
    INDEX idx_auth_refresh_tokens_expires_at (expires_at),
    INDEX idx_auth_refresh_tokens_active (user_id, is_revoked, expires_at),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (replaced_by_token_id) REFERENCES auth_refresh_tokens(id) ON DELETE SET NULL
);

-- Password reset table - one-time use tokens
CREATE TABLE auth_password_resets (
    id BINARY(16) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL, -- SHA-256 hash
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP NULL,
    user_agent TEXT,
    ip_address VARCHAR(45),

    INDEX idx_auth_password_resets_user_id (user_id),
    INDEX idx_auth_password_resets_token_hash (token_hash),
    INDEX idx_auth_password_resets_expires_at (expires_at),
    INDEX idx_auth_password_resets_active (user_id, used_at, expires_at),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Email verification table - required for social functions per Anexo 1
CREATE TABLE email_verifications (
    id BINARY(16) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    token_hash VARCHAR(64) NOT NULL, -- SHA-256 hash
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verified_at TIMESTAMP NULL,
    user_agent TEXT,
    ip_address VARCHAR(45),

    INDEX idx_email_verifications_user_id (user_id),
    INDEX idx_email_verifications_email (email),
    INDEX idx_email_verifications_token_hash (token_hash),
    INDEX idx_email_verifications_expires_at (expires_at),
    INDEX idx_email_verifications_active (user_id, email, verified_at, expires_at),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Auth sessions table - session management and tracking
CREATE TABLE auth_sessions (
    id BINARY(16) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_agent TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    refresh_token_id BINARY(16) NULL, -- Link to current refresh token

    INDEX idx_auth_sessions_user_id (user_id),
    INDEX idx_auth_sessions_expires_at (expires_at),
    INDEX idx_auth_sessions_active (user_id, is_active, expires_at),
    INDEX idx_auth_sessions_refresh_token (refresh_token_id),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (refresh_token_id) REFERENCES auth_refresh_tokens(id) ON DELETE SET NULL
);

-- Update users table to include email_verified flag for social functions
ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD INDEX idx_users_email_verified (email_verified);

-- Audit table for authentication events (security logging)
CREATE TABLE auth_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    event_type VARCHAR(50) NOT NULL, -- LOGIN, LOGOUT, REGISTER, PASSWORD_RESET, etc.
    event_status VARCHAR(20) NOT NULL, -- SUCCESS, FAILURE, BLOCKED
    details JSON,
    user_agent TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_auth_audit_log_user_id (user_id),
    INDEX idx_auth_audit_log_event_type (event_type),
    INDEX idx_auth_audit_log_created_at (created_at),
    INDEX idx_auth_audit_log_ip_address (ip_address),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Cleanup procedures for expired tokens (security maintenance)
DELIMITER //

CREATE PROCEDURE CleanupExpiredAuthTokens()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;

    START TRANSACTION;

    -- Delete expired refresh tokens
    DELETE FROM auth_refresh_tokens
    WHERE expires_at < NOW() OR is_revoked = TRUE;

    -- Delete expired password reset tokens
    DELETE FROM auth_password_resets
    WHERE expires_at < NOW() OR used_at IS NOT NULL;

    -- Delete expired email verification tokens
    DELETE FROM email_verifications
    WHERE expires_at < NOW() OR verified_at IS NOT NULL;

    -- Deactivate expired sessions
    UPDATE auth_sessions
    SET is_active = FALSE
    WHERE expires_at < NOW() AND is_active = TRUE;

    COMMIT;
END //

DELIMITER ;

-- Initial configuration data for auth module
INSERT INTO auth_audit_log (event_type, event_status, details, user_agent, ip_address)
VALUES ('SYSTEM_INIT', 'SUCCESS',
        JSON_OBJECT('message', 'Authentication module initialized', 'version', 'v1.0'),
        'IMPULSE-SYSTEM', '127.0.0.1');

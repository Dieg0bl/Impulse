-- IMPULSE - Schema maestro MySQL 8
-- Versión: 1.0.0
-- Charset: utf8mb4 (soporte completo Unicode + emojis)
-- Engine: InnoDB (transacciones + foreign keys)
-- Timezone: UTC

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET time_zone = '+00:00';

-- =================================
-- USUARIOS Y AUTENTICACIÓN
-- =================================

CREATE TABLE users (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL COMMENT 'bcrypt hash',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500),
    bio TEXT,
    birth_date DATE,
    timezone VARCHAR(50) DEFAULT 'UTC',
    locale VARCHAR(10) DEFAULT 'es-ES',
    
    -- Estado y roles
    status ENUM('PENDING', 'ACTIVE', 'SUSPENDED', 'DELETED') NOT NULL DEFAULT 'PENDING',
    role ENUM('GUEST', 'USER', 'VALIDATOR', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN') NOT NULL DEFAULT 'USER',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email_verified_at TIMESTAMP NULL,
    
    -- Configuración de privacidad
    profile_visibility ENUM('PUBLIC', 'PRIVATE', 'FRIENDS') NOT NULL DEFAULT 'PRIVATE',
    allow_invitations BOOLEAN NOT NULL DEFAULT TRUE,
    allow_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    
    -- Índices
    INDEX idx_users_email (email),
    INDEX idx_users_username (username),
    INDEX idx_users_status (status),
    INDEX idx_users_role (role),
    INDEX idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tokens de refresh JWT
CREATE TABLE refresh_tokens (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    device_info VARCHAR(500),
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- RETOS (CHALLENGES)
-- =================================

CREATE TABLE challenges (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL COMMENT 'Creador del reto',
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    
    -- Fechas importantes
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Estado del reto
    status ENUM('DRAFT', 'ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'DRAFT',
    visibility ENUM('PUBLIC', 'PRIVATE', 'TEAM') NOT NULL DEFAULT 'PRIVATE',
    
    -- Configuración
    max_validators INT NOT NULL DEFAULT 3,
    min_evidences INT NOT NULL DEFAULT 1,
    auto_approve BOOLEAN NOT NULL DEFAULT FALSE,
    allow_late_submission BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Metadatos
    tags JSON COMMENT 'Array de tags del reto',
    metadata JSON COMMENT 'Configuración adicional',
    
    -- Scoring
    difficulty_level TINYINT NOT NULL DEFAULT 1 COMMENT '1=Fácil, 5=Muy difícil',
    priority_score DECIMAL(3,2) NOT NULL DEFAULT 0.5 COMMENT 'CPS: 0-1',
    
    CONSTRAINT fk_challenges_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_challenges_user_id (user_id),
    INDEX idx_challenges_status (status),
    INDEX idx_challenges_visibility (visibility),
    INDEX idx_challenges_category (category),
    INDEX idx_challenges_end_date (end_date),
    INDEX idx_challenges_priority_score (priority_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Participación en retos
CREATE TABLE challenge_participations (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    challenge_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    status ENUM('PENDING', 'ACTIVE', 'COMPLETED', 'WITHDRAWN') NOT NULL DEFAULT 'ACTIVE',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    
    UNIQUE KEY uk_challenge_participation (challenge_id, user_id),
    CONSTRAINT fk_participation_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    CONSTRAINT fk_participation_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_participation_user_id (user_id),
    INDEX idx_participation_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- EVIDENCIAS
-- =================================

CREATE TABLE evidences (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    challenge_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL COMMENT 'Quien sube la evidencia',
    title VARCHAR(200) NOT NULL,
    description TEXT,
    
    -- Contenido multimedia
    media_type ENUM('TEXT', 'IMAGE', 'VIDEO', 'DOCUMENT') NOT NULL DEFAULT 'TEXT',
    media_url VARCHAR(1000) COMMENT 'URL del archivo en storage',
    media_size_bytes BIGINT COMMENT 'Tamaño del archivo',
    media_metadata JSON COMMENT 'Metadatos del archivo (dimensiones, duración, etc)',
    
    -- Estado y validación
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'FLAGGED') NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    
    -- Validación automática
    auto_validated BOOLEAN NOT NULL DEFAULT FALSE,
    virus_scan_status ENUM('PENDING', 'CLEAN', 'INFECTED', 'ERROR') DEFAULT 'PENDING',
    content_hash VARCHAR(64) COMMENT 'Hash del contenido para detectar duplicados',
    
    CONSTRAINT fk_evidences_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    CONSTRAINT fk_evidences_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_evidences_challenge_id (challenge_id),
    INDEX idx_evidences_user_id (user_id),
    INDEX idx_evidences_status (status),
    INDEX idx_evidences_submitted_at (submitted_at),
    INDEX idx_evidences_content_hash (content_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- VALIDACIONES
-- =================================

CREATE TABLE evidence_validations (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    evidence_id CHAR(36) NOT NULL,
    validator_id CHAR(36) NOT NULL COMMENT 'Usuario que valida',
    decision ENUM('APPROVED', 'REJECTED') NOT NULL,
    feedback TEXT COMMENT 'Feedback obligatorio en caso de rechazo',
    confidence_level TINYINT NOT NULL DEFAULT 5 COMMENT '1=Muy inseguro, 5=Muy seguro',
    
    -- Timestamps
    validated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    time_taken_seconds INT COMMENT 'Tiempo que tardó en validar',
    
    UNIQUE KEY uk_evidence_validator (evidence_id, validator_id),
    CONSTRAINT fk_validation_evidence FOREIGN KEY (evidence_id) REFERENCES evidences(id) ON DELETE CASCADE,
    CONSTRAINT fk_validation_validator FOREIGN KEY (validator_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_validations_validator_id (validator_id),
    INDEX idx_validations_decision (decision),
    INDEX idx_validations_validated_at (validated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Invitaciones para ser validador
CREATE TABLE validator_invitations (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    challenge_id CHAR(36) NOT NULL,
    inviter_id CHAR(36) NOT NULL COMMENT 'Quien invita',
    invitee_id CHAR(36) NOT NULL COMMENT 'Quien es invitado',
    status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
    message TEXT COMMENT 'Mensaje personalizado de la invitación',
    
    invited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP NULL,
    expires_at TIMESTAMP NOT NULL,
    
    UNIQUE KEY uk_validator_invitation (challenge_id, invitee_id),
    CONSTRAINT fk_invitation_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    CONSTRAINT fk_invitation_inviter FOREIGN KEY (inviter_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_invitation_invitee FOREIGN KEY (invitee_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_invitations_invitee_id (invitee_id),
    INDEX idx_invitations_status (status),
    INDEX idx_invitations_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- COACHES Y MARKETPLACE
-- =================================

CREATE TABLE coaches (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) UNIQUE NOT NULL,
    level ENUM('STARTER', 'RISING', 'CHAMPION') NOT NULL DEFAULT 'STARTER',
    score DECIMAL(5,2) NOT NULL DEFAULT 0,
    
    -- Stripe Connect
    stripe_account_id VARCHAR(64) UNIQUE,
    stripe_onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    payout_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Configuración marketplace
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    hourly_rate DECIMAL(10,2),
    bio_coach TEXT,
    specialties JSON COMMENT 'Array de especialidades',
    availability JSON COMMENT 'Configuración de disponibilidad',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_coaches_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_coaches_level (level),
    INDEX idx_coaches_score (score),
    INDEX idx_coaches_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Estadísticas semanales de coaches
CREATE TABLE coach_stats_weekly (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    coach_id CHAR(36) NOT NULL,
    week_start DATE NOT NULL,
    
    -- Métricas JSON
    metrics_json JSON NOT NULL COMMENT 'Métricas detalladas de la semana',
    score DECIMAL(5,2) NOT NULL,
    level ENUM('STARTER', 'RISING', 'CHAMPION') NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_coach_stats_week (coach_id, week_start),
    CONSTRAINT fk_coach_stats_coach FOREIGN KEY (coach_id) REFERENCES coaches(id) ON DELETE CASCADE,
    INDEX idx_coach_stats_week_start (week_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- SCORING Y ALGORITMOS
-- =================================

CREATE TABLE user_scores (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    
    -- Scores principales
    uci_score DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT 'User Consistency Index (0-100)',
    vts_score DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT 'Validator Trust Score (0-100)',
    rqs_score DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT 'Referral Quality Score (0-100)',
    frs_score DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT 'Fraud Risk Score (0-100)',
    
    -- Metadatos
    last_activity_at TIMESTAMP NULL,
    calculation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    calculation_details JSON COMMENT 'Detalles del cálculo para auditoría',
    
    UNIQUE KEY uk_user_scores (user_id),
    CONSTRAINT fk_user_scores_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_scores_uci (uci_score),
    INDEX idx_user_scores_frs (frs_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- SISTEMA DE REFERIDOS
-- =================================

CREATE TABLE referral_codes (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    
    -- Estadísticas
    uses_count INT NOT NULL DEFAULT 0,
    max_uses INT NOT NULL DEFAULT 100,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Configuración
    reward_type ENUM('MONTH_PRO', 'DISCOUNT', 'CREDITS') NOT NULL DEFAULT 'MONTH_PRO',
    reward_value DECIMAL(10,2) NOT NULL DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    
    CONSTRAINT fk_referral_codes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_referral_codes_user_id (user_id),
    INDEX idx_referral_codes_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE referrals (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    referral_code_id CHAR(36) NOT NULL,
    referrer_id CHAR(36) NOT NULL COMMENT 'Quien refiere',
    referred_id CHAR(36) NOT NULL COMMENT 'Quien es referido',
    
    -- Anti-fraude
    ip_address VARCHAR(45),
    device_fingerprint VARCHAR(255),
    status ENUM('PENDING', 'VALIDATED', 'REWARDED', 'FRAUDULENT') NOT NULL DEFAULT 'PENDING',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    validated_at TIMESTAMP NULL,
    
    UNIQUE KEY uk_referrals_referred (referred_id),
    CONSTRAINT fk_referrals_code FOREIGN KEY (referral_code_id) REFERENCES referral_codes(id) ON DELETE CASCADE,
    CONSTRAINT fk_referrals_referrer FOREIGN KEY (referrer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_referrals_referred FOREIGN KEY (referred_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_referrals_referrer_id (referrer_id),
    INDEX idx_referrals_status (status),
    INDEX idx_referrals_ip_address (ip_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- MODERACIÓN Y COMPLIANCE
-- =================================

CREATE TABLE content_reports (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    reporter_id CHAR(36) NOT NULL,
    
    -- Contenido reportado
    content_type ENUM('CHALLENGE', 'EVIDENCE', 'USER', 'COMMENT') NOT NULL,
    content_id CHAR(36) NOT NULL,
    
    -- Detalles del reporte
    reason ENUM('SPAM', 'HATE_SPEECH', 'HARASSMENT', 'FALSE_INFO', 'COPYRIGHT', 'ADULT_CONTENT', 'OTHER') NOT NULL,
    description TEXT NOT NULL,
    
    -- Estado y resolución
    status ENUM('PENDING', 'UNDER_REVIEW', 'RESOLVED', 'DISMISSED') NOT NULL DEFAULT 'PENDING',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') NOT NULL DEFAULT 'MEDIUM',
    
    reviewed_by CHAR(36) NULL COMMENT 'Moderador que revisó',
    reviewed_at TIMESTAMP NULL,
    resolution TEXT COMMENT 'Explicación de la resolución',
    action_taken ENUM('NONE', 'WARNING', 'CONTENT_REMOVED', 'USER_SUSPENDED', 'USER_BANNED') NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reports_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_reports_status (status),
    INDEX idx_reports_priority (priority),
    INDEX idx_reports_content (content_type, content_id),
    INDEX idx_reports_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE content_appeals (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    report_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL COMMENT 'Usuario que apela',
    
    reason TEXT NOT NULL,
    additional_info TEXT,
    
    status ENUM('PENDING', 'UNDER_REVIEW', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    reviewed_by CHAR(36) NULL,
    reviewed_at TIMESTAMP NULL,
    decision_reason TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_appeals_report (report_id),
    CONSTRAINT fk_appeals_report FOREIGN KEY (report_id) REFERENCES content_reports(id) ON DELETE CASCADE,
    CONSTRAINT fk_appeals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_appeals_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_appeals_status (status),
    INDEX idx_appeals_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- GDPR Y PRIVACIDAD
-- =================================

CREATE TABLE data_export_requests (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    
    status ENUM('REQUESTED', 'PROCESSING', 'READY', 'DOWNLOADED', 'EXPIRED') NOT NULL DEFAULT 'REQUESTED',
    format ENUM('JSON', 'CSV', 'ZIP') NOT NULL DEFAULT 'JSON',
    
    -- Archivos generados
    file_path VARCHAR(1000) NULL,
    file_size_bytes BIGINT NULL,
    expires_at TIMESTAMP NULL,
    
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    downloaded_at TIMESTAMP NULL,
    
    CONSTRAINT fk_export_requests_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_export_requests_user_id (user_id),
    INDEX idx_export_requests_status (status),
    INDEX idx_export_requests_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE data_deletion_requests (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    
    status ENUM('REQUESTED', 'GRACE_PERIOD', 'PROCESSING', 'COMPLETED') NOT NULL DEFAULT 'REQUESTED',
    reason TEXT,
    
    -- Fechas importantes
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    grace_period_ends_at TIMESTAMP NULL COMMENT '14 días para cancelar',
    processed_at TIMESTAMP NULL,
    
    -- Reversión durante grace period
    cancelled_at TIMESTAMP NULL,
    cancellation_reason TEXT,
    
    CONSTRAINT fk_deletion_requests_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_deletion_requests_user_id (user_id),
    INDEX idx_deletion_requests_status (status),
    INDEX idx_deletion_requests_grace_period (grace_period_ends_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- FACTURACIÓN Y PAGOS
-- =================================

CREATE TABLE subscriptions (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    
    -- Plan y precios
    plan ENUM('BASIC', 'PRO', 'TEAMS') NOT NULL,
    billing_cycle ENUM('MONTHLY', 'YEARLY') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'EUR',
    
    -- Estado
    status ENUM('ACTIVE', 'CANCELLED', 'PAST_DUE', 'UNPAID', 'INCOMPLETE') NOT NULL,
    
    -- Integración Stripe/PayPal
    stripe_subscription_id VARCHAR(100) UNIQUE,
    stripe_customer_id VARCHAR(100),
    paypal_subscription_id VARCHAR(100) UNIQUE,
    
    -- Fechas
    current_period_start TIMESTAMP NOT NULL,
    current_period_end TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP NULL,
    
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_subscriptions_user_id (user_id),
    INDEX idx_subscriptions_status (status),
    INDEX idx_subscriptions_stripe_customer (stripe_customer_id),
    INDEX idx_subscriptions_period_end (current_period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payments (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    subscription_id CHAR(36) NOT NULL,
    
    -- Detalles del pago
    amount DECIMAL(10,2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'EUR',
    status ENUM('PENDING', 'SUCCEEDED', 'FAILED', 'REFUNDED') NOT NULL,
    
    -- Proveedor
    provider ENUM('STRIPE', 'PAYPAL') NOT NULL,
    provider_payment_id VARCHAR(100) NOT NULL,
    provider_fee DECIMAL(10,2),
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    
    CONSTRAINT fk_payments_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE,
    INDEX idx_payments_subscription_id (subscription_id),
    INDEX idx_payments_status (status),
    INDEX idx_payments_provider_id (provider, provider_payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- LOGS Y AUDITORÍA
-- =================================

CREATE TABLE audit_logs (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NULL,
    
    -- Acción realizada
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id CHAR(36) NULL,
    
    -- Contexto
    ip_address VARCHAR(45),
    user_agent VARCHAR(1000),
    correlation_id CHAR(36) COMMENT 'Para trazar requests',
    
    -- Datos
    old_values JSON COMMENT 'Valores antes del cambio',
    new_values JSON COMMENT 'Valores después del cambio',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_logs_user_id (user_id),
    INDEX idx_audit_logs_action (action),
    INDEX idx_audit_logs_entity (entity_type, entity_id),
    INDEX idx_audit_logs_created_at (created_at),
    INDEX idx_audit_logs_correlation_id (correlation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- CONFIGURACIÓN DEL SISTEMA
-- =================================

CREATE TABLE system_config (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type ENUM('STRING', 'INTEGER', 'BOOLEAN', 'JSON') NOT NULL DEFAULT 'STRING',
    description TEXT,
    
    is_public BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Si se puede leer sin autenticación',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_system_config_key (config_key),
    INDEX idx_system_config_is_public (is_public)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================
-- DATOS INICIALES
-- =================================

-- Configuración inicial del sistema
INSERT INTO system_config (config_key, config_value, config_type, description, is_public) VALUES
('billing_enabled', 'false', 'BOOLEAN', 'Activar sistema de facturación', true),
('coach_market_enabled', 'false', 'BOOLEAN', 'Activar marketplace de coaches', true),
('maintenance_mode', 'false', 'BOOLEAN', 'Modo mantenimiento', true),
('beta_mode', 'true', 'BOOLEAN', 'Modo beta 90 días gratis', true),
('max_challenges_basic', '2', 'INTEGER', 'Máximo retos simultáneos plan Basic', true),
('max_validators_basic', '3', 'INTEGER', 'Máximo validadores por reto plan Basic', true),
('max_file_size_mb', '25', 'INTEGER', 'Tamaño máximo archivo en MB', true),
('allowed_file_extensions', '["jpg","jpeg","png","mp4","mov"]', 'JSON', 'Extensiones permitidas', true),
('rate_limit_general', '100', 'INTEGER', 'Rate limit general por 10 minutos', false),
('rate_limit_login', '10', 'INTEGER', 'Rate limit login por 10 minutos', false);

-- Usuario administrador inicial (password: admin123)
INSERT INTO users (id, username, email, password_hash, first_name, last_name, status, role, email_verified) VALUES
('admin-uuid-1234-5678-9012', 'admin', 'admin@impulse.app', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/lewfBvBgNYZFu7.Pe', 'Admin', 'IMPULSE', 'ACTIVE', 'SUPER_ADMIN', true);

-- Reiniciar foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- ================================
-- IMPULSE DATABASE SCHEMA - COMPLETE
-- MySQL 8.0 Production Ready
-- ================================

-- Set character set and collation
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================
-- CORE USER SYSTEM
-- ================================

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    birth_date DATE,
    profile_image_url VARCHAR(500),
    bio TEXT,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED') DEFAULT 'ACTIVE',
    role ENUM('USER', 'COACH', 'VALIDATOR', 'ADMIN') DEFAULT 'USER',
    email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP NULL,
    privacy_settings JSON,
    notification_preferences JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    login_count INT DEFAULT 0,
    
    INDEX idx_users_email (email),
    INDEX idx_users_username (username),
    INDEX idx_users_status (status),
    INDEX idx_users_role (role),
    INDEX idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- AUTHENTICATION & SECURITY
-- ================================

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_token (token),
    INDEX idx_refresh_tokens_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    device_fingerprint VARCHAR(255),
    user_agent VARCHAR(500),
    ip_address VARCHAR(45),
    location VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_sessions_user_id (user_id),
    INDEX idx_user_sessions_token (session_token),
    INDEX idx_user_sessions_active (is_active),
    INDEX idx_user_sessions_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- CHALLENGES SYSTEM
-- ================================

CREATE TABLE IF NOT EXISTS challenges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    detailed_description LONGTEXT,
    creator_id BIGINT NOT NULL,
    category ENUM('FITNESS', 'NUTRITION', 'MENTAL_HEALTH', 'PRODUCTIVITY', 'LEARNING', 'SOCIAL', 'CREATIVE', 'ENVIRONMENT', 'OTHER') NOT NULL,
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') DEFAULT 'BEGINNER',
    status ENUM('DRAFT', 'ACTIVE', 'PAUSED', 'COMPLETED', 'CANCELLED', 'ARCHIVED') DEFAULT 'DRAFT',
    type ENUM('INDIVIDUAL', 'GROUP', 'PUBLIC', 'PRIVATE') DEFAULT 'INDIVIDUAL',
    duration_days INT NOT NULL DEFAULT 30,
    max_participants INT DEFAULT NULL,
    current_participants INT DEFAULT 0,
    entry_fee DECIMAL(10,2) DEFAULT 0.00,
    prize_pool DECIMAL(10,2) DEFAULT 0.00,
    validation_method ENUM('SELF_VALIDATION', 'PEER_VALIDATION', 'EXPERT_VALIDATION', 'AUTOMATED') DEFAULT 'SELF_VALIDATION',
    validation_criteria TEXT,
    required_evidence_types JSON,
    start_date TIMESTAMP NULL,
    end_date TIMESTAMP NULL,
    registration_deadline TIMESTAMP NULL,
    image_url VARCHAR(500),
    tags JSON,
    rules TEXT,
    rewards JSON,
    is_featured BOOLEAN DEFAULT FALSE,
    is_public BOOLEAN DEFAULT TRUE,
    min_age INT DEFAULT 18,
    max_age INT DEFAULT NULL,
    location_restricted BOOLEAN DEFAULT FALSE,
    allowed_countries JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_challenges_creator_id (creator_id),
    INDEX idx_challenges_category (category),
    INDEX idx_challenges_difficulty (difficulty),
    INDEX idx_challenges_status (status),
    INDEX idx_challenges_type (type),
    INDEX idx_challenges_start_date (start_date),
    INDEX idx_challenges_end_date (end_date),
    INDEX idx_challenges_featured (is_featured),
    INDEX idx_challenges_public (is_public),
    INDEX idx_challenges_created_at (created_at),
    FULLTEXT idx_challenges_search (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS challenge_participations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    challenge_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('REGISTERED', 'ACTIVE', 'COMPLETED', 'WITHDRAWN', 'DISQUALIFIED') DEFAULT 'REGISTERED',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    progress_percentage DECIMAL(5,2) DEFAULT 0.00,
    current_streak INT DEFAULT 0,
    max_streak INT DEFAULT 0,
    total_evidences_submitted INT DEFAULT 0,
    total_evidences_validated INT DEFAULT 0,
    score DECIMAL(10,2) DEFAULT 0.00,
    ranking_position INT DEFAULT NULL,
    withdrawal_reason TEXT,
    notes TEXT,
    private_notes TEXT,
    
    FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_challenge_participation (challenge_id, user_id),
    INDEX idx_challenge_participations_challenge_id (challenge_id),
    INDEX idx_challenge_participations_user_id (user_id),
    INDEX idx_challenge_participations_status (status),
    INDEX idx_challenge_participations_progress (progress_percentage),
    INDEX idx_challenge_participations_score (score),
    INDEX idx_challenge_participations_ranking (ranking_position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- EVIDENCE & VALIDATION SYSTEM
-- ================================

CREATE TABLE IF NOT EXISTS evidences (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    challenge_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    participation_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    evidence_type ENUM('IMAGE', 'VIDEO', 'AUDIO', 'DOCUMENT', 'TEXT', 'LOCATION', 'BIOMETRIC', 'ACTIVITY_DATA') NOT NULL,
    content_url VARCHAR(500),
    thumbnail_url VARCHAR(500),
    file_size BIGINT DEFAULT NULL,
    mime_type VARCHAR(100),
    metadata JSON,
    location_data JSON,
    biometric_data JSON,
    activity_data JSON,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'UNDER_REVIEW', 'FLAGGED') DEFAULT 'PENDING',
    validation_score DECIMAL(5,2) DEFAULT NULL,
    confidence_level ENUM('LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH') DEFAULT NULL,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    validation_deadline TIMESTAMP NULL,
    is_public BOOLEAN DEFAULT TRUE,
    privacy_level ENUM('PUBLIC', 'PARTICIPANTS_ONLY', 'VALIDATORS_ONLY', 'PRIVATE') DEFAULT 'PUBLIC',
    tags JSON,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    flag_count INT DEFAULT 0,
    
    FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (participation_id) REFERENCES challenge_participations(id) ON DELETE CASCADE,
    INDEX idx_evidences_challenge_id (challenge_id),
    INDEX idx_evidences_user_id (user_id),
    INDEX idx_evidences_participation_id (participation_id),
    INDEX idx_evidences_type (evidence_type),
    INDEX idx_evidences_status (status),
    INDEX idx_evidences_submission_date (submission_date),
    INDEX idx_evidences_validation_score (validation_score),
    INDEX idx_evidences_public (is_public)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS evidence_validations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    evidence_id BIGINT NOT NULL,
    validator_id BIGINT NOT NULL,
    validation_type ENUM('PEER', 'EXPERT', 'AUTOMATED', 'COACH') NOT NULL,
    decision ENUM('APPROVE', 'REJECT', 'REQUEST_MORE_INFO', 'FLAG') NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    confidence DECIMAL(5,2) NOT NULL,
    feedback TEXT,
    criteria_met JSON,
    validation_time_seconds INT DEFAULT NULL,
    automated_flags JSON,
    reviewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_final BOOLEAN DEFAULT FALSE,
    appeal_eligible BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (evidence_id) REFERENCES evidences(id) ON DELETE CASCADE,
    FOREIGN KEY (validator_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_evidence_validator (evidence_id, validator_id),
    INDEX idx_evidence_validations_evidence_id (evidence_id),
    INDEX idx_evidence_validations_validator_id (validator_id),
    INDEX idx_evidence_validations_type (validation_type),
    INDEX idx_evidence_validations_decision (decision),
    INDEX idx_evidence_validations_score (score),
    INDEX idx_evidence_validations_reviewed_at (reviewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- VALIDATORS SYSTEM
-- ================================

CREATE TABLE IF NOT EXISTS validators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    specialty ENUM('FITNESS', 'NUTRITION', 'MENTAL_HEALTH', 'PRODUCTIVITY', 'LEARNING', 'SOCIAL', 'CREATIVE', 'ENVIRONMENT', 'GENERAL') NOT NULL,
    certification_level ENUM('BASIC', 'INTERMEDIATE', 'ADVANCED', 'EXPERT', 'MASTER') DEFAULT 'BASIC',
    status ENUM('PENDING', 'ACTIVE', 'SUSPENDED', 'INACTIVE') DEFAULT 'PENDING',
    bio TEXT,
    credentials TEXT,
    experience_years INT DEFAULT 0,
    validation_count INT DEFAULT 0,
    accuracy_rate DECIMAL(5,2) DEFAULT 0.00,
    average_response_time_hours DECIMAL(8,2) DEFAULT NULL,
    rating DECIMAL(3,2) DEFAULT 0.00,
    rating_count INT DEFAULT 0,
    specialties JSON,
    availability_schedule JSON,
    hourly_rate DECIMAL(8,2) DEFAULT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    preferred_languages JSON,
    timezone VARCHAR(50),
    certifications JSON,
    portfolio_items JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_active TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_validators_user_id (user_id),
    INDEX idx_validators_specialty (specialty),
    INDEX idx_validators_certification_level (certification_level),
    INDEX idx_validators_status (status),
    INDEX idx_validators_accuracy_rate (accuracy_rate),
    INDEX idx_validators_rating (rating),
    INDEX idx_validators_availability (last_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS validator_assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    evidence_id BIGINT NOT NULL,
    validator_id BIGINT NOT NULL,
    challenge_id BIGINT NOT NULL,
    assignment_type ENUM('AUTOMATIC', 'MANUAL', 'REQUESTED') DEFAULT 'AUTOMATIC',
    status ENUM('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED', 'DECLINED', 'EXPIRED') DEFAULT 'ASSIGNED',
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
    estimated_time_minutes INT DEFAULT NULL,
    fee_amount DECIMAL(8,2) DEFAULT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP NULL,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    deadline TIMESTAMP NULL,
    assignment_notes TEXT,
    
    FOREIGN KEY (evidence_id) REFERENCES evidences(id) ON DELETE CASCADE,
    FOREIGN KEY (validator_id) REFERENCES validators(id) ON DELETE CASCADE,
    FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    INDEX idx_validator_assignments_evidence_id (evidence_id),
    INDEX idx_validator_assignments_validator_id (validator_id),
    INDEX idx_validator_assignments_challenge_id (challenge_id),
    INDEX idx_validator_assignments_status (status),
    INDEX idx_validator_assignments_priority (priority),
    INDEX idx_validator_assignments_deadline (deadline),
    INDEX idx_validator_assignments_assigned_at (assigned_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- BILLING & SUBSCRIPTIONS
-- ================================

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plan_id VARCHAR(100) NOT NULL,
    plan_name VARCHAR(100) NOT NULL,
    status ENUM('ACTIVE', 'CANCELLED', 'PAST_DUE', 'UNPAID', 'TRIALING', 'INCOMPLETE') NOT NULL,
    stripe_subscription_id VARCHAR(255) UNIQUE,
    stripe_customer_id VARCHAR(255),
    current_period_start TIMESTAMP NOT NULL,
    current_period_end TIMESTAMP NOT NULL,
    trial_start TIMESTAMP NULL,
    trial_end TIMESTAMP NULL,
    cancel_at_period_end BOOLEAN DEFAULT FALSE,
    cancelled_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,
    billing_cycle ENUM('MONTHLY', 'YEARLY') DEFAULT 'MONTHLY',
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_subscriptions_user_id (user_id),
    INDEX idx_subscriptions_status (status),
    INDEX idx_subscriptions_stripe_subscription_id (stripe_subscription_id),
    INDEX idx_subscriptions_stripe_customer_id (stripe_customer_id),
    INDEX idx_subscriptions_period_end (current_period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT NULL,
    stripe_payment_intent_id VARCHAR(255) UNIQUE,
    stripe_charge_id VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status ENUM('PENDING', 'SUCCEEDED', 'FAILED', 'CANCELLED', 'REFUNDED', 'PARTIALLY_REFUNDED') NOT NULL,
    payment_method ENUM('CARD', 'BANK_TRANSFER', 'DIGITAL_WALLET', 'OTHER') DEFAULT 'CARD',
    description TEXT,
    metadata JSON,
    failure_reason VARCHAR(255),
    refund_amount DECIMAL(10,2) DEFAULT 0.00,
    fee_amount DECIMAL(10,2) DEFAULT 0.00,
    net_amount DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL,
    INDEX idx_payments_user_id (user_id),
    INDEX idx_payments_subscription_id (subscription_id),
    INDEX idx_payments_status (status),
    INDEX idx_payments_stripe_payment_intent_id (stripe_payment_intent_id),
    INDEX idx_payments_amount (amount),
    INDEX idx_payments_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT NULL,
    stripe_invoice_id VARCHAR(255) UNIQUE,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    status ENUM('DRAFT', 'OPEN', 'PAID', 'VOID', 'UNCOLLECTIBLE') NOT NULL,
    amount_due DECIMAL(10,2) NOT NULL,
    amount_paid DECIMAL(10,2) DEFAULT 0.00,
    amount_remaining DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    due_date TIMESTAMP NULL,
    finalized_at TIMESTAMP NULL,
    paid_at TIMESTAMP NULL,
    voided_at TIMESTAMP NULL,
    description TEXT,
    notes TEXT,
    pdf_url VARCHAR(500),
    hosted_invoice_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL,
    INDEX idx_invoices_user_id (user_id),
    INDEX idx_invoices_subscription_id (subscription_id),
    INDEX idx_invoices_status (status),
    INDEX idx_invoices_stripe_invoice_id (stripe_invoice_id),
    INDEX idx_invoices_invoice_number (invoice_number),
    INDEX idx_invoices_due_date (due_date),
    INDEX idx_invoices_period_end (period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- GAMIFICATION SYSTEM
-- ================================

CREATE TABLE IF NOT EXISTS achievements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    category ENUM('PARTICIPATION', 'COMPLETION', 'STREAK', 'VALIDATION', 'SOCIAL', 'MILESTONE', 'SPECIAL') NOT NULL,
    type ENUM('BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND') DEFAULT 'BRONZE',
    icon_url VARCHAR(500),
    badge_url VARCHAR(500),
    points_reward INT DEFAULT 0,
    criteria JSON NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_hidden BOOLEAN DEFAULT FALSE,
    rarity ENUM('COMMON', 'UNCOMMON', 'RARE', 'EPIC', 'LEGENDARY') DEFAULT 'COMMON',
    prerequisites JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_achievements_category (category),
    INDEX idx_achievements_type (type),
    INDEX idx_achievements_rarity (rarity),
    INDEX idx_achievements_active (is_active),
    INDEX idx_achievements_hidden (is_hidden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_achievements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    progress_data JSON,
    is_featured BOOLEAN DEFAULT FALSE,
    notification_sent BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_achievement (user_id, achievement_id),
    INDEX idx_user_achievements_user_id (user_id),
    INDEX idx_user_achievements_achievement_id (achievement_id),
    INDEX idx_user_achievements_earned_at (earned_at),
    INDEX idx_user_achievements_featured (is_featured)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    total_points INT DEFAULT 0,
    level INT DEFAULT 1,
    experience_points INT DEFAULT 0,
    challenges_created INT DEFAULT 0,
    challenges_joined INT DEFAULT 0,
    challenges_completed INT DEFAULT 0,
    challenges_won INT DEFAULT 0,
    evidences_submitted INT DEFAULT 0,
    evidences_validated INT DEFAULT 0,
    validations_performed INT DEFAULT 0,
    validation_accuracy_rate DECIMAL(5,2) DEFAULT 0.00,
    current_streak INT DEFAULT 0,
    longest_streak INT DEFAULT 0,
    total_earnings DECIMAL(10,2) DEFAULT 0.00,
    total_spent DECIMAL(10,2) DEFAULT 0.00,
    referrals_count INT DEFAULT 0,
    social_connections INT DEFAULT 0,
    profile_views INT DEFAULT 0,
    achievement_count INT DEFAULT 0,
    last_activity TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_statistics_total_points (total_points),
    INDEX idx_user_statistics_level (level),
    INDEX idx_user_statistics_current_streak (current_streak),
    INDEX idx_user_statistics_last_activity (last_activity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- NOTIFICATION SYSTEM
-- ================================

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type ENUM('CHALLENGE_INVITATION', 'EVIDENCE_VALIDATION', 'ACHIEVEMENT_EARNED', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'SUBSCRIPTION_RENEWAL', 'CHALLENGE_REMINDER', 'SOCIAL_INTERACTION', 'SYSTEM_ANNOUNCEMENT', 'SECURITY_ALERT') NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    action_url VARCHAR(500),
    action_data JSON,
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
    status ENUM('UNREAD', 'READ', 'ARCHIVED', 'DELETED') DEFAULT 'UNREAD',
    delivery_method ENUM('IN_APP', 'EMAIL', 'SMS', 'PUSH') DEFAULT 'IN_APP',
    scheduled_for TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    read_at TIMESTAMP NULL,
    archived_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_type (type),
    INDEX idx_notifications_status (status),
    INDEX idx_notifications_priority (priority),
    INDEX idx_notifications_scheduled_for (scheduled_for),
    INDEX idx_notifications_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- CONTENT MODERATION & REPORTING
-- ================================

CREATE TABLE IF NOT EXISTS content_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT NOT NULL,
    reported_content_type ENUM('EVIDENCE', 'CHALLENGE', 'USER_PROFILE', 'COMMENT', 'MESSAGE') NOT NULL,
    reported_content_id BIGINT NOT NULL,
    reported_user_id BIGINT NOT NULL,
    reason ENUM('INAPPROPRIATE_CONTENT', 'SPAM', 'HARASSMENT', 'FAKE_EVIDENCE', 'COPYRIGHT_VIOLATION', 'PRIVACY_VIOLATION', 'DANGEROUS_CONTENT', 'OTHER') NOT NULL,
    description TEXT,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    status ENUM('PENDING', 'UNDER_REVIEW', 'RESOLVED', 'DISMISSED', 'ESCALATED') DEFAULT 'PENDING',
    moderator_id BIGINT NULL,
    moderator_notes TEXT,
    action_taken ENUM('NO_ACTION', 'WARNING', 'CONTENT_REMOVED', 'USER_SUSPENDED', 'USER_BANNED', 'CONTENT_MODIFIED') DEFAULT 'NO_ACTION',
    resolution_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (moderator_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_content_reports_reporter_id (reporter_id),
    INDEX idx_content_reports_reported_user_id (reported_user_id),
    INDEX idx_content_reports_content_type (reported_content_type),
    INDEX idx_content_reports_reason (reason),
    INDEX idx_content_reports_status (status),
    INDEX idx_content_reports_severity (severity),
    INDEX idx_content_reports_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- AUDIT & LOGGING
-- ================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    action ENUM('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'PASSWORD_CHANGE', 'EMAIL_CHANGE', 'PAYMENT', 'VALIDATION', 'REPORT', 'ADMIN_ACTION') NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    session_id VARCHAR(255),
    additional_data JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_logs_user_id (user_id),
    INDEX idx_audit_logs_action (action),
    INDEX idx_audit_logs_entity_type (entity_type),
    INDEX idx_audit_logs_entity_id (entity_id),
    INDEX idx_audit_logs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- SYSTEM CONFIGURATION
-- ================================

CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value JSON NOT NULL,
    setting_type ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON', 'ARRAY') DEFAULT 'STRING',
    category VARCHAR(50) NOT NULL,
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    is_editable BOOLEAN DEFAULT TRUE,
    validation_rules JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT NULL,
    
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_system_settings_category (category),
    INDEX idx_system_settings_public (is_public),
    INDEX idx_system_settings_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================
-- INITIAL SYSTEM DATA
-- ================================

-- Insert default achievements
INSERT INTO achievements (name, description, category, type, points_reward, criteria, icon_url) VALUES
('First Challenge', 'Complete your first challenge', 'COMPLETION', 'BRONZE', 100, '{"challenges_completed": 1}', '/icons/first-challenge.svg'),
('Evidence Master', 'Submit 10 pieces of evidence', 'PARTICIPATION', 'SILVER', 250, '{"evidences_submitted": 10}', '/icons/evidence-master.svg'),
('Streak Warrior', 'Maintain a 7-day streak', 'STREAK', 'GOLD', 500, '{"current_streak": 7}', '/icons/streak-warrior.svg'),
('Validator', 'Validate 25 pieces of evidence', 'VALIDATION', 'SILVER', 300, '{"validations_performed": 25}', '/icons/validator.svg'),
('Challenge Creator', 'Create your first challenge', 'PARTICIPATION', 'BRONZE', 150, '{"challenges_created": 1}', '/icons/challenge-creator.svg');

-- Insert default system settings
INSERT INTO system_settings (setting_key, setting_value, setting_type, category, description, is_public) VALUES
('max_file_size_mb', '50', 'NUMBER', 'UPLOAD', 'Maximum file size for evidence uploads in MB', true),
('supported_image_formats', '["jpg", "jpeg", "png", "gif", "webp"]', 'ARRAY', 'UPLOAD', 'Supported image file formats', true),
('supported_video_formats', '["mp4", "avi", "mov", "wmv"]', 'ARRAY', 'UPLOAD', 'Supported video file formats', true),
('default_challenge_duration', '30', 'NUMBER', 'CHALLENGE', 'Default challenge duration in days', true),
('validation_timeout_hours', '72', 'NUMBER', 'VALIDATION', 'Timeout for evidence validation in hours', true),
('min_password_length', '8', 'NUMBER', 'SECURITY', 'Minimum password length', true),
('session_timeout_minutes', '1440', 'NUMBER', 'SECURITY', 'Session timeout in minutes', false),
('max_login_attempts', '5', 'NUMBER', 'SECURITY', 'Maximum login attempts before lockout', false);

-- ================================
-- END OF SCHEMA
-- ================================

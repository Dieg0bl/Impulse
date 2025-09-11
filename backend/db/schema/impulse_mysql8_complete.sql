-- IMPULSE LEAN v1 - MySQL 8 Schema (Specification Compliant)
-- Implements exact SQL structure from specification
-- UTF8MB4, UTC timezone, UUID primary keys

SET SQL_MODE = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';
SET time_zone = '+00:00';
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =======================
-- CORE USER MANAGEMENT
-- =======================

-- Users table with GDPR compliance
CREATE TABLE users (
  id CHAR(36) PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  avatar_url VARCHAR(500),
  bio TEXT,
  role ENUM('USER','VALIDATOR','MODERATOR','ADMIN','SUPER_ADMIN') NOT NULL DEFAULT 'USER',
  status ENUM('ACTIVE','SUSPENDED','BANNED','DELETED') NOT NULL DEFAULT 'ACTIVE',
  email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  email_verified_at TIMESTAMP NULL,
  two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
  two_factor_secret VARCHAR(32),
  privacy_consent BOOLEAN NOT NULL DEFAULT FALSE,
  marketing_consent BOOLEAN NOT NULL DEFAULT FALSE,
  gdpr_consent_date TIMESTAMP NULL,
  default_visibility ENUM('private','validators','public') NOT NULL DEFAULT 'private',
  last_login_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL,
  
  INDEX idx_users_email (email),
  INDEX idx_users_username (username),
  INDEX idx_users_role (role),
  INDEX idx_users_status (status),
  INDEX idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================
-- CHALLENGES SYSTEM
-- =======================

-- Categories for challenges
CREATE TABLE categories (
  id CHAR(36) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  icon VARCHAR(100),
  color VARCHAR(7), -- hex color
  sort_order INT NOT NULL DEFAULT 0,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  INDEX idx_categories_active_sort (active, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Main challenges table
CREATE TABLE challenges (
  id CHAR(36) PRIMARY KEY,
  uuid CHAR(36) UNIQUE NOT NULL,
  title VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  creator_id CHAR(36) NOT NULL,
  category_id CHAR(36),
  difficulty ENUM('LOW','MEDIUM','HIGH','EXTREME') NOT NULL DEFAULT 'MEDIUM',
  visibility ENUM('private','validators','public') NOT NULL DEFAULT 'private',
  status ENUM('DRAFT','ACTIVE','PAUSED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'DRAFT',
  evidence_types JSON NOT NULL, -- ['text', 'image', 'video', 'audio']
  min_validators INT NOT NULL DEFAULT 3,
  max_validators INT DEFAULT NULL,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP NOT NULL,
  auto_approve BOOLEAN NOT NULL DEFAULT FALSE,
  points_reward INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
  
  INDEX idx_challenges_creator (creator_id),
  INDEX idx_challenges_category (category_id),
  INDEX idx_challenges_status (status),
  INDEX idx_challenges_visibility (visibility),
  INDEX idx_challenges_dates (start_date, end_date),
  INDEX idx_challenges_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Challenge participations
CREATE TABLE participations (
  id CHAR(36) PRIMARY KEY,
  challenge_id CHAR(36) NOT NULL,
  user_id CHAR(36) NOT NULL,
  status ENUM('ACTIVE','COMPLETED','FAILED','WITHDRAWN') NOT NULL DEFAULT 'ACTIVE',
  joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMP NULL,
  progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  
  FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  
  UNIQUE KEY uk_participation (challenge_id, user_id),
  INDEX idx_participations_user (user_id),
  INDEX idx_participations_status (status),
  INDEX idx_participations_completed (completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================
-- EVIDENCE SYSTEM
-- =======================

-- Evidence submissions
CREATE TABLE evidences (
  id CHAR(36) PRIMARY KEY,
  participation_id CHAR(36) NOT NULL,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  evidence_type ENUM('text','image','video','audio','document') NOT NULL,
  content_text TEXT,
  media_url VARCHAR(500),
  media_metadata JSON, -- size, duration, etc.
  status ENUM('PENDING','APPROVED','REJECTED','FLAGGED') NOT NULL DEFAULT 'PENDING',
  validation_score DECIMAL(3,2) DEFAULT NULL, -- 0.00 to 1.00
  submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reviewed_at TIMESTAMP NULL,
  
  FOREIGN KEY (participation_id) REFERENCES participations(id) ON DELETE CASCADE,
  
  INDEX idx_evidences_participation (participation_id),
  INDEX idx_evidences_status (status),
  INDEX idx_evidences_type (evidence_type),
  INDEX idx_evidences_submitted (submitted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Validator assignments
CREATE TABLE validator_assignments (
  id CHAR(36) PRIMARY KEY,
  challenge_id CHAR(36) NOT NULL,
  validator_id CHAR(36) NOT NULL,
  assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status ENUM('ACTIVE','COMPLETED','WITHDRAWN') NOT NULL DEFAULT 'ACTIVE',
  
  FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
  FOREIGN KEY (validator_id) REFERENCES users(id) ON DELETE CASCADE,
  
  UNIQUE KEY uk_validator_assignment (challenge_id, validator_id),
  INDEX idx_validator_assignments_validator (validator_id),
  INDEX idx_validator_assignments_challenge (challenge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Evidence validations
CREATE TABLE validations (
  id CHAR(36) PRIMARY KEY,
  evidence_id CHAR(36) NOT NULL,
  validator_id CHAR(36) NOT NULL,
  decision ENUM('APPROVE','REJECT','NEEDS_MORE_INFO') NOT NULL,
  score DECIMAL(3,2), -- 0.00 to 1.00
  feedback TEXT,
  confidence_level ENUM('LOW','MEDIUM','HIGH') NOT NULL DEFAULT 'MEDIUM',
  validated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (evidence_id) REFERENCES evidences(id) ON DELETE CASCADE,
  FOREIGN KEY (validator_id) REFERENCES users(id) ON DELETE CASCADE,
  
  UNIQUE KEY uk_validation (evidence_id, validator_id),
  INDEX idx_validations_evidence (evidence_id),
  INDEX idx_validations_validator (validator_id),
  INDEX idx_validations_decision (decision)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================
-- SUBSCRIPTION & BILLING (SPECIFICATION COMPLIANT)
-- =======================

-- Subscription plans (as per specification)
CREATE TABLE subscription_plans (
  id CHAR(36) PRIMARY KEY,
  plan_id VARCHAR(50) UNIQUE NOT NULL, -- 'basic', 'pro', 'teams', 'coach'
  name VARCHAR(100) NOT NULL,
  description TEXT,
  price_monthly DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  price_yearly DECIMAL(10,2) DEFAULT NULL,
  currency CHAR(3) NOT NULL DEFAULT 'EUR',
  features JSON NOT NULL,
  limits_json JSON NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  stripe_price_id VARCHAR(100),
  stripe_price_id_yearly VARCHAR(100),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  INDEX idx_plans_active (active),
  INDEX idx_plans_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User subscriptions
CREATE TABLE subscriptions (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  plan_id CHAR(36) NOT NULL,
  stripe_subscription_id VARCHAR(100) UNIQUE,
  status ENUM('TRIAL','ACTIVE','PAST_DUE','CANCELED','INCOMPLETE','INCOMPLETE_EXPIRED','UNPAID','PAUSED') NOT NULL DEFAULT 'TRIAL',
  billing_cycle ENUM('MONTHLY','YEARLY','LIFETIME') NOT NULL DEFAULT 'MONTHLY',
  current_period_start TIMESTAMP NULL,
  current_period_end TIMESTAMP NULL,
  trial_end TIMESTAMP NULL,
  canceled_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (plan_id) REFERENCES subscription_plans(id),
  
  INDEX idx_subscriptions_user (user_id),
  INDEX idx_subscriptions_status (status),
  INDEX idx_subscriptions_stripe (stripe_subscription_id),
  INDEX idx_subscriptions_period (current_period_start, current_period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payment transactions
CREATE TABLE payments (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  subscription_id CHAR(36),
  stripe_payment_intent_id VARCHAR(100) UNIQUE,
  amount DECIMAL(10,2) NOT NULL,
  currency CHAR(3) NOT NULL DEFAULT 'EUR',
  status ENUM('PENDING','SUCCEEDED','FAILED','CANCELED','REFUNDED') NOT NULL DEFAULT 'PENDING',
  payment_method ENUM('CARD','SEPA','PAYPAL','BANK_TRANSFER','OTHER') NOT NULL DEFAULT 'CARD',
  stripe_fee DECIMAL(10,2) DEFAULT NULL,
  net_amount DECIMAL(10,2) DEFAULT NULL,
  description TEXT,
  metadata JSON,
  processed_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL,
  
  INDEX idx_payments_user (user_id),
  INDEX idx_payments_subscription (subscription_id),
  INDEX idx_payments_status (status),
  INDEX idx_payments_stripe (stripe_payment_intent_id),
  INDEX idx_payments_processed (processed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Invoices
CREATE TABLE invoices (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  subscription_id CHAR(36),
  invoice_number VARCHAR(50) UNIQUE NOT NULL,
  stripe_invoice_id VARCHAR(100) UNIQUE,
  amount_subtotal DECIMAL(10,2) NOT NULL,
  amount_tax DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  amount_total DECIMAL(10,2) NOT NULL,
  currency CHAR(3) NOT NULL DEFAULT 'EUR',
  status ENUM('DRAFT','OPEN','PAID','VOID','UNCOLLECTIBLE') NOT NULL DEFAULT 'DRAFT',
  due_date TIMESTAMP NULL,
  paid_at TIMESTAMP NULL,
  invoice_pdf_url VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL,
  
  INDEX idx_invoices_user (user_id),
  INDEX idx_invoices_subscription (subscription_id),
  INDEX idx_invoices_status (status),
  INDEX idx_invoices_due_date (due_date),
  INDEX idx_invoices_number (invoice_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================
-- COACH MARKETPLACE (SPECIFICATION COMPLIANT)
-- =======================

-- Coach profiles (as per specification Section 20)
CREATE TABLE coach (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) UNIQUE NOT NULL,
  level VARCHAR(16) NOT NULL DEFAULT 'Starter',
  score DECIMAL(5,2) NOT NULL DEFAULT 0,
  stripe_account_id VARCHAR(64),
  bio TEXT,
  specializations JSON, -- areas of expertise
  hourly_rate DECIMAL(10,2),
  currency CHAR(3) DEFAULT 'EUR',
  availability_calendar JSON,
  verification_status ENUM('PENDING','VERIFIED','REJECTED') NOT NULL DEFAULT 'PENDING',
  verified_at TIMESTAMP NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  
  INDEX idx_coach_user (user_id),
  INDEX idx_coach_level (level),
  INDEX idx_coach_score (score),
  INDEX idx_coach_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Coach statistics (weekly aggregates as per specification)
CREATE TABLE coach_stats_weekly (
  id CHAR(36) PRIMARY KEY,
  coach_id CHAR(36) NOT NULL,
  week_start DATE NOT NULL,
  metrics_json JSON NOT NULL,
  score DECIMAL(5,2) NOT NULL,
  level VARCHAR(16) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  CONSTRAINT fk_coach_stats_coach FOREIGN KEY (coach_id) REFERENCES coach(id) ON DELETE CASCADE,
  
  INDEX idx_coach_stats_weekly_coach_week (coach_id, week_start),
  INDEX idx_coach_stats_week (week_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================
-- GAMIFICATION SYSTEM
-- =======================

-- Achievement definitions
CREATE TABLE achievements (
  id CHAR(36) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  icon VARCHAR(100),
  category VARCHAR(50),
  points INT NOT NULL DEFAULT 0,
  rarity ENUM('COMMON','RARE','EPIC','LEGENDARY') NOT NULL DEFAULT 'COMMON',
  criteria_json JSON NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  INDEX idx_achievements_category (category),
  INDEX idx_achievements_rarity (rarity),
  INDEX idx_achievements_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User achievements
CREATE TABLE user_achievements (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  achievement_id CHAR(36) NOT NULL,
  earned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  progress JSON,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
  
  UNIQUE KEY uk_user_achievement (user_id, achievement_id),
  INDEX idx_user_achievements_user (user_id),
  INDEX idx_user_achievements_earned (earned_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User statistics for gamification
CREATE TABLE user_stats (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) UNIQUE NOT NULL,
  total_points INT NOT NULL DEFAULT 0,
  level INT NOT NULL DEFAULT 1,
  experience_points INT NOT NULL DEFAULT 0,
  streak_current INT NOT NULL DEFAULT 0,
  streak_longest INT NOT NULL DEFAULT 0,
  challenges_completed INT NOT NULL DEFAULT 0,
  challenges_created INT NOT NULL DEFAULT 0,
  validations_performed INT NOT NULL DEFAULT 0,
  evidence_submitted INT NOT NULL DEFAULT 0,
  last_activity_at TIMESTAMP NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  
  INDEX idx_user_stats_points (total_points),
  INDEX idx_user_stats_level (level),
  INDEX idx_user_stats_streak (streak_current)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================
-- MODERATION & COMPLIANCE
-- =======================

-- Content reports
CREATE TABLE reports (
  id CHAR(36) PRIMARY KEY,
  reporter_id CHAR(36) NOT NULL,
  content_type ENUM('CHALLENGE','EVIDENCE','USER','COMMENT') NOT NULL,
  content_id CHAR(36) NOT NULL,
  reason ENUM('SPAM','HATE_SPEECH','HARASSMENT','FALSE_INFO','COPYRIGHT','ADULT_CONTENT','OTHER') NOT NULL,
  description TEXT,
  status ENUM('PENDING','REVIEWING','RESOLVED','DISMISSED') NOT NULL DEFAULT 'PENDING',
  priority ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'MEDIUM',
  moderator_id CHAR(36),
  resolution TEXT,
  resolved_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (moderator_id) REFERENCES users(id) ON DELETE SET NULL,
  
  INDEX idx_reports_content (content_type, content_id),
  INDEX idx_reports_status (status),
  INDEX idx_reports_priority (priority),
  INDEX idx_reports_moderator (moderator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- GDPR data requests
CREATE TABLE gdpr_requests (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  request_type ENUM('EXPORT','DELETE','RECTIFY','RESTRICT') NOT NULL,
  status ENUM('PENDING','PROCESSING','COMPLETED','FAILED') NOT NULL DEFAULT 'PENDING',
  requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMP NULL,
  data_url VARCHAR(500), -- for export requests
  notes TEXT,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  
  INDEX idx_gdpr_requests_user (user_id),
  INDEX idx_gdpr_requests_type (request_type),
  INDEX idx_gdpr_requests_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================
-- AUDIT & LOGGING
-- =======================

-- Audit trail
CREATE TABLE audit_log (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36),
  action VARCHAR(100) NOT NULL,
  resource_type VARCHAR(50) NOT NULL,
  resource_id CHAR(36),
  old_values JSON,
  new_values JSON,
  ip_address VARCHAR(45),
  user_agent TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  
  INDEX idx_audit_log_user (user_id),
  INDEX idx_audit_log_action (action),
  INDEX idx_audit_log_resource (resource_type, resource_id),
  INDEX idx_audit_log_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================
-- INSERT DEFAULT DATA
-- =======================

-- Insert default subscription plans (specification compliant)
INSERT INTO subscription_plans (id, plan_id, name, description, price_monthly, price_yearly, features, limits_json) VALUES
(UUID(), 'basic', 'Basic', 'Perfecto para empezar tu viaje de crecimiento personal', 0.00, NULL, 
 JSON_ARRAY('Hasta 2 retos activos', '3 validadores por reto', 'Texto + 1 imagen', 'Privacidad estándar', 'Historial básico'),
 JSON_OBJECT('activeChallenges', 2, 'validatorsPerChallenge', 3, 'mediaSupport', 'text + 1 image', 'export', false)),

(UUID(), 'pro', 'Pro', 'Para usuarios comprometidos con su crecimiento', 12.99, 99.00,
 JSON_ARRAY('Retos ilimitados', 'Validadores ilimitados', 'Multimedia completa', 'Retos privados/equipo', 'Stats avanzadas', 'Exportación', 'Soporte prioritario'),
 JSON_OBJECT('activeChallenges', 'unlimited', 'validatorsPerChallenge', 'unlimited', 'mediaSupport', 'full multimedia', 'export', true)),

(UUID(), 'teams', 'Teams', 'Para equipos y organizaciones', 39.99, NULL,
 JSON_ARRAY('Incluye 10 miembros', 'Dashboard equipo', 'Colaborativos', 'Competiciones', 'Reportes', 'Roles y onboarding'),
 JSON_OBJECT('activeChallenges', 'unlimited', 'validatorsPerChallenge', 'unlimited', 'teamMembers', 10, 'extraMemberPrice', 4.00, 'export', true)),

(UUID(), 'coach', 'Coach', 'Marketplace y herramientas profesionales', 0.00, NULL,
 JSON_ARRAY('Marketplace personal', 'Sesiones 1-a-1', 'Marca blanca', 'Analíticas avanzadas'),
 JSON_OBJECT('activeChallenges', 'unlimited', 'marketplace', true, 'whiteLabelBranding', true, 'export', true));

-- Insert default categories
INSERT INTO categories (id, name, description, icon, color, sort_order) VALUES
(UUID(), 'Deporte', 'Retos relacionados con actividad física y deporte', 'sport', '#10B981', 1),
(UUID(), 'Educación', 'Aprendizaje y desarrollo de habilidades', 'education', '#3B82F6', 2),
(UUID(), 'Salud', 'Bienestar físico y mental', 'health', '#EF4444', 3),
(UUID(), 'Desarrollo Personal', 'Crecimiento personal y profesional', 'personal', '#8B5CF6', 4),
(UUID(), 'Creatividad', 'Arte, música y expresión creativa', 'creative', '#F59E0B', 5),
(UUID(), 'Social', 'Retos comunitarios y sociales', 'social', '#EC4899', 6);

-- Insert sample achievements
INSERT INTO achievements (id, name, description, icon, category, points, rarity, criteria_json) VALUES
(UUID(), 'Primer Paso', 'Completa tu primer reto', 'first-step', 'milestone', 100, 'COMMON', 
 JSON_OBJECT('type', 'challenges_completed', 'target', 1)),
(UUID(), 'Validador Confiable', 'Realiza 10 validaciones', 'validator', 'validation', 250, 'RARE',
 JSON_OBJECT('type', 'validations_performed', 'target', 10)),
(UUID(), 'Maestro de Retos', 'Completa 50 retos', 'master', 'milestone', 1000, 'EPIC',
 JSON_OBJECT('type', 'challenges_completed', 'target', 50));

COMMIT;

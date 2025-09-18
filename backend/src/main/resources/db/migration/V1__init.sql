/* =======================================================================
	 IMPULSE v1.0 — V1__init.sql (definitivo)
	 MySQL 8 · InnoDB · utf8mb4_unicode_ci · UTC
	 ======================================================================= */

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET time_zone = '+00:00';

CREATE SCHEMA IF NOT EXISTS impulse
	DEFAULT CHARACTER SET utf8mb4
	DEFAULT COLLATE utf8mb4_unicode_ci;
USE impulse;

/* ========================= USUARIOS & RBAC ============================ */
CREATE TABLE users (
	id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	email             VARCHAR(320) NOT NULL,
	email_normalized  VARCHAR(320) AS (LOWER(email)) STORED,
	password_hash     VARCHAR(255) NULL,
	display_name      VARCHAR(120) NOT NULL,
	status            ENUM('ACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
	last_login_at     DATETIME NULL,
	created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	deleted_at        DATETIME NULL,
	PRIMARY KEY (id),
	UNIQUE KEY uq_users_email (email),
	KEY ix_users_email_norm (email_normalized),
	KEY ix_users_status (status),
	KEY ix_users_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE roles (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	name         ENUM('ADMIN','USER','VALIDATOR','MODERATOR','SUPPORT','BILLING','COACH') NOT NULL,
	description  VARCHAR(255) NULL,
	created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE KEY uq_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
	user_id    BIGINT UNSIGNED NOT NULL,
	role_id    BIGINT UNSIGNED NOT NULL,
	granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	granted_by BIGINT UNSIGNED NULL,
	PRIMARY KEY (user_id, role_id),
	CONSTRAINT fk_user_roles_user  FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_user_roles_role  FOREIGN KEY (role_id) REFERENCES roles(id),
	CONSTRAINT fk_user_roles_admin FOREIGN KEY (granted_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/* ============ CHALLENGES / PARTICIPATIONS / VALIDATORS / EVIDENCES ===== */
CREATE TABLE challenges (
	id                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	owner_user_id          BIGINT UNSIGNED NOT NULL,
	title                  VARCHAR(200) NOT NULL,
	description            TEXT NULL,
	status                 ENUM('DRAFT','OPEN','CLOSED') NOT NULL DEFAULT 'DRAFT',
	visibility             ENUM('PRIVATE','PUBLIC','LINK') NOT NULL DEFAULT 'PRIVATE',
	category               VARCHAR(64) NOT NULL DEFAULT 'General',
	public_consent_version VARCHAR(32) NULL,
	opened_at              DATETIME NULL,
	closed_at              DATETIME NULL,
	created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	deleted_at             DATETIME NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_challenges_owner FOREIGN KEY (owner_user_id) REFERENCES users(id),
	KEY ix_challenges_owner (owner_user_id),
	KEY ix_challenges_status (status),
	KEY ix_challenges_visibility (visibility),
	KEY ix_challenges_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE participations (
	id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	challenge_id  BIGINT UNSIGNED NOT NULL,
	user_id       BIGINT UNSIGNED NOT NULL,
	status        ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
	created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	deleted_at    DATETIME NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_participations_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id),
	CONSTRAINT fk_participations_user      FOREIGN KEY (user_id) REFERENCES users(id),
	UNIQUE KEY uq_participation (challenge_id, user_id),
	KEY ix_participations_status (status),
	KEY ix_participations_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE validators (
	id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	challenge_id     BIGINT UNSIGNED NOT NULL,
	user_id          BIGINT UNSIGNED NOT NULL,
	invited_by       BIGINT UNSIGNED NULL,
	created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_validators_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id),
	CONSTRAINT fk_validators_user      FOREIGN KEY (user_id)      REFERENCES users(id),
	CONSTRAINT fk_validators_invitedby FOREIGN KEY (invited_by)   REFERENCES users(id),
	UNIQUE KEY uq_validator (challenge_id, user_id),
	KEY ix_validators_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE evidences (
	id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	challenge_id         BIGINT UNSIGNED NOT NULL,
	participation_id     BIGINT UNSIGNED NOT NULL,
	text                 TEXT NULL,
	media_key            VARCHAR(512) NULL,
	media_mime           VARCHAR(128) NULL,
	media_size_bytes     BIGINT UNSIGNED NULL,
	media_scan_status    ENUM('PENDING','SCANNING','CLEAN','INFECTED') NOT NULL DEFAULT 'PENDING',
	status               ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
	decided_by_user_id   BIGINT UNSIGNED NULL,
	decided_at           DATETIME NULL,
	decision_reason      VARCHAR(500) NULL,
	created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	deleted_at           DATETIME NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_evidences_challenge      FOREIGN KEY (challenge_id)     REFERENCES challenges(id),
	CONSTRAINT fk_evidences_participation  FOREIGN KEY (participation_id) REFERENCES participations(id),
	CONSTRAINT fk_evidences_decider        FOREIGN KEY (decided_by_user_id) REFERENCES users(id),
	CONSTRAINT chk_ev_state_decision CHECK (
		(status = 'PENDING'  AND decided_by_user_id IS NULL AND decided_at IS NULL)
		OR
		(status IN ('APPROVED','REJECTED') AND decided_by_user_id IS NOT NULL AND decided_at IS NOT NULL)
	),
	CONSTRAINT chk_ev_reject_reason CHECK (
		status <> 'REJECTED' OR (decision_reason IS NOT NULL AND CHAR_LENGTH(TRIM(decision_reason)) > 0)
	),
	KEY ix_evidences_status (status, media_scan_status),
	KEY ix_evidences_challenge (challenge_id),
	KEY ix_evidences_participation (participation_id),
	KEY ix_evidences_decided_at (decided_at),
	KEY ix_evidences_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/* ============================ MODERACIÓN / DSA ======================== */
CREATE TABLE reports (
	id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	resource_type    ENUM('USER','CHALLENGE','EVIDENCE') NOT NULL,
	resource_id      BIGINT UNSIGNED NOT NULL,
	reporter_user_id BIGINT UNSIGNED NOT NULL,
	reason           ENUM('SPAM','ABUSE','NUDITY','HATE','HARASSMENT','VIOLENCE','ILLEGAL','OTHER') NOT NULL,
	context          TEXT NULL,
	status           ENUM('PENDING','REVIEWING','RESOLVED') NOT NULL DEFAULT 'PENDING',
	created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_user_id) REFERENCES users(id),
	KEY ix_reports_resource (resource_type, resource_id),
	KEY ix_reports_status (status),
	KEY ix_reports_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE moderation_actions (
	id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	report_id        BIGINT UNSIGNED NULL,
	resource_type    ENUM('USER','CHALLENGE','EVIDENCE') NOT NULL,
	resource_id      BIGINT UNSIGNED NOT NULL,
	action           ENUM('HIDE','DELETE','WARN','BAN') NOT NULL,
	reason           VARCHAR(500) NOT NULL,
	acted_by_user_id BIGINT UNSIGNED NOT NULL,
	created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_actions_report  FOREIGN KEY (report_id) REFERENCES reports(id),
	CONSTRAINT fk_actions_actor   FOREIGN KEY (acted_by_user_id) REFERENCES users(id),
	KEY ix_actions_resource (resource_type, resource_id),
	KEY ix_actions_action (action, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE appeals (
	id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	action_id          BIGINT UNSIGNED NOT NULL,
	appellant_user_id  BIGINT UNSIGNED NOT NULL,
	status             ENUM('PENDING','ACCEPTED','REJECTED') NOT NULL DEFAULT 'PENDING',
	reason             VARCHAR(500) NOT NULL,
	decided_by_user_id BIGINT UNSIGNED NULL,
	decided_at         DATETIME NULL,
	created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_appeals_action  FOREIGN KEY (action_id) REFERENCES moderation_actions(id),
	CONSTRAINT fk_appeals_user    FOREIGN KEY (appellant_user_id) REFERENCES users(id),
	CONSTRAINT fk_appeals_decider FOREIGN KEY (decided_by_user_id) REFERENCES users(id),
	KEY ix_appeals_status (status),
	KEY ix_appeals_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/* ============================== PRIVACIDAD ============================ */
CREATE TABLE consents (
	id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id       BIGINT UNSIGNED NOT NULL,
	consent_key   ENUM('tos','privacy','marketing') NOT NULL,
	version       VARCHAR(32) NOT NULL,
	granted_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	revoked_at    DATETIME NULL,
	user_agent    VARCHAR(255) NULL,
	ip_address    VARCHAR(45) NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_consents_user FOREIGN KEY (user_id) REFERENCES users(id),
	KEY ix_consents_user_key (user_id, consent_key),
	KEY ix_consents_version (version, granted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE privacy_visibilities (
	id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	resource_type      ENUM('CHALLENGE','EVIDENCE') NOT NULL,
	resource_id        BIGINT UNSIGNED NOT NULL,
	old_visibility     ENUM('PRIVATE','PUBLIC','LINK') NOT NULL,
	new_visibility     ENUM('PRIVATE','PUBLIC','LINK') NOT NULL,
	changed_by_user_id BIGINT UNSIGNED NOT NULL,
	consent_version    VARCHAR(32) NULL,
	reason             VARCHAR(500) NULL,
	created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_vis_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id),
	CONSTRAINT chk_vis_change CHECK (old_visibility <> new_visibility),
	KEY ix_vis_resource (resource_type, resource_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dsar_requests (
	id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id       BIGINT UNSIGNED NOT NULL,
	type          ENUM('EXPORT','DELETE') NOT NULL,
	status        ENUM('PENDING','PROCESSING','DONE','REJECTED') NOT NULL DEFAULT 'PENDING',
	artifact_url  VARCHAR(512) NULL,
	reason        VARCHAR(500) NULL,
	created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	closed_at     DATETIME NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_dsar_user FOREIGN KEY (user_id) REFERENCES users(id),
	KEY ix_dsar_status (status),
	KEY ix_dsar_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/* ========================== TELEMETRÍA & ENCUESTAS ==================== */
CREATE TABLE events (
	id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id         BIGINT UNSIGNED NULL,
	event_key       VARCHAR(64) NOT NULL,
	correlation_id  VARCHAR(64) NULL,
	properties      JSON NULL,
	created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_events_user FOREIGN KEY (user_id) REFERENCES users(id),
	KEY ix_events_key_time (event_key, created_at),
	KEY ix_events_user_time (user_id, created_at),
	KEY ix_events_corr (correlation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE surveys (
	id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	survey_key  VARCHAR(64) NOT NULL,
	is_active   BOOLEAN NOT NULL DEFAULT TRUE,
	created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE KEY uq_surveys_key (survey_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE survey_answers (
	id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	survey_id   BIGINT UNSIGNED NOT NULL,
	user_id     BIGINT UNSIGNED NULL,
	rating      TINYINT UNSIGNED NULL,
	comment     VARCHAR(1000) NULL,
	created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_sanswers_survey FOREIGN KEY (survey_id) REFERENCES surveys(id),
	CONSTRAINT fk_sanswers_user   FOREIGN KEY (user_id)  REFERENCES users(id),
	CONSTRAINT chk_nps_range CHECK (rating IS NULL OR (rating BETWEEN 0 AND 10)),
	KEY ix_sanswers_survey_time (survey_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/* ============================= STRIPE BILLING ========================= */
CREATE TABLE plans (
	id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	code               VARCHAR(64) NOT NULL,
	stripe_product_id  VARCHAR(64) NOT NULL,
	stripe_price_id    VARCHAR(64) NOT NULL,
	price_cents        INT UNSIGNED NOT NULL,
	currency           CHAR(3) NOT NULL DEFAULT 'EUR',
	features           JSON NULL,
	is_active          BOOLEAN NOT NULL DEFAULT TRUE,
	created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE KEY uq_plans_code (code),
	UNIQUE KEY uq_plans_price (stripe_price_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE subscriptions (
	id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id                 BIGINT UNSIGNED NOT NULL,
	plan_id                 BIGINT UNSIGNED NOT NULL,
	stripe_customer_id      VARCHAR(64) NOT NULL,
	stripe_subscription_id  VARCHAR(64) NOT NULL,
	status                  ENUM('active','canceled','incomplete','incomplete_expired','past_due','trialing','unpaid') NOT NULL,
	current_period_end      DATETIME NULL,
	cancel_at               DATETIME NULL,
	canceled_at             DATETIME NULL,
	created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_sub_plan FOREIGN KEY (plan_id) REFERENCES plans(id),
	UNIQUE KEY uq_sub_stripe (stripe_subscription_id),
	KEY ix_sub_user_status (user_id, status),
	KEY ix_sub_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payments (
	id                       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id                  BIGINT UNSIGNED NOT NULL,
	subscription_id          BIGINT UNSIGNED NULL,
	stripe_payment_intent_id VARCHAR(64) NOT NULL,
	amount_cents             INT UNSIGNED NOT NULL,
	currency                 CHAR(3) NOT NULL DEFAULT 'EUR',
	status                   ENUM('succeeded','failed','requires_action','processing','canceled') NOT NULL,
	receipt_url              VARCHAR(512) NULL,
	created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_pay_user  FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_pay_sub   FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
	UNIQUE KEY uq_pay_pi (stripe_payment_intent_id),
	KEY ix_pay_status_time (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE invoices (
	id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id               BIGINT UNSIGNED NOT NULL,
	subscription_id       BIGINT UNSIGNED NULL,
	stripe_invoice_id     VARCHAR(64) NOT NULL,
	status                ENUM('draft','open','paid','void','uncollectible') NOT NULL,
	amount_due_cents      INT UNSIGNED NOT NULL,
	hosted_invoice_url    VARCHAR(512) NULL,
	pdf_url               VARCHAR(512) NULL,
	created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	paid_at               DATETIME NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_inv_user FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_inv_sub  FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
	UNIQUE KEY uq_inv_stripe (stripe_invoice_id),
	KEY ix_inv_status_time (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refunds (
	id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	payment_id        BIGINT UNSIGNED NOT NULL,
	stripe_refund_id  VARCHAR(64) NOT NULL,
	amount_cents      INT UNSIGNED NOT NULL,
	status            ENUM('pending','succeeded','failed','canceled') NOT NULL,
	reason            VARCHAR(255) NULL,
	created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_ref_payment FOREIGN KEY (payment_id) REFERENCES payments(id),
	UNIQUE KEY uq_ref_stripe (stripe_refund_id),
	KEY ix_ref_status_time (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dunning_attempts (
	id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	subscription_id   BIGINT UNSIGNED NOT NULL,
	attempt_number    INT UNSIGNED NOT NULL,
	outcome           ENUM('success','failed') NOT NULL,
	error_code        VARCHAR(64) NULL,
	error_message     VARCHAR(255) NULL,
	created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_dunning_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
	CONSTRAINT chk_dunning_attempt CHECK (attempt_number >= 1),
	KEY ix_dunning_sub (subscription_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE webhook_events (
	id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	provider             ENUM('stripe') NOT NULL DEFAULT 'stripe',
	event_id             VARCHAR(100) NOT NULL,
	signature_verified   BOOLEAN NOT NULL DEFAULT FALSE,
	payload_hash         CHAR(64) NOT NULL,
	payload              JSON NOT NULL,
	processed_at         DATETIME NULL,
	received_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE KEY uq_webhook_event (event_id),
	KEY ix_webhook_processed (processed_at),
	KEY ix_webhook_received (received_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/* ============================== REFERIDOS ============================= */
CREATE TABLE referral_codes (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id      BIGINT UNSIGNED NOT NULL,
	code         VARCHAR(32) NOT NULL,
	created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_rc_user FOREIGN KEY (user_id) REFERENCES users(id),
	UNIQUE KEY uq_rc_code (code),
	UNIQUE KEY uq_rc_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE referrals (
	id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	referrer_user_id    BIGINT UNSIGNED NOT NULL,
	referred_user_id    BIGINT UNSIGNED NOT NULL,
	referral_code       VARCHAR(32) NOT NULL,
	ip_address          VARCHAR(45) NULL,
	device_fingerprint  VARCHAR(100) NULL,
	created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_referrals_referrer FOREIGN KEY (referrer_user_id) REFERENCES users(id),
	CONSTRAINT fk_referrals_referred FOREIGN KEY (referred_user_id) REFERENCES users(id),
	CONSTRAINT fk_referrals_code     FOREIGN KEY (referral_code) REFERENCES referral_codes(code),
	UNIQUE KEY uq_referrals_referred (referred_user_id),
	KEY ix_referrals_referrer (referrer_user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE referral_rewards (
	id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	referrer_user_id   BIGINT UNSIGNED NOT NULL,
	referred_user_id   BIGINT UNSIGNED NOT NULL,
	amount_cents       INT UNSIGNED NOT NULL DEFAULT 0,
	currency           CHAR(3) NOT NULL DEFAULT 'EUR',
	status             ENUM('PENDING','EARNED','PAID','CANCELED') NOT NULL DEFAULT 'PENDING',
	reason             VARCHAR(255) NULL,
	created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	paid_at            DATETIME NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_rewards_referrer FOREIGN KEY (referrer_user_id) REFERENCES users(id),
	CONSTRAINT fk_rewards_referred FOREIGN KEY (referred_user_id) REFERENCES users(id),
	KEY ix_rewards_status (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/* ============ AUDITORÍA / IP BLOCKLIST / IDEMPOTENCIA / JOBS ========== */
CREATE TABLE audit_log (
	id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	actor_user_id   BIGINT UNSIGNED NULL,
	action          VARCHAR(64) NOT NULL,
	resource_type   VARCHAR(32) NOT NULL,
	resource_id     VARCHAR(64) NOT NULL,
	reason          VARCHAR(500) NULL,
	metadata        JSON NULL,
	correlation_id  VARCHAR(64) NULL,
	created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_audit_actor FOREIGN KEY (actor_user_id) REFERENCES users(id),
	KEY ix_audit_resource (resource_type, resource_id, created_at),
	KEY ix_audit_corr (correlation_id),
	KEY ix_audit_action (action, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ip_blocklist (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	ip_address   VARCHAR(45) NOT NULL,
	reason       VARCHAR(255) NULL,
	created_by   BIGINT UNSIGNED NULL,
	created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	expires_at   DATETIME NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_ipb_creator FOREIGN KEY (created_by) REFERENCES users(id),
	UNIQUE KEY uq_ipb_ip (ip_address),
	KEY ix_ipb_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE idempotency_keys (
	id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	key_hash              BINARY(32) NOT NULL,
	request_fingerprint   CHAR(64) NOT NULL,
	correlation_id        VARCHAR(64) NULL,
	first_seen_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	locked_until          DATETIME NULL,
	response_status       INT NULL,
	response_body         MEDIUMTEXT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY uq_idem_key (key_hash),
	KEY ix_idem_seen (first_seen_at),
	KEY ix_idem_locked (locked_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE job_definitions (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	name         VARCHAR(100) NOT NULL,
	cron_expr    VARCHAR(64) NOT NULL,
	owner        VARCHAR(100) NOT NULL,
	is_active    BOOLEAN NOT NULL DEFAULT TRUE,
	created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE KEY uq_jobdef_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE job_runs (
	id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	job_definition_id BIGINT UNSIGNED NOT NULL,
	status            ENUM('SCHEDULED','RUNNING','SUCCESS','FAILED') NOT NULL,
	started_at        DATETIME NULL,
	finished_at       DATETIME NULL,
	result            JSON NULL,
	error_message     VARCHAR(500) NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_jobruns_def FOREIGN KEY (job_definition_id) REFERENCES job_definitions(id),
	KEY ix_jobruns_status_time (status, started_at, finished_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/* ============================== SEMILLAS ============================== */
INSERT INTO roles (name, description) VALUES
	('ADMIN','Administrador'),
	('USER','Usuario'),
	('VALIDATOR','Validador'),
	('MODERATOR','Moderador DSA'),
	('SUPPORT','Soporte'),
	('BILLING','Operaciones de cobro'),
	('COACH','Reservado (flag)')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO plans (code, stripe_product_id, stripe_price_id, price_cents, currency, features, is_active)
VALUES ('basic', 'prod_BASIC_PLACEHOLDER', 'price_BASIC_PLACEHOLDER', 0, 'EUR', JSON_OBJECT('limits','default'), TRUE)
ON DUPLICATE KEY UPDATE code=code;

/* ============================== FIN =================================== */
SELECT 'OK – Esquema listo' AS status;

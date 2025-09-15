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
-- ...continúa el resto del archivo original...
-- ...contenido original del archivo impulse_database.sql...

-- DAC7 marketplace / seller onboarding tables
CREATE TABLE IF NOT EXISTS platform_seller (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  legal_name VARCHAR(255) NOT NULL,
  tax_id VARCHAR(64),
  country CHAR(2),
  address VARCHAR(255),
  kyc_status ENUM('pending','verified','failed') NOT NULL DEFAULT 'pending',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uniq_seller_user (user_id)
);

CREATE TABLE IF NOT EXISTS tax_forms (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  seller_id BIGINT NOT NULL,
  form_type VARCHAR(64) NOT NULL,
  year INT NOT NULL,
  data JSON,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uniq_seller_year_form (seller_id, year, form_type)
);

CREATE TABLE IF NOT EXISTS payout_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  seller_id BIGINT NOT NULL,
  amount_cents BIGINT NOT NULL,
  currency CHAR(3) NOT NULL,
  psp_transfer_id VARCHAR(128),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

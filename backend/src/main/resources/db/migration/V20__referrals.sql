-- Phase 4 referrals / viral growth
CREATE TABLE IF NOT EXISTS referral_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    code VARCHAR(32) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS referral_uses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code_id BIGINT NOT NULL,
    referred_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_referral_code FOREIGN KEY (code_id) REFERENCES referral_codes(id)
);

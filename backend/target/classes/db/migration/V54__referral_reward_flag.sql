-- Phase 7: Referral reward idempotency (first successful use triggers reward once)
ALTER TABLE referral_codes
    ADD COLUMN IF NOT EXISTS reward_released BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS reward_released_at TIMESTAMP NULL;

-- Index to query unreleased rewards quickly (optional micro-optimization)
CREATE INDEX IF NOT EXISTS idx_referral_codes_reward ON referral_codes(reward_released);

-- Add conversion timestamp and index for stats
ALTER TABLE invites ADD COLUMN IF NOT EXISTS accepted_at TIMESTAMP NULL;
CREATE INDEX IF NOT EXISTS idx_invites_referrer ON invites(referrer_id);

-- Moderation enhancements: admin_user_id, indexes
ALTER TABLE moderation_action ADD COLUMN IF NOT EXISTS admin_user_id BIGINT NULL AFTER notified_user_id;
CREATE INDEX IF NOT EXISTS idx_mod_report_status ON moderation_report(status);
CREATE INDEX IF NOT EXISTS idx_mod_report_target ON moderation_report(target_content_id);
CREATE INDEX IF NOT EXISTS idx_mod_report_reporter ON moderation_report(reporter_user_id);

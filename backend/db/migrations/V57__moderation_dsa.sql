-- DSA moderation core tables
CREATE TABLE IF NOT EXISTS moderation_report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reporter_user_id BIGINT NULL,
  target_content_id BIGINT NOT NULL,
  target_content_type ENUM('challenge','evidence','comment','profile') NOT NULL,
  reason ENUM('copyright','privacy','intimate','harassment','hate','other') NOT NULL,
  description TEXT,
  url VARCHAR(512),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status ENUM('received','triaged','actioned','dismissed') NOT NULL DEFAULT 'received'
);

CREATE TABLE IF NOT EXISTS moderation_action (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_id BIGINT NOT NULL,
  action ENUM('remove','restrict','suspend','no_action') NOT NULL,
  reason_code VARCHAR(64) NOT NULL,
  statement TEXT NOT NULL,
  notified_user_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_mod_action_report (report_id)
);

CREATE TABLE IF NOT EXISTS moderation_appeal (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  action_id BIGINT NOT NULL,
  appellant_user_id BIGINT NOT NULL,
  message TEXT NOT NULL,
  status ENUM('received','upheld','reversed','partial') NOT NULL DEFAULT 'received',
  decided_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_mod_appeal_action (action_id)
);

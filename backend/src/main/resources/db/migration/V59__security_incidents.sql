-- Security incidents & breach assessments
CREATE TABLE IF NOT EXISTS security_incident (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  detected_at TIMESTAMP NOT NULL,
  severity ENUM('low','medium','high') NOT NULL,
  description TEXT,
  scope TEXT,
  contained_at TIMESTAMP NULL,
  root_cause TEXT,
  status ENUM('open','closed') NOT NULL DEFAULT 'open'
);

CREATE TABLE IF NOT EXISTS breach_assessment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  incident_id BIGINT NOT NULL,
  risk_level ENUM('none','low','medium','high') NOT NULL,
  dpia_impact TEXT,
  notify_authority BOOLEAN NOT NULL DEFAULT 0,
  notify_subjects BOOLEAN NOT NULL DEFAULT 0,
  assessed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_breach_incident (incident_id)
);

CREATE TABLE IF NOT EXISTS breach_notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  incident_id BIGINT NOT NULL,
  channel ENUM('authority','email','in_app','other') NOT NULL,
  sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  content TEXT,
  INDEX idx_notification_incident (incident_id)
);

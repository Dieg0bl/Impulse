CREATE TABLE IF NOT EXISTS events (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NULL,
  event_type VARCHAR(100) NOT NULL,
  properties JSON NULL,
  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  session_id VARCHAR(100) NULL,
  source VARCHAR(50) NULL,
  INDEX idx_events_user_type (user_id, event_type),
  INDEX idx_events_ts (timestamp)
);

CREATE TABLE IF NOT EXISTS surveys (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  survey_type VARCHAR(40) NOT NULL,
  answers_json JSON NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_surveys_user (user_id)
);

CREATE TABLE IF NOT EXISTS user_aha_cache (
  user_id BIGINT PRIMARY KEY,
  first_aha_at TIMESTAMP NULL,
  t2v_seconds INT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

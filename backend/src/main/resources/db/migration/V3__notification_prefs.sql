CREATE TABLE IF NOT EXISTS notification_prefs (
  user_id BIGINT PRIMARY KEY,
  allow_email BOOLEAN DEFAULT TRUE,
  allow_push BOOLEAN DEFAULT TRUE,
  max_per_day INT DEFAULT 2,
  max_per_week INT DEFAULT 6,
  quiet_hours_start TIME DEFAULT '20:00:00',
  quiet_hours_end TIME DEFAULT '09:00:00',
  tz VARCHAR(40) DEFAULT 'Europe/Madrid',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  channel VARCHAR(20) NOT NULL,
  type VARCHAR(50) NOT NULL,
  sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_notif_user_day (user_id, sent_at),
  INDEX idx_notif_type (type)
);

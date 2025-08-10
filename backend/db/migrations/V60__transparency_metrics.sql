-- AMAR transparency metrics
CREATE TABLE IF NOT EXISTS amar_metrics (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  period_start DATE NOT NULL,
  period_end DATE NOT NULL,
  eu_monthly_active_recipients BIGINT NOT NULL,
  methodology TEXT,
  published_at TIMESTAMP NULL,
  UNIQUE KEY uniq_period (period_start, period_end)
);

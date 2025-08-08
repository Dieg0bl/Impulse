-- Phase 8: Product Metrics & Cohorts
CREATE TABLE IF NOT EXISTS daily_metrics (
  metric_date DATE NOT NULL,
  dau INT NOT NULL DEFAULT 0,
  wau INT NOT NULL DEFAULT 0,
  mau INT NOT NULL DEFAULT 0,
  d1_retention DECIMAL(5,2) DEFAULT NULL,
  d7_retention DECIMAL(5,2) DEFAULT NULL,
  d30_retention DECIMAL(5,2) DEFAULT NULL,
  new_users INT NOT NULL DEFAULT 0,
  aha_users INT NOT NULL DEFAULT 0,
  PRIMARY KEY(metric_date)
);

CREATE TABLE IF NOT EXISTS cohort_users (
  user_id BIGINT PRIMARY KEY,
  cohort_date DATE NOT NULL,
  first_activity_date DATE NULL
);
CREATE INDEX IF NOT EXISTS idx_cohort_date ON cohort_users(cohort_date);

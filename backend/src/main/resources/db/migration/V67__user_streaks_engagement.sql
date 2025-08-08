-- Phase 7: Retention & Engagement - user streaks
CREATE TABLE IF NOT EXISTS user_streaks (
  user_id BIGINT PRIMARY KEY,
  current_streak INT NOT NULL DEFAULT 0,
  longest_streak INT NOT NULL DEFAULT 0,
  last_activity_date DATE NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_user_streaks_current ON user_streaks(current_streak);

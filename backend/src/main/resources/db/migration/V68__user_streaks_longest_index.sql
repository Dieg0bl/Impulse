-- Phase 7 optional optimization
CREATE INDEX IF NOT EXISTS idx_user_streaks_longest ON user_streaks(longest_streak);

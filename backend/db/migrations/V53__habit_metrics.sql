-- Habit metrics table for streak & last action tracking
CREATE TABLE IF NOT EXISTS habit_metrics (
    user_id BIGINT PRIMARY KEY,
    last_action_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    streak_days INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_habit_metrics_updated ON habit_metrics(updated_at);

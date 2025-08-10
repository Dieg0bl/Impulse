-- Fase 4 Onboarding impecable: tracking de pasos y T2V (time to value)
CREATE TABLE IF NOT EXISTS onboarding_steps (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  step_key VARCHAR(50) NOT NULL,
  step_order INT NOT NULL,
  started_at TIMESTAMP NULL,
  completed_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uniq_user_step (user_id, step_key),
  INDEX idx_user_order (user_id, step_order)
);

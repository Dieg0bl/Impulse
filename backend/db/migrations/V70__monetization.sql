-- Monetization core schema
CREATE TABLE plans (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(120) NOT NULL,
  price_cents INT NOT NULL DEFAULT 0,
  currency VARCHAR(10) NOT NULL DEFAULT 'USD',
  max_retos INT NOT NULL DEFAULT 3,
  max_validadores INT NOT NULL DEFAULT 3,
  max_invites_per_day INT NOT NULL DEFAULT 10,
  features_json TEXT,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_subscriptions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  plan_id BIGINT NOT NULL,
  status VARCHAR(30) NOT NULL,
  period_start TIMESTAMP NOT NULL,
  period_end TIMESTAMP NOT NULL,
  cancel_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  CONSTRAINT fk_subscription_plan FOREIGN KEY (plan_id) REFERENCES plans(id),
  INDEX idx_user_active (user_id, status, period_end)
);

-- Seed base plans
INSERT INTO plans (code,name,price_cents,currency,max_retos,max_validadores,max_invites_per_day,features_json) VALUES
 ('FREE','Free',0,'USD',3,3,10,'{"streaks":true,"invites":true,"support":"community"}'),
 ('PRO','Pro',990,'USD',50,10,200,'{"streaks":true,"invites":true,"advanced_metrics":true,"priority_support":true}');

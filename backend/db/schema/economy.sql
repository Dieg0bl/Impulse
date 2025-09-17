-- IMPULSE: Sistema de Economía
-- Todas las entidades y reglas reflejadas 1:1 con el frontend

-- Tipos de moneda
CREATE TABLE currency (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    type VARCHAR(16) CHECK (type IN ('soft', 'hard', 'reputation', 'service')),
    transferable BOOLEAN NOT NULL,
    purchasable BOOLEAN NOT NULL,
    description TEXT
);

-- Saldos de usuario por moneda
CREATE TABLE currency_balance (
    user_id VARCHAR(64) NOT NULL,
    currency_id VARCHAR(32) NOT NULL,
    amount NUMERIC(16,2) NOT NULL DEFAULT 0,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, currency_id),
    FOREIGN KEY (currency_id) REFERENCES currency(id)
);

-- Reglas para ganar moneda
CREATE TABLE earning_rule (
    id VARCHAR(64) PRIMARY KEY,
    action VARCHAR(64) NOT NULL,
    currency_id VARCHAR(32) NOT NULL,
    amount NUMERIC(16,2) NOT NULL,
    max_per_day INT,
    max_per_week INT,
    conditions TEXT,
    FOREIGN KEY (currency_id) REFERENCES currency(id)
);

-- Ítems para gastar moneda
CREATE TABLE spending_item (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    category VARCHAR(32) CHECK (category IN ('sla', 'storage', 'templates', 'cosmetics', 'programs')),
    currency_id VARCHAR(32) NOT NULL,
    cost NUMERIC(16,2) NOT NULL,
    description TEXT,
    real_cost NUMERIC(16,2),
    max_purchases_per_month INT,
    FOREIGN KEY (currency_id) REFERENCES currency(id)
);

-- Parámetros de control económico (solo 1 fila, config global)
CREATE TABLE economic_guardrails (
    id SERIAL PRIMARY KEY,
    monthly_rewards_pool NUMERIC(16,2) NOT NULL,
    max_rewards_percentage NUMERIC(5,4) NOT NULL,
    max_benefit_percentage NUMERIC(5,4) NOT NULL,
    user_monthly_limit NUMERIC(16,2) NOT NULL,
    user_daily_threshold NUMERIC(16,2) NOT NULL,
    user_impulse_protection INT NOT NULL
);

-- Transacciones de moneda (trazabilidad completa)
CREATE TABLE currency_transaction (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    type VARCHAR(16) CHECK (type IN ('earn', 'spend', 'stipend', 'purchase')),
    currency_id VARCHAR(32) NOT NULL,
    amount NUMERIC(16,2) NOT NULL,
    reason VARCHAR(128) NOT NULL,
    source_id VARCHAR(64),
    metadata JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    balance_before NUMERIC(16,2) NOT NULL,
    balance_after NUMERIC(16,2) NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES currency(id)
);

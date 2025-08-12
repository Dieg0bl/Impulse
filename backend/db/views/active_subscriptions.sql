-- =============================================================
-- Vista: active_subscriptions
-- Descripción: Suscripciones activas o en trial
-- Cumple: Queries simples, enforcement
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
CREATE OR REPLACE VIEW active_subscriptions AS
SELECT *
FROM subscriptions
WHERE status IN ('trial','active') AND current_period_end >= NOW(3);

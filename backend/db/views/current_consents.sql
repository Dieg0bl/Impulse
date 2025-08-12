-- =============================================================
-- Vista: current_consents
-- Descripción: Último consentimiento por usuario y scope
-- Cumple: GDPR, enforcement, queries simples
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
CREATE OR REPLACE VIEW current_consents AS
SELECT c.*
FROM consents c
JOIN (
  SELECT user_id, scope, MAX(created_at) AS max_created
  FROM consents GROUP BY user_id, scope
) lastc ON lastc.user_id=c.user_id AND lastc.scope=c.scope AND lastc.max_created=c.created_at;

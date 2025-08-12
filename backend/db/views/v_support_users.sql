-- =============================================================
-- Vista: v_support_users
-- Descripción: Usuarios con email enmascarado para soporte
-- Cumple: Privacidad, soporte, solo-lectura
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
CREATE OR REPLACE VIEW v_support_users AS
SELECT id,
       CONCAT(LEFT(email,2),'***@',SUBSTRING_INDEX(email,'@',-1)) AS email_masked,
       nombre, apellidos, estado, created_at
FROM Usuario;

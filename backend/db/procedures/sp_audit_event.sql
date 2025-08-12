-- =============================================================
-- Procedimiento: sp_audit_event
-- Descripción: Inserta un evento de auditoría con hash
-- Cumple: Auditoría, integridad, trazabilidad
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_audit_event $$
CREATE PROCEDURE sp_audit_event(
  IN p_user_id BIGINT,
  IN p_event_type VARCHAR(64),
  IN p_payload JSON
)
BEGIN
  DECLARE v_hash CHAR(64);
  SET v_hash = SHA2(CONCAT(p_user_id, p_event_type, p_payload, NOW(3)), 256);
  INSERT INTO audit_event(user_id, event_type, payload, event_hash, created_at)
  VALUES (p_user_id, p_event_type, p_payload, v_hash, NOW(3));
END $$
DELIMITER ;

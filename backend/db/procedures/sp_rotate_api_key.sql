-- =============================================================
-- Procedimiento: sp_rotate_api_key
-- Descripción: Rota la API key de un usuario y registra el evento
-- Cumple: Seguridad, rotación, trazabilidad
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_rotate_api_key $$
CREATE PROCEDURE sp_rotate_api_key(IN p_user_id BIGINT)
BEGIN
  DECLARE v_new_key CHAR(64);
  SET v_new_key = SHA2(CONCAT(UUID(), RAND(), NOW(3)), 256);
  UPDATE Usuario SET api_key = v_new_key, api_key_rotated_at = NOW(3) WHERE id = p_user_id;
  CALL sp_audit_event(p_user_id, 'api_key.rotated', JSON_OBJECT('new_key', v_new_key));
END $$
DELIMITER ;

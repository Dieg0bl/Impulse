-- =============================================================
-- Procedimiento: sp_user_gdpr_erasure
-- Descripción: Anonimiza y elimina PII de usuario (GDPR)
-- Cumple: GDPR, compliance, atomicidad, auditabilidad
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_user_gdpr_erasure $$
CREATE PROCEDURE sp_user_gdpr_erasure(IN p_user BIGINT, IN p_actor VARCHAR(128))
BEGIN
  START TRANSACTION;
  UPDATE Usuario
     SET estado='ELIMINADO',
         deleted_at = NOW(3),
         email = CONCAT('user_', id, '@deleted.local'),
         email_cipher = NULL,
         email_hash = SHA2(CONCAT('tombstone:', id), 256),
         phone = NULL, phone_cipher = NULL, phone_hash = NULL,
         nombre = 'Usuario', apellidos = CONCAT('Eliminado_', id),
         photo_url = NULL, marketing_opt_in = 0
   WHERE id = p_user;

  INSERT INTO audit_event(event_type, category, actor_user_id, entity_type, entity_id, metadata)
       VALUES ('gdpr_erasure','privacy', NULL, 'user', p_user,
               JSON_OBJECT('by', p_actor));
  COMMIT;
END $$
DELIMITER ;

-- =============================================================
-- Evento: ev_purge_soft_deleted
-- Descripción: Purga registros marcados como eliminados lógicamente
-- Cumple: Retención, limpieza, cumplimiento GDPR/ENS
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP EVENT IF EXISTS ev_purge_soft_deleted $$
CREATE EVENT ev_purge_soft_deleted
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 DAY
DO
BEGIN
  DELETE FROM Evidencia WHERE deleted=1 AND deleted_at < NOW() - INTERVAL 30 DAY;
  DELETE FROM Usuario WHERE deleted=1 AND deleted_at < NOW() - INTERVAL 90 DAY;
END $$
DELIMITER ;

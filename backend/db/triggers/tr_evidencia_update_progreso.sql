-- =============================================================
-- Trigger: tr_evidencia_update_progreso
-- Descripción: Recalcula progreso al actualizar estado de Evidencia
-- Cumple: Consistencia, atomicidad, compliance
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP TRIGGER IF EXISTS tr_evidencia_update_progreso $$
CREATE TRIGGER tr_evidencia_update_progreso
AFTER UPDATE ON Evidencia
FOR EACH ROW
BEGIN
  IF NEW.estadoValidacion <> OLD.estadoValidacion THEN
    CALL sp_recalc_progreso_usuario_reto(NEW.idUsuario, NEW.idReto);
  END IF;
END $$
DELIMITER ;

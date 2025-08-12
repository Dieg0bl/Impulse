-- =============================================================
-- Trigger: evidencia_accept_metrics
-- Descripción: Actualiza métricas de racha/hábito al aceptar evidencia
-- Cumple: Métricas, streaks, atomicidad
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP TRIGGER IF EXISTS evidencia_accept_metrics $$
CREATE TRIGGER evidencia_accept_metrics
AFTER UPDATE ON Evidencia
FOR EACH ROW
BEGIN
  IF NEW.estadoValidacion='APROBADA' AND OLD.estadoValidacion<> 'APROBADA' THEN
    CALL sp_habit_on_accept(NEW.idUsuario, DATE(NEW.fechaValidacion));
  END IF;
END $$
DELIMITER ;

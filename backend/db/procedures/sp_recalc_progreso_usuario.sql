-- =============================================================
-- Procedimiento: sp_recalc_progreso_usuario_reto
-- Descripción: Recalcula el progreso de un usuario en un reto (por INSERT/UPDATE de Evidencia)
-- Cumple: Atomicidad, idempotencia, compliance, uso por triggers
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_recalc_progreso_usuario_reto $$
CREATE PROCEDURE sp_recalc_progreso_usuario_reto(IN p_user BIGINT, IN p_reto BIGINT)
BEGIN
  DECLARE v_total_dias INT DEFAULT 0;
  DECLARE v_aprobadas INT DEFAULT 0;
  DECLARE v_progreso DECIMAL(5,2) DEFAULT 0;

  SELECT DATEDIFF(fechaFin, fechaInicio) + 1 INTO v_total_dias FROM Reto WHERE id = p_reto;
  SELECT COUNT(*) INTO v_aprobadas
    FROM Evidencia WHERE idReto = p_reto AND idUsuario = p_user AND estadoValidacion = 'APROBADA';

  IF v_total_dias > 0 THEN
    SET v_progreso = LEAST(100, (v_aprobadas * 100.0) / v_total_dias);
    UPDATE ParticipacionReto
      SET progreso = v_progreso,
          fechaCompletado = CASE WHEN v_progreso >= 100 THEN NOW(3) ELSE NULL END,
          estado = CASE WHEN v_progreso >= 100 THEN 'COMPLETADO' ELSE estado END
    WHERE idReto = p_reto AND idUsuario = p_user;
  END IF;
END $$
DELIMITER ;

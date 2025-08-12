-- =============================================================
-- Procedimiento: sp_habit_on_accept
-- Descripción: Actualiza métricas de racha y hábitos al aceptar evidencia
-- Cumple: Métricas, streaks, atomicidad
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_habit_on_accept $$
CREATE PROCEDURE sp_habit_on_accept(IN p_user BIGINT, IN p_day DATE)
BEGIN
  INSERT INTO habit_metrics_daily(user_id, day, submissions, accepted)
  VALUES(p_user, p_day, 0, 1)
  ON DUPLICATE KEY UPDATE accepted = accepted + 1;

  DECLARE v_yesterday DATE; SET v_yesterday = p_day - INTERVAL 1 DAY;
  DECLARE v_current INT DEFAULT 1;
  IF EXISTS (SELECT 1 FROM habit_metrics_daily WHERE user_id=p_user AND day=v_yesterday AND accepted>0) THEN
    SELECT current_streak_days+1 INTO v_current FROM habit_streaks WHERE user_id=p_user FOR UPDATE;
    UPDATE habit_streaks SET current_streak_days=v_current, longest_streak_days=GREATEST(longest_streak_days,v_current), updated_at=NOW(3)
    WHERE user_id=p_user;
  ELSE
    INSERT INTO habit_streaks(user_id,current_streak_days,longest_streak_days,updated_at)
    VALUES(p_user,1,GREATEST(1,COALESCE((SELECT longest_streak_days FROM habit_streaks WHERE user_id=p_user),0)),NOW(3))
    ON DUPLICATE KEY UPDATE current_streak_days=1, longest_streak_days=GREATEST(longest_streak_days,1), updated_at=NOW(3);
  END IF;
END $$
DELIMITER ;

-- =============================================================
-- Trigger: notif_outbox_after_insert
-- Descripción: Emite evento outbox al crear notificación
-- Cumple: Event-driven, integridad, atomicidad
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
DELIMITER $$
DROP TRIGGER IF EXISTS notif_outbox_after_insert $$
CREATE TRIGGER notif_outbox_after_insert
AFTER INSERT ON NotificacionUsuario
FOR EACH ROW
BEGIN
  INSERT INTO outbox_events(aggregate_type, aggregate_id, event_type, payload)
  VALUES ('notification', NEW.id, 'notification.created',
          JSON_OBJECT('user_id', NEW.idUsuario, 'tipo', NEW.tipo, 'canal', NEW.canal));
END $$
DELIMITER ;

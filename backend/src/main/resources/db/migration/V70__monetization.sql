-- V70__monetization.sql
-- Propósito: Crear tabla monetizacion para gestión de planes y pagos.
-- Fecha: 2025-08-12
-- Autor: GitHub Copilot
-- Relacionado: ADR-2025-08-10, FLAG: monetizacion, Issue #119
-- Compliance: PSD2, ver docs/compliance/monetizacion.md

CREATE TABLE IF NOT EXISTS monetizacion (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	plan_type ENUM('free','pro','teams') NOT NULL DEFAULT 'free',
	amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
	status ENUM('active','inactive','cancelled') NOT NULL DEFAULT 'active',
	created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_user_id (user_id),
	INDEX idx_plan_type (plan_type)
);

-- Auditoría: Trigger para log de cambios en monetizacion
DELIMITER $$
CREATE TRIGGER monetizacion_audit
AFTER INSERT ON monetizacion
FOR EACH ROW
BEGIN
	INSERT INTO audit_log (entity, entity_id, action, changed_by, changed_at)
	VALUES ('monetizacion', NEW.id, 'INSERT', NEW.user_id, NEW.created_at);
END$$
DELIMITER ;

-- Validación: Ejecutar con Flyway y documentar logs en README/MonoDoc.

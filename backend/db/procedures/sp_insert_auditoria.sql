-- =============================================
-- Procedimiento: sp_insert_auditoria_avanzada
-- Descripción: Inserción avanzada en tabla de auditoría con validaciones y metadatos
-- Parámetros: Todos los campos necesarios para auditoría completa
-- Funcionalidad: Validación, inserción segura, notificación de eventos críticos
-- Autor: Sistema IMPULSE - Seguridad
-- Fecha: 2025-08-07
-- Versión: 2.0 (Extendida)
-- =============================================

DELIMITER $$

CREATE PROCEDURE sp_insert_auditoria_avanzada(
    IN p_id_usuario BIGINT,
    IN p_accion VARCHAR(100),
    IN p_detalles TEXT,
    IN p_ip_origen VARCHAR(45),
    IN p_user_agent TEXT,
    IN p_metadatos JSON,
    IN p_nivel_criticidad ENUM('BAJO', 'MEDIO', 'ALTO', 'CRITICO'),
    IN p_modulo VARCHAR(50),
    IN p_resultado ENUM('EXITOSO', 'FALLIDO', 'PARCIAL')
)
BEGIN
    DECLARE v_audit_id BIGINT;
    DECLARE v_user_exists INT DEFAULT 0;
    DECLARE v_ip_blocked INT DEFAULT 0;
    DECLARE v_daily_actions INT DEFAULT 0;
    DECLARE v_max_daily_actions INT DEFAULT 1000;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        -- Log crítico en tabla de errores del sistema
        INSERT INTO LogErroresSistema (
            modulo, error_type, error_message, timestamp, datos_contexto
        ) VALUES (
            COALESCE(p_modulo, 'AUDITORIA'),
            'AUDITORIA_FAILURE',
            'Error al insertar registro de auditoría',
            NOW(),
            JSON_OBJECT(
                'usuario_id', p_id_usuario,
                'accion', p_accion,
                'ip', p_ip_origen
            )
        );
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Validación de usuario (puede ser NULL para acciones anónimas)
    IF p_id_usuario IS NOT NULL THEN
        SELECT COUNT(*) INTO v_user_exists 
        FROM Usuario 
        WHERE id = p_id_usuario;
        
        IF v_user_exists = 0 THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Usuario especificado no existe en el sistema';
        END IF;
    END IF;

    -- Validación de IP en lista negra (tabla de IPs bloqueadas)
    SELECT COUNT(*) INTO v_ip_blocked
    FROM IPsBloqueadas 
    WHERE ip_address = p_ip_origen 
        AND estado = 'ACTIVO'
        AND (fecha_expiracion IS NULL OR fecha_expiracion > NOW());

    -- Validación de límite diario de acciones por usuario
    IF p_id_usuario IS NOT NULL THEN
        SELECT COUNT(*) INTO v_daily_actions
        FROM AuditoriaUsuario
        WHERE idUsuario = p_id_usuario 
            AND DATE(fechaAccion) = CURDATE();

        IF v_daily_actions >= v_max_daily_actions THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Límite diario de acciones excedido para este usuario';
        END IF;
    END IF;

    -- Inserción principal en auditoría
    INSERT INTO AuditoriaUsuario (
        idUsuario,
        accion,
        detalles,
        ipOrigen,
        userAgent,
        fechaAccion,
        resultado,
        metadatos
    ) VALUES (
        p_id_usuario,
        p_accion,
        p_detalles,
        p_ip_origen,
        p_user_agent,
        NOW(),
        p_resultado,
        COALESCE(p_metadatos, JSON_OBJECT())
    );

    SET v_audit_id = LAST_INSERT_ID();

    -- Inserción en tabla extendida de auditoría avanzada
    INSERT INTO AuditoriaAvanzada (
        idAuditoriaUsuario,
        nivelCriticidad,
        modulo,
        esIpBloqueada,
        hashSesion,
        timestampPreciso,
        datosExtendidos
    ) VALUES (
        v_audit_id,
        p_nivel_criticidad,
        p_modulo,
        v_ip_blocked > 0,
        SHA2(CONCAT(p_id_usuario, p_ip_origen, UNIX_TIMESTAMP()), 256),
        NOW(6), -- Microsegundos
        JSON_OBJECT(
            'acciones_diarias_usuario', v_daily_actions,
            'ip_previamente_bloqueada', v_ip_blocked > 0,
            'timestamp_unix', UNIX_TIMESTAMP(),
            'zona_horaria', @@session.time_zone
        )
    );

    -- Alertas automáticas para eventos críticos
    IF p_nivel_criticidad IN ('ALTO', 'CRITICO') OR p_resultado = 'FALLIDO' THEN
        INSERT INTO AlertasSeguridad (
            tipoAlerta,
            idUsuarioAfectado,
            descripcion,
            nivelPrioridad,
            estado,
            metadatosAlerta,
            fechaCreacion
        ) VALUES (
            'EVENTO_AUDITORIA_CRITICO',
            p_id_usuario,
            CONCAT('Evento de auditoría crítico: ', p_accion, ' - ', p_detalles),
            CASE p_nivel_criticidad
                WHEN 'CRITICO' THEN 'URGENTE'
                WHEN 'ALTO' THEN 'ALTA'
                ELSE 'MEDIA'
            END,
            'PENDIENTE',
            JSON_OBJECT(
                'accion', p_accion,
                'resultado', p_resultado,
                'ip_origen', p_ip_origen,
                'modulo', p_modulo,
                'audit_id', v_audit_id
            ),
            NOW()
        );
    END IF;

    -- Actualización de estadísticas de usuario si aplica
    IF p_id_usuario IS NOT NULL AND p_resultado = 'EXITOSO' THEN
        INSERT INTO EstadisticasUsuario (
            idUsuario, 
            fecha, 
            totalAcciones, 
            accionesExitosas,
            ultimaActividad
        ) VALUES (
            p_id_usuario,
            CURDATE(),
            1,
            1,
            NOW()
        ) ON DUPLICATE KEY UPDATE
            totalAcciones = totalAcciones + 1,
            accionesExitosas = accionesExitosas + 1,
            ultimaActividad = NOW();
    END IF;

    -- Retornar ID de auditoría creado
    SELECT v_audit_id as auditoria_id, 
           'Registro de auditoría insertado correctamente' as mensaje,
           p_nivel_criticidad as nivel_asignado,
           v_daily_actions + 1 as total_acciones_dia;

    COMMIT;

END$$

DELIMITER ;

-- =============================================
-- Crear tabla de auditoría avanzada si no existe
-- =============================================
CREATE TABLE IF NOT EXISTS AuditoriaAvanzada (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idAuditoriaUsuario BIGINT NOT NULL,
    nivelCriticidad ENUM('BAJO', 'MEDIO', 'ALTO', 'CRITICO') NOT NULL DEFAULT 'BAJO',
    modulo VARCHAR(50) NOT NULL,
    esIpBloqueada BOOLEAN DEFAULT FALSE,
    hashSesion VARCHAR(64),
    timestampPreciso TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    datosExtendidos JSON,
    INDEX idx_auditoria_avanzada_usuario (idAuditoriaUsuario),
    INDEX idx_auditoria_avanzada_criticidad (nivelCriticidad),
    INDEX idx_auditoria_avanzada_modulo (modulo),
    INDEX idx_auditoria_avanzada_timestamp (timestampPreciso),
    FOREIGN KEY (idAuditoriaUsuario) REFERENCES AuditoriaUsuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Crear tabla de IPs bloqueadas si no existe
-- =============================================
CREATE TABLE IF NOT EXISTS IPsBloqueadas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    razon_bloqueo VARCHAR(255) NOT NULL,
    fecha_bloqueo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP NULL,
    estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO',
    bloqueado_por BIGINT,
    metadatos JSON,
    UNIQUE KEY unique_ip_activa (ip_address, estado),
    INDEX idx_ip_estado (ip_address, estado),
    INDEX idx_ip_expiracion (fecha_expiracion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Procedimiento: sp_generar_reporte_usuario
-- Descripción: Genera reporte completo de actividad de un usuario
-- Parámetros: p_user_id (ID del usuario), p_fecha_desde, p_fecha_hasta
-- Retorna: Dataset con estadísticas y actividad del usuario
-- Autor: Sistema IMPULSE
-- Fecha: 2025-08-07
-- Versión: 1.0
-- =============================================

DELIMITER $$

CREATE PROCEDURE sp_generar_reporte_usuario(
    IN p_user_id BIGINT,
    IN p_fecha_desde DATE,
    IN p_fecha_hasta DATE
)
BEGIN
    DECLARE v_total_retos INT DEFAULT 0;
    DECLARE v_retos_completados INT DEFAULT 0;
    DECLARE v_retos_activos INT DEFAULT 0;
    DECLARE v_total_evidencias INT DEFAULT 0;
    DECLARE v_evidencias_aprobadas INT DEFAULT 0;
    DECLARE v_puntos_totales INT DEFAULT 0;
    DECLARE v_badges_obtenidos INT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Validar que el usuario existe
    IF NOT EXISTS (SELECT 1 FROM Usuario WHERE id = p_user_id AND estado = 'ACTIVO') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usuario no encontrado o inactivo';
    END IF;

    -- Calcular estadísticas de retos
    SELECT 
        COUNT(*) INTO v_total_retos
    FROM Reto r 
    WHERE r.idCreador = p_user_id 
        AND DATE(r.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

    SELECT 
        COUNT(*) INTO v_retos_completados
    FROM Reto r 
    WHERE r.idCreador = p_user_id 
        AND r.estado = 'COMPLETADO'
        AND DATE(r.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

    SELECT 
        COUNT(*) INTO v_retos_activos
    FROM Reto r 
    WHERE r.idCreador = p_user_id 
        AND r.estado = 'ACTIVO'
        AND DATE(r.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

    -- Calcular estadísticas de evidencias
    SELECT 
        COUNT(*) INTO v_total_evidencias
    FROM Evidencia e 
    WHERE e.idUsuario = p_user_id 
        AND DATE(e.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

    SELECT 
        COUNT(*) INTO v_evidencias_aprobadas
    FROM Evidencia e 
    WHERE e.idUsuario = p_user_id 
        AND e.estadoValidacion = 'APROBADA'
        AND DATE(e.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

    -- Calcular puntos y badges (simulado para MVP)
    SELECT 
        COALESCE(SUM(
            CASE 
                WHEN e.estadoValidacion = 'APROBADA' THEN 10
                WHEN e.estadoValidacion = 'PENDIENTE' THEN 5
                ELSE 0
            END
        ), 0) INTO v_puntos_totales
    FROM Evidencia e 
    WHERE e.idUsuario = p_user_id 
        AND DATE(e.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

    -- Generar reporte principal
    SELECT 
        p_user_id as usuario_id,
        u.nombre,
        u.apellidos,
        u.email,
        p_fecha_desde as fecha_desde,
        p_fecha_hasta as fecha_hasta,
        v_total_retos as total_retos_creados,
        v_retos_completados as retos_completados,
        v_retos_activos as retos_activos,
        v_total_evidencias as total_evidencias,
        v_evidencias_aprobadas as evidencias_aprobadas,
        CASE 
            WHEN v_total_evidencias > 0 THEN ROUND((v_evidencias_aprobadas * 100.0) / v_total_evidencias, 2)
            ELSE 0 
        END as porcentaje_exito,
        v_puntos_totales as puntos_obtenidos,
        v_badges_obtenidos as badges_obtenidos,
        NOW() as fecha_reporte
    FROM Usuario u
    WHERE u.id = p_user_id;

    -- Detalle de retos por estado
    SELECT 
        'detalle_retos' as seccion,
        r.id as reto_id,
        r.titulo,
        r.estado,
        r.dificultad,
        r.fechaInicio,
        r.fechaFin,
        r.metaObjetivo,
        r.fechaCreacion,
        DATEDIFF(COALESCE(r.fechaFin, NOW()), r.fechaInicio) as duracion_dias
    FROM Reto r
    WHERE r.idCreador = p_user_id 
        AND DATE(r.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta
    ORDER BY r.fechaCreacion DESC;

    -- Detalle de evidencias más recientes
    SELECT 
        'detalle_evidencias' as seccion,
        e.id as evidencia_id,
        r.titulo as reto_titulo,
        e.tipoEvidencia,
        e.estadoValidacion,
        e.fechaReporte,
        e.fechaValidacion,
        v.nombre as validador_nombre,
        e.comentarioValidador,
        e.descripcion
    FROM Evidencia e
    JOIN Reto r ON e.idReto = r.id
    LEFT JOIN Usuario v ON e.idValidador = v.id
    WHERE e.idUsuario = p_user_id 
        AND DATE(e.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta
    ORDER BY e.fechaReporte DESC
    LIMIT 20;

    -- Log de auditoría del reporte
    INSERT INTO AuditoriaUsuario (
        idUsuario, accion, detalles, ipOrigen, userAgent, 
        fechaAccion, resultado, metadatos
    ) VALUES (
        p_user_id, 
        'GENERAR_REPORTE', 
        CONCAT('Reporte generado para período ', p_fecha_desde, ' a ', p_fecha_hasta),
        '127.0.0.1',
        'SISTEMA_IMPULSE',
        NOW(),
        'EXITOSO',
        JSON_OBJECT(
            'retos_totales', v_total_retos,
            'evidencias_totales', v_total_evidencias,
            'puntos_obtenidos', v_puntos_totales
        )
    );

    COMMIT;

END$$

DELIMITER ;

-- =============================================
-- IMPULSE - Migración V2: Sistema de Retos
-- Descripción: Esquema completo de retos, evidencias y validaciones
-- Autor: Sistema IMPULSE
-- Fecha: 2025-08-07
-- Versión: 2.0 - Sistema de gamificación y validación
-- =============================================

-- Configuración inicial
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET AUTOCOMMIT = 0;
START TRANSACTION;

-- =============================================
-- Tabla: Reto
-- Descripción: Tabla principal de retos con todas las funcionalidades
-- =============================================
CREATE TABLE Reto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idCreador BIGINT NOT NULL,
    idCategoria BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    
    -- Fechas del reto
    fechaInicio TIMESTAMP NOT NULL,
    fechaFin TIMESTAMP NOT NULL,
    
    -- Configuración de validación
    tipoValidacion ENUM('MANUAL', 'AUTOMATICA', 'MIXTA') DEFAULT 'MANUAL',
    validadoresPermitidos ENUM('CUALQUIERA', 'SELECCIONADOS', 'NINGUNO') DEFAULT 'CUALQUIERA',
    
    -- Configuración del reto
    dificultad ENUM('BAJA', 'MEDIA', 'ALTA', 'EXTREMA') DEFAULT 'MEDIA',
    esPublico BOOLEAN DEFAULT TRUE,
    requiereEvidencia BOOLEAN DEFAULT TRUE,
    tipoEvidencia ENUM('TEXTO', 'FOTO', 'VIDEO', 'AUDIO', 'FOTO_Y_TEXTO', 'VIDEO_Y_TEXTO', 'TEXTO_Y_AUDIO') DEFAULT 'FOTO_Y_TEXTO',
    frecuenciaReporte ENUM('DIARIO', 'SEMANAL', 'QUINCENAL', 'MENSUAL', 'LIBRE') DEFAULT 'LIBRE',
    
    -- Objetivos y medición
    metaObjetivo VARCHAR(200),
    unidadMedida VARCHAR(50),
    valorObjetivo DECIMAL(10,2),
    
    -- Estado y control
    estado ENUM('BORRADOR', 'PENDIENTE', 'ACTIVO', 'PAUSADO', 'COMPLETADO', 'CANCELADO', 'ELIMINADO') DEFAULT 'BORRADOR',
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fechaPublicacion TIMESTAMP NULL,
    
    -- Configuración avanzada
    limiteParticipantes INT DEFAULT NULL,
    edadMinima INT DEFAULT NULL,
    edadMaxima INT DEFAULT NULL,
    paisesPermitidos JSON,
    etiquetas JSON,
    recompensas JSON,
    reglasEspeciales TEXT,
    
    -- Metadatos
    metadatos JSON,
    
    -- Índices optimizados
    INDEX idx_reto_creador (idCreador),
    INDEX idx_reto_categoria (idCategoria),
    INDEX idx_reto_estado (estado),
    INDEX idx_reto_fechas (fechaInicio, fechaFin),
    INDEX idx_reto_publico (esPublico),
    INDEX idx_reto_dificultad (dificultad),
    INDEX idx_reto_tipo_validacion (tipoValidacion),
    
    FOREIGN KEY (idCreador) REFERENCES Usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (idCategoria) REFERENCES CategoriaReto(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: ParticipacionReto
-- Descripción: Relación entre usuarios y retos en los que participan
-- =============================================
CREATE TABLE ParticipacionReto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idReto BIGINT NOT NULL,
    idUsuario BIGINT NOT NULL,
    fechaInscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('INSCRITO', 'ACTIVO', 'PAUSADO', 'COMPLETADO', 'ABANDONADO') DEFAULT 'INSCRITO',
    progreso DECIMAL(5,2) DEFAULT 0.00, -- Porcentaje de 0 a 100
    fechaCompletado TIMESTAMP NULL,
    notasPersonales TEXT,
    configuracionNotificaciones JSON,
    metadatos JSON,
    
    -- Índices optimizados
    INDEX idx_participacion_reto (idReto),
    INDEX idx_participacion_usuario (idUsuario),
    INDEX idx_participacion_estado (estado),
    INDEX idx_participacion_progreso (progreso),
    
    UNIQUE KEY unique_usuario_reto (idUsuario, idReto),
    FOREIGN KEY (idReto) REFERENCES Reto(id) ON DELETE CASCADE,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: Evidencia
-- Descripción: Evidencias reportadas por usuarios para sus retos
-- =============================================
CREATE TABLE Evidencia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idReto BIGINT NOT NULL,
    idUsuario BIGINT NOT NULL,
    fechaReporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipoEvidencia ENUM('TEXTO', 'FOTO', 'VIDEO', 'AUDIO', 'FOTO_Y_TEXTO', 'VIDEO_Y_TEXTO', 'TEXTO_Y_AUDIO') NOT NULL,
    descripcion TEXT,
    archivoUrl VARCHAR(500),
    archivoThumbnail VARCHAR(500),
    coordenadasGPS VARCHAR(50),
    
    -- Datos específicos del progreso
    valorReportado DECIMAL(10,2),
    unidadMedida VARCHAR(50),
    tiempoRegistrado TIME,
    distanciaRegistrada DECIMAL(10,2),
    
    -- Estado de validación
    estadoValidacion ENUM('PENDIENTE', 'EN_REVISION', 'APROBADA', 'RECHAZADA', 'AUTOMATICA_APROBADA') DEFAULT 'PENDIENTE',
    fechaValidacion TIMESTAMP NULL,
    idValidador BIGINT NULL,
    comentarioValidador TEXT,
    razonRechazo TEXT,
    puntuacionValidador DECIMAL(3,2), -- De 0 a 10
    
    -- Metadatos técnicos
    hashArchivo VARCHAR(64),
    tamanoArchivo BIGINT,
    metadatos JSON,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Índices optimizados
    INDEX idx_evidencia_reto (idReto),
    INDEX idx_evidencia_usuario (idUsuario),
    INDEX idx_evidencia_validador (idValidador),
    INDEX idx_evidencia_estado (estadoValidacion),
    INDEX idx_evidencia_fecha_reporte (fechaReporte),
    INDEX idx_evidencia_tipo (tipoEvidencia),
    
    FOREIGN KEY (idReto) REFERENCES Reto(id) ON DELETE CASCADE,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (idValidador) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: ValidadorReto
-- Descripción: Validadores asignados a retos específicos
-- =============================================
CREATE TABLE ValidadorReto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idReto BIGINT NOT NULL,
    idValidador BIGINT NOT NULL,
    fechaAsignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('ACTIVO', 'INACTIVO', 'SUSPENDIDO') DEFAULT 'ACTIVO',
    tipoValidador ENUM('PRINCIPAL', 'SECUNDARIO', 'ESPECIALISTA') DEFAULT 'PRINCIPAL',
    notasValidador TEXT,
    metadatos JSON,
    
    -- Índices optimizados
    INDEX idx_validador_reto (idReto),
    INDEX idx_validador_usuario (idValidador),
    INDEX idx_validador_estado (estado),
    
    UNIQUE KEY unique_validador_reto (idReto, idValidador),
    FOREIGN KEY (idReto) REFERENCES Reto(id) ON DELETE CASCADE,
    FOREIGN KEY (idValidador) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: LogroUsuario (Sistema de gamificación)
-- Descripción: Logros obtenidos por usuarios
-- =============================================
CREATE TABLE LogroUsuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idUsuario BIGINT NOT NULL,
    tipoLogro ENUM('RETO_COMPLETADO', 'RACHA_DIARIA', 'PRIMERA_EVIDENCIA', 'VALIDADOR_DESTACADO', 'MENTOR', 'CONSTANCIA', 'SUPERACION') NOT NULL,
    nombreLogro VARCHAR(100) NOT NULL,
    descripcionLogro TEXT,
    iconoLogro VARCHAR(50),
    puntosObtenidos INT DEFAULT 0,
    fechaObtencion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    idRetoRelacionado BIGINT NULL,
    datosLogro JSON,
    esPublico BOOLEAN DEFAULT TRUE,
    
    -- Índices optimizados
    INDEX idx_logro_usuario (idUsuario),
    INDEX idx_logro_tipo (tipoLogro),
    INDEX idx_logro_fecha (fechaObtencion),
    INDEX idx_logro_reto (idRetoRelacionado),
    
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (idRetoRelacionado) REFERENCES Reto(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: NotificacionUsuario
-- Descripción: Notificaciones del sistema para usuarios
-- =============================================
CREATE TABLE NotificacionUsuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idUsuario BIGINT NOT NULL,
    tipo ENUM('RETO_NUEVO', 'EVIDENCIA_APROBADA', 'EVIDENCIA_RECHAZADA', 'VALIDACION_PENDIENTE', 'LOGRO_OBTENIDO', 'RECORDATORIO', 'SISTEMA') NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaLectura TIMESTAMP NULL,
    idReferenciaReto BIGINT NULL,
    idReferenciaEvidencia BIGINT NULL,
    canal ENUM('APP', 'EMAIL', 'PUSH', 'SMS') DEFAULT 'APP',
    prioridad ENUM('BAJA', 'MEDIA', 'ALTA', 'URGENTE') DEFAULT 'MEDIA',
    metadatos JSON,
    
    -- Índices optimizados
    INDEX idx_notificacion_usuario (idUsuario),
    INDEX idx_notificacion_leida (leida),
    INDEX idx_notificacion_tipo (tipo),
    INDEX idx_notificacion_fecha (fechaCreacion),
    INDEX idx_notificacion_prioridad (prioridad),
    
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (idReferenciaReto) REFERENCES Reto(id) ON DELETE SET NULL,
    FOREIGN KEY (idReferenciaEvidencia) REFERENCES Evidencia(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Datos de ejemplo para desarrollo
-- =============================================

-- Reto de ejemplo para testing
INSERT INTO Reto (
    idCreador, idCategoria, titulo, descripcion,
    fechaInicio, fechaFin, tipoValidacion, dificultad,
    metaObjetivo, unidadMedida, estado, esPublico
) VALUES (
    1, 1, 'Reto de ejemplo: Caminar 10.000 pasos diarios',
    'Reto para mantener actividad física constante caminando 10.000 pasos cada día durante un mes',
    DATE_ADD(NOW(), INTERVAL 1 DAY),
    DATE_ADD(NOW(), INTERVAL 31 DAY),
    'MANUAL',
    'MEDIA',
    '10000 pasos diarios por 30 días',
    'pasos',
    'ACTIVO',
    TRUE
);

-- =============================================
-- Procedimientos almacenados adicionales
-- =============================================

-- Trigger para actualizar progreso automáticamente
DELIMITER $$
CREATE TRIGGER tr_evidencia_actualizar_progreso
AFTER INSERT ON Evidencia
FOR EACH ROW
BEGIN
    DECLARE v_total_dias INT DEFAULT 0;
    DECLARE v_evidencias_aprobadas INT DEFAULT 0;
    DECLARE v_progreso DECIMAL(5,2) DEFAULT 0;
    
    -- Calcular progreso basado en evidencias aprobadas
    SELECT DATEDIFF(fechaFin, fechaInicio) + 1 INTO v_total_dias
    FROM Reto WHERE id = NEW.idReto;
    
    SELECT COUNT(*) INTO v_evidencias_aprobadas
    FROM Evidencia 
    WHERE idReto = NEW.idReto 
        AND idUsuario = NEW.idUsuario 
        AND estadoValidacion = 'APROBADA';
    
    IF v_total_dias > 0 THEN
        SET v_progreso = LEAST(100, (v_evidencias_aprobadas * 100.0) / v_total_dias);
        
        UPDATE ParticipacionReto 
        SET progreso = v_progreso,
            fechaCompletado = CASE WHEN v_progreso >= 100 THEN NOW() ELSE NULL END,
            estado = CASE WHEN v_progreso >= 100 THEN 'COMPLETADO' ELSE estado END
        WHERE idReto = NEW.idReto AND idUsuario = NEW.idUsuario;
    END IF;
END$$
DELIMITER ;

-- =============================================
-- Configuraciones finales
-- =============================================
SET FOREIGN_KEY_CHECKS = 1;
COMMIT;

-- =============================================
-- Verificación de migración V2
-- =============================================
SELECT 'V2 - Sistema de retos inicializado correctamente' as status,
       (SELECT COUNT(*) FROM Reto) as total_retos,
       (SELECT COUNT(*) FROM CategoriaReto) as total_categorias;

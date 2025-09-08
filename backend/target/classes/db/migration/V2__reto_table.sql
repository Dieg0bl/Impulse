-- V2__reto_table.sql
-- Migración para crear las tablas relacionadas con retos
-- Incluye: retos, participaciones, evidencias, validaciones

-- =============================================================================
-- TABLA: retos
-- Tabla principal para almacenar los retos/desafíos
-- =============================================================================
CREATE TABLE retos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    categoria_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    
    -- Configuración del reto
    dificultad ENUM('FACIL', 'INTERMEDIO', 'DIFICIL') NOT NULL,
    duracion_dias INT NOT NULL DEFAULT 7,
    puntos_recompensa INT NOT NULL DEFAULT 100,
    
    -- Requisitos y validación
    requisitos_previos TEXT,
    criterios_validacion TEXT NOT NULL,
    tipo_evidencia ENUM('FOTO', 'VIDEO', 'TEXTO', 'CUALQUIERA') DEFAULT 'CUALQUIERA',
    
    -- Control de acceso
    es_privado BOOLEAN DEFAULT FALSE,
    requiere_aprobacion BOOLEAN DEFAULT FALSE,
    max_participantes INT NULL,
    
    -- Estado del reto
    estado ENUM('BORRADOR', 'ACTIVO', 'PAUSADO', 'FINALIZADO', 'CANCELADO') DEFAULT 'BORRADOR',
    fecha_inicio DATE,
    fecha_fin DATE,
    
    -- Metadatos
    tags JSON,
    participantes_actuales INT DEFAULT 0,
    validaciones_pendientes INT DEFAULT 0,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    
    INDEX idx_categoria (categoria_id),
    INDEX idx_autor (autor_id),
    INDEX idx_estado (estado),
    INDEX idx_dificultad (dificultad),
    INDEX idx_fecha_inicio (fecha_inicio),
    INDEX idx_privado (es_privado),
    INDEX idx_deleted_at (deleted_at),
    
    FULLTEXT idx_busqueda (titulo, descripcion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLA: participaciones_reto
-- Registro de usuarios que participan en retos
-- =============================================================================
CREATE TABLE participaciones_reto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    
    -- Estado de participación
    estado ENUM('INSCRITO', 'ACTIVO', 'COMPLETADO', 'ABANDONADO', 'SUSPENDIDO') DEFAULT 'INSCRITO',
    progreso_porcentaje DECIMAL(5,2) DEFAULT 0.00,
    
    -- Fechas importantes
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio TIMESTAMP NULL,
    fecha_completado TIMESTAMP NULL,
    fecha_ultima_actividad TIMESTAMP NULL,
    
    -- Métricas
    dias_activos INT DEFAULT 0,
    evidencias_enviadas INT DEFAULT 0,
    evidencias_validadas INT DEFAULT 0,
    puntos_obtenidos INT DEFAULT 0,
    
    -- Notas del participante
    notas_privadas TEXT,
    motivacion TEXT,
    
    FOREIGN KEY (reto_id) REFERENCES retos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_participacion (reto_id, usuario_id),
    INDEX idx_reto_id (reto_id),
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_inscripcion (fecha_inscripcion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLA: evidencias
-- Evidencias enviadas por los usuarios para validar progreso
-- =============================================================================
CREATE TABLE evidencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    participacion_id BIGINT NOT NULL,
    reto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    
    -- Contenido de la evidencia
    tipo ENUM('FOTO', 'VIDEO', 'TEXTO', 'ENLACE') NOT NULL,
    titulo VARCHAR(200),
    descripcion TEXT NOT NULL,
    contenido_url VARCHAR(1000),
    contenido_texto TEXT,
    
    -- Metadatos del archivo
    nombre_archivo VARCHAR(255),
    tipo_mime VARCHAR(100),
    tamaño_bytes BIGINT,
    
    -- Estado de validación
    estado ENUM('PENDIENTE', 'VALIDANDO', 'APROBADA', 'RECHAZADA', 'REVISIÓN') DEFAULT 'PENDIENTE',
    validador_id BIGINT NULL,
    fecha_validacion TIMESTAMP NULL,
    comentarios_validacion TEXT,
    puntos_otorgados INT DEFAULT 0,
    
    -- Metadatos
    ip_envio VARCHAR(45),
    metadata JSON,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    FOREIGN KEY (participacion_id) REFERENCES participaciones_reto(id) ON DELETE CASCADE,
    FOREIGN KEY (reto_id) REFERENCES retos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (validador_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    
    INDEX idx_participacion (participacion_id),
    INDEX idx_reto (reto_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_validador (validador_id),
    INDEX idx_estado (estado),
    INDEX idx_tipo (tipo),
    INDEX idx_fecha_creacion (fecha_creacion),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLA: validaciones
-- Registro detallado del proceso de validación
-- =============================================================================
CREATE TABLE validaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evidencia_id BIGINT NOT NULL,
    validador_id BIGINT NOT NULL,
    
    -- Resultado de la validación
    decision ENUM('APROBADA', 'RECHAZADA', 'NECESITA_REVISION') NOT NULL,
    puntos_asignados INT DEFAULT 0,
    comentarios TEXT,
    
    -- Criterios evaluados
    criterios_cumplidos JSON,
    calidad_evidencia ENUM('EXCELENTE', 'BUENA', 'REGULAR', 'INSUFICIENTE'),
    
    -- Proceso
    tiempo_revision_minutos INT,
    requiere_segunda_opinion BOOLEAN DEFAULT FALSE,
    validador_secundario_id BIGINT NULL,
    
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_validacion TIMESTAMP NULL,
    
    FOREIGN KEY (evidencia_id) REFERENCES evidencias(id) ON DELETE CASCADE,
    FOREIGN KEY (validador_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (validador_secundario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    
    INDEX idx_evidencia (evidencia_id),
    INDEX idx_validador (validador_id),
    INDEX idx_decision (decision),
    INDEX idx_fecha_validacion (fecha_validacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLA: logros
-- Sistema de logros y badges para gamificación
-- =============================================================================
CREATE TABLE logros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT NOT NULL,
    icono VARCHAR(100),
    categoria VARCHAR(50),
    
    -- Criterios para obtener el logro
    tipo_logro ENUM('RETOS_COMPLETADOS', 'PUNTOS_ACUMULADOS', 'RACHA_DIAS', 'VALIDACIONES_REALIZADAS', 'ESPECIAL') NOT NULL,
    valor_objetivo INT,
    es_secreto BOOLEAN DEFAULT FALSE,
    
    -- Recompensas
    puntos_bonus INT DEFAULT 0,
    es_repetible BOOLEAN DEFAULT FALSE,
    
    -- Estado
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_tipo (tipo_logro),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLA: usuario_logros
-- Relación entre usuarios y logros obtenidos
-- =============================================================================
CREATE TABLE usuario_logros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    logro_id BIGINT NOT NULL,
    
    -- Metadatos del logro
    fecha_obtencion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reto_asociado_id BIGINT NULL,
    valor_alcanzado INT,
    
    -- Visibilidad
    es_visible BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (logro_id) REFERENCES logros(id) ON DELETE CASCADE,
    FOREIGN KEY (reto_asociado_id) REFERENCES retos(id) ON DELETE SET NULL,
    
    UNIQUE KEY unique_usuario_logro (usuario_id, logro_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_logro (logro_id),
    INDEX idx_fecha_obtencion (fecha_obtencion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- DATOS INICIALES - LOGROS
-- =============================================================================
INSERT INTO logros (nombre, descripcion, icono, categoria, tipo_logro, valor_objetivo, puntos_bonus) VALUES
('Primer Paso', 'Completa tu primer reto', '', 'Principiante', 'RETOS_COMPLETADOS', 1, 50),
('Perseverante', 'Completa 5 retos', '', 'Dedicación', 'RETOS_COMPLETADOS', 5, 100),
('Campeón', 'Completa 10 retos', '', 'Dedicación', 'RETOS_COMPLETADOS', 10, 200),
('Maestro', 'Completa 25 retos', '', 'Maestría', 'RETOS_COMPLETADOS', 25, 500),
('Leyenda', 'Completa 50 retos', '', 'Leyenda', 'RETOS_COMPLETADOS', 50, 1000),
('Millonario', 'Acumula 1000 puntos', '', 'Puntos', 'PUNTOS_ACUMULADOS', 1000, 100),
('Racha de Fuego', 'Mantén una racha de 7 días', '', 'Constancia', 'RACHA_DIAS', 7, 150),
('Validador Experto', 'Realiza 100 validaciones', '', 'Validación', 'VALIDACIONES_REALIZADAS', 100, 300);

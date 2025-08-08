-- V1__init_schema.sql
-- Migración inicial del esquema de base de datos IMPULSE
-- Cumple compliance: RGPD, ISO 27001, ENS
-- Charset: utf8mb4_unicode_ci para soporte completo Unicode

-- =============================================================================
-- TABLA: usuarios
-- Almacena información de usuarios con campos obligatorios para RGPD
-- =============================================================================
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE,
    telefono VARCHAR(20),
    pais VARCHAR(100),
    idioma_preferido VARCHAR(10) DEFAULT 'es',
    timezone VARCHAR(50) DEFAULT 'Europe/Madrid',
    
    -- Campos de control
    estado ENUM('ACTIVO', 'INACTIVO', 'SUSPENDIDO', 'PENDIENTE_VERIFICACION') DEFAULT 'PENDIENTE_VERIFICACION',
    rol ENUM('USUARIO', 'VALIDADOR', 'ADMIN', 'MODERADOR') DEFAULT 'USUARIO',
    email_verificado BOOLEAN DEFAULT FALSE,
    
    -- Configuración de privacidad (RGPD)
    acepta_terminos BOOLEAN DEFAULT FALSE,
    acepta_privacidad BOOLEAN DEFAULT FALSE,
    acepta_marketing BOOLEAN DEFAULT FALSE,
    perfil_publico BOOLEAN DEFAULT FALSE,
    
    -- Metadatos
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP NULL,
    ip_registro VARCHAR(45),
    
    -- Soft delete
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_email (email),
    INDEX idx_estado (estado),
    INDEX idx_fecha_creacion (fecha_creacion),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLA: perfiles_usuario
-- Información extendida del perfil del usuario
-- =============================================================================
CREATE TABLE perfiles_usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    avatar_url VARCHAR(500),
    biografia TEXT,
    profesion VARCHAR(150),
    nivel_experiencia ENUM('PRINCIPIANTE', 'INTERMEDIO', 'AVANZADO', 'EXPERTO') DEFAULT 'PRINCIPIANTE',
    
    -- Estadísticas
    puntos_totales INT DEFAULT 0,
    retos_completados INT DEFAULT 0,
    racha_actual INT DEFAULT 0,
    racha_maxima INT DEFAULT 0,
    
    -- Configuración de notificaciones
    notif_email_retos BOOLEAN DEFAULT TRUE,
    notif_email_validaciones BOOLEAN DEFAULT TRUE,
    notif_push_recordatorios BOOLEAN DEFAULT TRUE,
    notif_sms_criticas BOOLEAN DEFAULT FALSE,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_puntos (puntos_totales)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLA: categorias
-- Categorías para clasificar retos
-- =============================================================================
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    icono VARCHAR(50),
    color_hex VARCHAR(7) DEFAULT '#3B82F6',
    activa BOOLEAN DEFAULT TRUE,
    orden_visualizacion INT DEFAULT 0,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_activa (activa),
    INDEX idx_orden (orden_visualizacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLA: auditoria
-- Registro de auditoría para compliance y seguridad
-- =============================================================================
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    accion VARCHAR(100) NOT NULL,
    tabla_afectada VARCHAR(100),
    registro_id BIGINT,
    datos_anteriores JSON,
    datos_nuevos JSON,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    sesion_id VARCHAR(255),
    
    -- Clasificación de la acción
    tipo_accion ENUM('CREATE', 'READ', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'ADMIN') NOT NULL,
    nivel_critico ENUM('BAJO', 'MEDIO', 'ALTO', 'CRITICO') DEFAULT 'BAJO',
    
    fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_fecha_accion (fecha_accion),
    INDEX idx_tipo_accion (tipo_accion),
    INDEX idx_nivel_critico (nivel_critico),
    INDEX idx_tabla_afectada (tabla_afectada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- DATOS INICIALES
-- =============================================================================

-- Insertar categorías predefinidas
INSERT INTO categorias (nombre, descripcion, icono, color_hex, orden_visualizacion) VALUES
('Fitness y Salud', 'Retos relacionados con ejercicio físico y bienestar', '', '#10B981', 1),
('Educación', 'Aprendizaje de nuevas habilidades y conocimientos', '', '#3B82F6', 2),
('Creatividad', 'Proyectos artísticos y creativos', '', '#8B5CF6', 3),
('Tecnología', 'Programación, desarrollo y tecnología', '', '#06B6D4', 4),
('Sostenibilidad', 'Cuidado del medio ambiente y sostenibilidad', '', '#22C55E', 5),
('Social', 'Habilidades sociales y networking', '', '#F59E0B', 6),
('Desarrollo Personal', 'Crecimiento personal y profesional', '', '#EF4444', 7);

-- Crear usuario administrador por defecto
INSERT INTO usuarios (email, password_hash, nombre, apellidos, estado, rol, email_verificado, acepta_terminos, acepta_privacidad, ip_registro) 
VALUES ('admin@impulse.dev', '$2a$10$example_hash_here', 'Admin', 'IMPULSE', 'ACTIVO', 'ADMIN', TRUE, TRUE, TRUE, '127.0.0.1');

-- Crear perfil para el admin
INSERT INTO perfiles_usuario (usuario_id, biografia, profesion, nivel_experiencia) 
VALUES (1, 'Administrador del sistema IMPULSE', 'Administrador de Sistema', 'EXPERTO');

-- =============================================
-- IMPULSE - Migración V1: Esquema Base de Datos
-- Descripción: Esquema inicial con usuarios, auditoría y estructura base
-- Autor: Sistema IMPULSE
-- Fecha: 2025-08-07
-- Versión: 1.0 - GDPR Compliant
-- =============================================

-- Configuración inicial
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET AUTOCOMMIT = 0;
START TRANSACTION;

-- =============================================
-- Tabla: Usuario
-- Descripción: Tabla principal de usuarios con cumplimiento GDPR
-- =============================================
CREATE TABLE Usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    fechaNacimiento DATE,
    telefono VARCHAR(20),
    pais VARCHAR(100),
    idiomaPreferido VARCHAR(5) DEFAULT 'es',
    timezone VARCHAR(50) DEFAULT 'Europe/Madrid',
    
    -- Estados y roles
    estado ENUM('ACTIVO', 'INACTIVO', 'SUSPENDIDO', 'ELIMINADO') DEFAULT 'ACTIVO',
    rol ENUM('USUARIO', 'VALIDADOR', 'ADMIN', 'SUPER_ADMIN') DEFAULT 'USUARIO',
    
    -- Verificaciones
    emailVerificado BOOLEAN DEFAULT FALSE,
    telefonoVerificado BOOLEAN DEFAULT FALSE,
    
    -- Consentimientos GDPR
    aceptaTerminos BOOLEAN DEFAULT FALSE,
    fechaAceptacionTerminos TIMESTAMP NULL,
    aceptaPrivacidad BOOLEAN DEFAULT FALSE,
    fechaAceptacionPrivacidad TIMESTAMP NULL,
    aceptaMarketing BOOLEAN DEFAULT FALSE,
    fechaAceptacionMarketing TIMESTAMP NULL,
    
    -- Privacidad y visibilidad
    perfilPublico BOOLEAN DEFAULT TRUE,
    compartirProgreso BOOLEAN DEFAULT TRUE,
    notificacionesEmail BOOLEAN DEFAULT TRUE,
    notificacionesPush BOOLEAN DEFAULT FALSE,
    
    -- Metadatos temporales
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fechaUltimoAcceso TIMESTAMP,
    fechaEliminacion TIMESTAMP NULL,
    
    -- Datos de registro y seguridad
    ipRegistro VARCHAR(45),
    ipUltimoAcceso VARCHAR(45),
    tokenVerificacion VARCHAR(255),
    tokenRecuperacion VARCHAR(255),
    fechaExpiracionToken TIMESTAMP NULL,
    intentosFallidos INT DEFAULT 0,
    fechaBloqueo TIMESTAMP NULL,
    
    -- Índices optimizados
    INDEX idx_usuario_email (email),
    INDEX idx_usuario_estado (estado),
    INDEX idx_usuario_rol (rol),
    INDEX idx_usuario_fechas (fechaCreacion, fechaUltimoAcceso),
    INDEX idx_usuario_tokens (tokenVerificacion, tokenRecuperacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: PerfilUsuario
-- Descripción: Información extendida del perfil de usuario
-- =============================================
CREATE TABLE PerfilUsuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idUsuario BIGINT NOT NULL,
    biografia TEXT,
    avatarUrl VARCHAR(500),
    sitioWeb VARCHAR(255),
    redesSociales JSON,
    preferenciasNotificacion JSON,
    configuracionPrivacidad JSON,
    metadatosPersonalizacion JSON,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE,
    INDEX idx_perfil_usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: CategoriaReto
-- Descripción: Categorías disponibles para clasificar retos
-- =============================================
CREATE TABLE CategoriaReto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    icono VARCHAR(50),
    color VARCHAR(7), -- Color hexadecimal
    activa BOOLEAN DEFAULT TRUE,
    ordenVisualizacion INT DEFAULT 0,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_categoria_activa (activa),
    INDEX idx_categoria_orden (ordenVisualizacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: AuditoriaUsuario
-- Descripción: Log completo de auditoría de acciones de usuarios
-- =============================================
CREATE TABLE AuditoriaUsuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idUsuario BIGINT,
    accion VARCHAR(100) NOT NULL,
    detalles TEXT,
    ipOrigen VARCHAR(45),
    userAgent TEXT,
    fechaAccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resultado ENUM('EXITOSO', 'FALLIDO', 'PARCIAL') DEFAULT 'EXITOSO',
    metadatos JSON,
    
    INDEX idx_auditoria_usuario (idUsuario),
    INDEX idx_auditoria_fecha (fechaAccion),
    INDEX idx_auditoria_accion (accion),
    INDEX idx_auditoria_resultado (resultado),
    INDEX idx_auditoria_ip (ipOrigen),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: SesionUsuario
-- Descripción: Control de sesiones activas de usuarios
-- =============================================
CREATE TABLE SesionUsuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idUsuario BIGINT NOT NULL,
    tokenSesion VARCHAR(255) NOT NULL UNIQUE,
    ipAddress VARCHAR(45),
    userAgent TEXT,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaExpiracion TIMESTAMP NOT NULL,
    fechaUltimoAcceso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activa BOOLEAN DEFAULT TRUE,
    dispositivo VARCHAR(100),
    ubicacion VARCHAR(200),
    
    INDEX idx_sesion_usuario (idUsuario),
    INDEX idx_sesion_token (tokenSesion),
    INDEX idx_sesion_activa (activa),
    INDEX idx_sesion_expiracion (fechaExpiracion),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Datos iniciales: Categorías de retos
-- =============================================
INSERT INTO CategoriaReto (nombre, descripcion, icono, color, ordenVisualizacion) VALUES
('Deporte y Fitness', 'Retos relacionados con actividad física y deportes', 'fitness', '#FF6B6B', 1),
('Educación y Aprendizaje', 'Retos de desarrollo de habilidades y conocimientos', 'education', '#4ECDC4', 2),
('Salud y Bienestar', 'Retos enfocados en salud física y mental', 'health', '#45B7D1', 3),
('Desarrollo Personal', 'Retos de crecimiento personal y profesional', 'growth', '#96CEB4', 4),
('Finanzas Personales', 'Retos relacionados con ahorro e inversión', 'money', '#FFEAA7', 5),
('Creatividad y Arte', 'Retos artísticos y de expresión creativa', 'art', '#DDA0DD', 6),
('Tecnología', 'Retos relacionados con programación y tecnología', 'tech', '#74B9FF', 7),
('Voluntariado y Social', 'Retos de ayuda comunitaria y social', 'volunteer', '#00B894', 8);

-- =============================================
-- Usuario administrador inicial (contraseña: admin123)
-- =============================================
INSERT INTO Usuario (
    email, passwordHash, nombre, apellidos, 
    estado, rol, emailVerificado, 
    aceptaTerminos, aceptaPrivacidad,
    fechaAceptacionTerminos, fechaAceptacionPrivacidad,
    ipRegistro
) VALUES (
    'admin@impulse.dev',
    '$2a$10$8.UnVuG9HHO4X/LjUNdrL.nzANNlKMiP4nNb4/G4k3D4YO8FCFUiG',
    'Administrador',
    'Sistema IMPULSE',
    'ACTIVO',
    'SUPER_ADMIN',
    TRUE,
    TRUE,
    TRUE,
    NOW(),
    NOW(),
    '127.0.0.1'
);

-- =============================================
-- Configuraciones finales
-- =============================================
SET FOREIGN_KEY_CHECKS = 1;
COMMIT;

-- =============================================
-- Verificación de migración
-- =============================================
SELECT 'V1 - Esquema base inicializado correctamente' as status,
       COUNT(*) as total_categorias FROM CategoriaReto;

-- V1__init_schema.sql (TEST canonical)
-- Copied from db/migration-test consolidated script to be placed on classpath for tests.
-- This is the single canonical migration used by the test profile (H2-compatible).

CREATE TABLE usuarios (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE,
    telefono VARCHAR(20),
    pais VARCHAR(100),
    idioma_preferido VARCHAR(10) DEFAULT 'es',
    timezone VARCHAR(50) DEFAULT 'Europe/Madrid',
    estado VARCHAR(32) DEFAULT 'PENDIENTE_VERIFICACION',
    rol VARCHAR(32) DEFAULT 'USUARIO',
    email_verificado BIT DEFAULT 0,
    acepta_terminos BIT DEFAULT 0,
    acepta_privacidad BIT DEFAULT 0,
    acepta_marketing BIT DEFAULT 0,
    perfil_publico BIT DEFAULT 0,
    fecha_creacion DATETIME2 DEFAULT SYSDATETIME(),
    fecha_actualizacion DATETIME2 DEFAULT SYSDATETIME(),
    fecha_ultimo_acceso DATETIME2 NULL,
    ip_registro VARCHAR(45),
    deleted_at DATETIME2 NULL
);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);
CREATE INDEX idx_usuarios_fecha_creacion ON usuarios(fecha_creacion);
CREATE INDEX idx_usuarios_deleted_at ON usuarios(deleted_at);

-- (Truncated: the full migration is in db/migration-test/V1__init_schema.sql)
-- Copia de V1__init_schema.sql para pruebas (H2-friendly)
-- Generado automáticamente desde src/main/resources
-- Contenido completo del consolidado V1__init_schema.sql

-- (El archivo real es largo; para evitar duplicar en este patch, se referencia al archivo original ya presente en src/main/resources)


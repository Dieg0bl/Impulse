-- V1__init_schema.sql (CONSOLIDADO)
-- Script consolidado generado para entorno de TEST/local.
-- Incluye el estado acumulado de las migraciones V1..Vxx adaptadas para H2/MySQL.
-- NOTA: Este archivo reemplaza únicamente el script inicial usado en el perfil de pruebas. No
-- borra el historial de migraciones en src/main/resources/db/migration; conservarlo para producción.

-- Backup: original consolidated migration moved to V1__init_schema.sql.bak
-- To restore, rename this file back to V1__init_schema.sql

    registro_id BIGINT,
    datos_anteriores JSON,
    datos_nuevos JSON,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    sesion_id VARCHAR(255),
    tipo_accion VARCHAR(32) NOT NULL,
    nivel_critico VARCHAR(16) DEFAULT 'BAJO',
    fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE auditoria ADD CONSTRAINT IF NOT EXISTS fk_auditoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario_id ON auditoria(usuario_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha_accion ON auditoria(fecha_accion);
CREATE INDEX IF NOT EXISTS idx_auditoria_tipo_accion ON auditoria(tipo_accion);
CREATE INDEX IF NOT EXISTS idx_auditoria_nivel_critico ON auditoria(nivel_critico);
CREATE INDEX IF NOT EXISTS idx_auditoria_tabla_afectada ON auditoria(tabla_afectada);

-- =========================
-- TABLAS DE RETOS (retos, participaciones_reto, evidencias, validaciones, logros)
-- =========================
CREATE TABLE IF NOT EXISTS retos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    categoria_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    dificultad VARCHAR(32) NOT NULL,
    duracion_dias INT NOT NULL DEFAULT 7,
    puntos_recompensa INT NOT NULL DEFAULT 100,
    requisitos_previos TEXT,
    criterios_validacion TEXT NOT NULL,
    tipo_evidencia VARCHAR(32) DEFAULT 'CUALQUIERA',
    es_privado BOOLEAN DEFAULT FALSE,
    requiere_aprobacion BOOLEAN DEFAULT FALSE,
    max_participantes INT NULL,
    estado VARCHAR(32) DEFAULT 'BORRADOR',
    fecha_inicio DATE,
    fecha_fin DATE,
    tags JSON,
    participantes_actuales INT DEFAULT 0,
    validaciones_pendientes INT DEFAULT 0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);
ALTER TABLE retos ADD CONSTRAINT IF NOT EXISTS fk_retos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id);
ALTER TABLE retos ADD CONSTRAINT IF NOT EXISTS fk_retos_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id);
CREATE INDEX IF NOT EXISTS idx_retos_categoria ON retos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_retos_autor ON retos(autor_id);
CREATE INDEX IF NOT EXISTS idx_retos_estado ON retos(estado);
CREATE INDEX IF NOT EXISTS idx_retos_dificultad ON retos(dificultad);
CREATE INDEX IF NOT EXISTS idx_retos_fecha_inicio ON retos(fecha_inicio);
CREATE INDEX IF NOT EXISTS idx_retos_privado ON retos(es_privado);
CREATE INDEX IF NOT EXISTS idx_retos_deleted_at ON retos(deleted_at);
CREATE INDEX IF NOT EXISTS idx_retos_busqueda_titulo_descripcion ON retos(titulo, descripcion);

CREATE TABLE IF NOT EXISTS participaciones_reto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(32) DEFAULT 'INSCRITO',
    progreso_porcentaje DECIMAL(5,2) DEFAULT 0.00,
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio TIMESTAMP NULL,
    fecha_completado TIMESTAMP NULL,
    fecha_ultima_actividad TIMESTAMP NULL,
    dias_activos INT DEFAULT 0,
    evidencias_enviadas INT DEFAULT 0,
    evidencias_validadas INT DEFAULT 0,
    puntos_obtenidos INT DEFAULT 0,
    notas_privadas TEXT,
    motivacion TEXT
);
ALTER TABLE participaciones_reto ADD CONSTRAINT IF NOT EXISTS fk_participaciones_reto_reto FOREIGN KEY (reto_id) REFERENCES retos(id) ON DELETE CASCADE;
ALTER TABLE participaciones_reto ADD CONSTRAINT IF NOT EXISTS fk_participaciones_reto_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
CREATE UNIQUE INDEX IF NOT EXISTS unique_participacion ON participaciones_reto(reto_id, usuario_id);
CREATE INDEX IF NOT EXISTS idx_participaciones_reto_reto_id ON participaciones_reto(reto_id);
CREATE INDEX IF NOT EXISTS idx_participaciones_reto_usuario_id ON participaciones_reto(usuario_id);
CREATE INDEX IF NOT EXISTS idx_participaciones_reto_estado ON participaciones_reto(estado);
CREATE INDEX IF NOT EXISTS idx_participaciones_reto_fecha_inscripcion ON participaciones_reto(fecha_inscripcion);

CREATE TABLE IF NOT EXISTS evidencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    participacion_id BIGINT NOT NULL,
    reto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(32) NOT NULL,
    titulo VARCHAR(200),
    descripcion TEXT NOT NULL,
    contenido_url VARCHAR(1000),
    contenido_texto TEXT,
    nombre_archivo VARCHAR(255),
    tipo_mime VARCHAR(100),
    tamano_bytes BIGINT,
    estado VARCHAR(32) DEFAULT 'PENDIENTE',
    validador_id BIGINT NULL,
    fecha_validacion TIMESTAMP NULL,
    comentarios_validacion TEXT,
    puntos_otorgados INT DEFAULT 0,
    ip_envio VARCHAR(45),
    metadata JSON,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);
ALTER TABLE evidencias ADD CONSTRAINT IF NOT EXISTS fk_evidencias_participacion FOREIGN KEY (participacion_id) REFERENCES participaciones_reto(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT IF NOT EXISTS fk_evidencias_reto FOREIGN KEY (reto_id) REFERENCES retos(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT IF NOT EXISTS fk_evidencias_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT IF NOT EXISTS fk_evidencias_validador FOREIGN KEY (validador_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_evidencias_participacion ON evidencias(participacion_id);
CREATE INDEX IF NOT EXISTS idx_evidencias_reto ON evidencias(reto_id);
CREATE INDEX IF NOT EXISTS idx_evidencias_usuario ON evidencias(usuario_id);
CREATE INDEX IF NOT EXISTS idx_evidencias_validador ON evidencias(validador_id);
CREATE INDEX IF NOT EXISTS idx_evidencias_estado ON evidencias(estado);
CREATE INDEX IF NOT EXISTS idx_evidencias_tipo ON evidencias(tipo);
CREATE INDEX IF NOT EXISTS idx_evidencias_fecha_creacion ON evidencias(fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_evidencias_deleted_at ON evidencias(deleted_at);

CREATE TABLE IF NOT EXISTS validaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evidencia_id BIGINT NOT NULL,
    validador_id BIGINT NOT NULL,
    decision VARCHAR(32) NOT NULL,
    puntos_asignados INT DEFAULT 0,
    comentarios TEXT,
    criterios_cumplidos JSON,
    calidad_evidencia VARCHAR(32),
    tiempo_revision_minutos INT,
    requiere_segunda_opinion BOOLEAN DEFAULT FALSE,
    validador_secundario_id BIGINT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_validacion TIMESTAMP NULL
);
ALTER TABLE validaciones ADD CONSTRAINT IF NOT EXISTS fk_validaciones_evidencia FOREIGN KEY (evidencia_id) REFERENCES evidencias(id) ON DELETE CASCADE;
ALTER TABLE validaciones ADD CONSTRAINT IF NOT EXISTS fk_validaciones_validador FOREIGN KEY (validador_id) REFERENCES usuarios(id) ON DELETE CASCADE;
ALTER TABLE validaciones ADD CONSTRAINT IF NOT EXISTS fk_validaciones_validador_secundario FOREIGN KEY (validador_secundario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_validaciones_evidencia ON validaciones(evidencia_id);
CREATE INDEX IF NOT EXISTS idx_validaciones_validador ON validaciones(validador_id);
CREATE INDEX IF NOT EXISTS idx_validaciones_decision ON validaciones(decision);
CREATE INDEX IF NOT EXISTS idx_validaciones_fecha_validacion ON validaciones(fecha_validacion);

CREATE TABLE IF NOT EXISTS logros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    icono VARCHAR(100),
    categoria VARCHAR(50),
    tipo_logro VARCHAR(64) NOT NULL,
    valor_objetivo INT,
    es_secreto BOOLEAN DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_logros_nombre ON logros(nombre);

-- =========================
-- EVENTS & SURVEYS (V10)
-- =========================
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    event_type VARCHAR(100) NOT NULL,
    properties JSON NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    session_id VARCHAR(100) NULL,
    source VARCHAR(50) NULL
);
CREATE INDEX IF NOT EXISTS idx_events_user_type ON events(user_id, event_type);
CREATE INDEX IF NOT EXISTS idx_events_ts ON events(timestamp);

CREATE TABLE IF NOT EXISTS surveys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    survey_type VARCHAR(40) NOT NULL,
    answers_json JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_surveys_user ON surveys(user_id);
CREATE INDEX IF NOT EXISTS idx_surveys_created_at ON surveys(created_at);

CREATE TABLE IF NOT EXISTS user_aha_cache (
    user_id BIGINT PRIMARY KEY,
    first_aha_at TIMESTAMP NULL,
    t2v_seconds INT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- PLANS & SUBSCRIPTIONS (V25)
-- =========================
CREATE TABLE IF NOT EXISTS plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(80) NOT NULL,
    price_cents INT NOT NULL,
    currency VARCHAR(8) NOT NULL DEFAULT 'EUR',
    period VARCHAR(16) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_plans_code ON plans(code);

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    status VARCHAR(24) NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ends_at TIMESTAMP NULL
);
ALTER TABLE subscriptions ADD CONSTRAINT IF NOT EXISTS fk_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans(id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_user_status ON subscriptions(user_id, status);

CREATE TABLE IF NOT EXISTS referral_fraud_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    signal VARCHAR(64) NOT NULL,
    details VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_referral_fraud_user ON referral_fraud_audit(user_id);

-- =========================
-- EMAIL SENDS (V55)
-- =========================
CREATE TABLE IF NOT EXISTS email_sends (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_code VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    rendered_body TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_email_sends_user ON email_sends(user_id);
CREATE INDEX IF NOT EXISTS idx_email_sends_template ON email_sends(template_code);

-- =========================
-- MONETIZACION (V70) simplified (no triggers)
-- =========================
CREATE TABLE IF NOT EXISTS monetizacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_type VARCHAR(32) DEFAULT 'free',
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_monetizacion_user_id ON monetizacion(user_id);
CREATE INDEX IF NOT EXISTS idx_monetizacion_plan_type ON monetizacion(plan_type);

-- =========================
-- Consolidated V1__init_schema.sql
-- This file consolidates all V* migrations into a single initial schema file.
-- It was generated to provide a single, canonical CREATE-schema for new databases and tests.
-- WARNING: This replaces the initial schema migration file. The old incremental migration files
-- have been removed as requested. Keep backups if you need to preserve historical diffs.

SET FOREIGN_KEY_CHECKS=0;

-- =========================
-- TABLE: usuarios
-- =========================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    email_verificado BOOLEAN DEFAULT FALSE,
    acepta_terminos BOOLEAN DEFAULT FALSE,
    acepta_privacidad BOOLEAN DEFAULT FALSE,
    acepta_marketing BOOLEAN DEFAULT FALSE,
    perfil_publico BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP NULL,
    ip_registro VARCHAR(45),
    deleted_at TIMESTAMP NULL
);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_estado ON usuarios(estado);
CREATE INDEX IF NOT EXISTS idx_usuarios_fecha_creacion ON usuarios(fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_usuarios_deleted_at ON usuarios(deleted_at);

-- =========================
-- TABLE: perfiles_usuario
-- =========================
CREATE TABLE IF NOT EXISTS perfiles_usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    avatar_url VARCHAR(500),
    biografia TEXT,
    profesion VARCHAR(150),
    nivel_experiencia VARCHAR(32) DEFAULT 'PRINCIPIANTE',
    puntos_totales INT DEFAULT 0,
    retos_completados INT DEFAULT 0,
    racha_actual INT DEFAULT 0,
    racha_maxima INT DEFAULT 0,
    notif_email_retos BOOLEAN DEFAULT TRUE,
    notif_email_validaciones BOOLEAN DEFAULT TRUE,
    notif_push_recordatorios BOOLEAN DEFAULT TRUE,
    notif_sms_criticas BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE perfiles_usuario ADD CONSTRAINT IF NOT EXISTS fk_perfiles_usuario_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS idx_perfiles_usuario_usuario_id ON perfiles_usuario(usuario_id);
CREATE INDEX IF NOT EXISTS idx_perfiles_usuario_puntos ON perfiles_usuario(puntos_totales);

-- =========================
-- TABLE: categorias
-- =========================
CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    icono VARCHAR(50),
    color_hex VARCHAR(7) DEFAULT '#3B82F6',
    activa BOOLEAN DEFAULT TRUE,
    orden_visualizacion INT DEFAULT 0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_categorias_nombre ON categorias(nombre);
CREATE INDEX IF NOT EXISTS idx_categorias_activa ON categorias(activa);
CREATE INDEX IF NOT EXISTS idx_categorias_orden ON categorias(orden_visualizacion);

-- =========================
-- TABLE: auditoria
-- =========================
CREATE TABLE IF NOT EXISTS auditoria (
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
    tipo_accion VARCHAR(32) NOT NULL,
    nivel_critico VARCHAR(16) DEFAULT 'BAJO',
    fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE auditoria ADD CONSTRAINT IF NOT EXISTS fk_auditoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario_id ON auditoria(usuario_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha_accion ON auditoria(fecha_accion);
CREATE INDEX IF NOT EXISTS idx_auditoria_tipo_accion ON auditoria(tipo_accion);
CREATE INDEX IF NOT EXISTS idx_auditoria_nivel_critico ON auditoria(nivel_critico);
CREATE INDEX IF NOT EXISTS idx_auditoria_tabla_afectada ON auditoria(tabla_afectada);

-- =========================
-- RETOS + PARTICIPACIONES + EVIDENCIAS + VALIDACIONES + LOGROS
-- =========================
CREATE TABLE IF NOT EXISTS retos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    categoria_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    dificultad VARCHAR(32) NOT NULL,
    duracion_dias INT NOT NULL DEFAULT 7,
    puntos_recompensa INT NOT NULL DEFAULT 100,
    requisitos_previos TEXT,
    criterios_validacion TEXT NOT NULL,
    tipo_evidencia VARCHAR(32) DEFAULT 'CUALQUIERA',
    es_privado BOOLEAN DEFAULT FALSE,
    requiere_aprobacion BOOLEAN DEFAULT FALSE,
    max_participantes INT NULL,
    estado VARCHAR(32) DEFAULT 'BORRADOR',
    fecha_inicio DATE,
    fecha_fin DATE,
    tags JSON,
    participantes_actuales INT DEFAULT 0,
    validaciones_pendientes INT DEFAULT 0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);
ALTER TABLE retos ADD CONSTRAINT IF NOT EXISTS fk_retos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id);
ALTER TABLE retos ADD CONSTRAINT IF NOT EXISTS fk_retos_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id);
CREATE INDEX IF NOT EXISTS idx_retos_categoria ON retos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_retos_autor ON retos(autor_id);
CREATE INDEX IF NOT EXISTS idx_retos_estado ON retos(estado);
CREATE INDEX IF NOT EXISTS idx_retos_dificultad ON retos(dificultad);
CREATE INDEX IF NOT EXISTS idx_retos_fecha_inicio ON retos(fecha_inicio);
CREATE INDEX IF NOT EXISTS idx_retos_privado ON retos(es_privado);
CREATE INDEX IF NOT EXISTS idx_retos_deleted_at ON retos(deleted_at);
CREATE INDEX IF NOT EXISTS idx_retos_busqueda_titulo_descripcion ON retos(titulo, descripcion);

CREATE TABLE IF NOT EXISTS participaciones_reto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(32) DEFAULT 'INSCRITO',
    progreso_porcentaje DECIMAL(5,2) DEFAULT 0.00,
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio TIMESTAMP NULL,
    fecha_completado TIMESTAMP NULL,
    fecha_ultima_actividad TIMESTAMP NULL,
    dias_activos INT DEFAULT 0,
    evidencias_enviadas INT DEFAULT 0,
    evidencias_validadas INT DEFAULT 0,
    puntos_obtenidos INT DEFAULT 0,
    notas_privadas TEXT,
    motivacion TEXT
);
ALTER TABLE participaciones_reto ADD CONSTRAINT IF NOT EXISTS fk_participaciones_reto_reto FOREIGN KEY (reto_id) REFERENCES retos(id) ON DELETE CASCADE;
ALTER TABLE participaciones_reto ADD CONSTRAINT IF NOT EXISTS fk_participaciones_reto_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
CREATE UNIQUE INDEX IF NOT EXISTS unique_participacion ON participaciones_reto(reto_id, usuario_id);
CREATE INDEX IF NOT EXISTS idx_participaciones_reto_reto_id ON participaciones_reto(reto_id);
CREATE INDEX IF NOT EXISTS idx_participaciones_reto_usuario_id ON participaciones_reto(usuario_id);
CREATE INDEX IF NOT EXISTS idx_participaciones_reto_estado ON participaciones_reto(estado);
CREATE INDEX IF NOT EXISTS idx_participaciones_reto_fecha_inscripcion ON participaciones_reto(fecha_inscripcion);

CREATE TABLE IF NOT EXISTS evidencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    participacion_id BIGINT NOT NULL,
    reto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(32) NOT NULL,
    titulo VARCHAR(200),
    descripcion TEXT NOT NULL,
    contenido_url VARCHAR(1000),
    contenido_texto TEXT,
    nombre_archivo VARCHAR(255),
    tipo_mime VARCHAR(100),
    tamano_bytes BIGINT,
    estado VARCHAR(32) DEFAULT 'PENDIENTE',
    validador_id BIGINT NULL,
    fecha_validacion TIMESTAMP NULL,
    comentarios_validacion TEXT,
    puntos_otorgados INT DEFAULT 0,
    ip_envio VARCHAR(45),
    metadata JSON,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);
ALTER TABLE evidencias ADD CONSTRAINT IF NOT EXISTS fk_evidencias_participacion FOREIGN KEY (participacion_id) REFERENCES participaciones_reto(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT IF NOT EXISTS fk_evidencias_reto FOREIGN KEY (reto_id) REFERENCES retos(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT IF NOT EXISTS fk_evidencias_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT IF NOT EXISTS fk_evidencias_validador FOREIGN KEY (validador_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_evidencias_participacion ON evidencias(participacion_id);
CREATE INDEX IF NOT EXISTS idx_evidencias_reto ON evidencias(reto_id);
CREATE INDEX IF NOT EXISTS idx_evidencias_usuario ON evidencias(usuario_id);
CREATE INDEX IF NOT EXISTS idx_evidencias_validador ON evidencias(validador_id);
CREATE INDEX IF NOT EXISTS idx_evidencias_estado ON evidencias(estado);
CREATE INDEX IF NOT EXISTS idx_evidencias_tipo ON evidencias(tipo);
CREATE INDEX IF NOT EXISTS idx_evidencias_fecha_creacion ON evidencias(fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_evidencias_deleted_at ON evidencias(deleted_at);

CREATE TABLE IF NOT EXISTS validaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evidencia_id BIGINT NOT NULL,
    validador_id BIGINT NOT NULL,
    decision VARCHAR(32) NOT NULL,
    puntos_asignados INT DEFAULT 0,
    comentarios TEXT,
    criterios_cumplidos JSON,
    calidad_evidencia VARCHAR(32),
    tiempo_revision_minutos INT,
    requiere_segunda_opinion BOOLEAN DEFAULT FALSE,
    validador_secundario_id BIGINT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_validacion TIMESTAMP NULL
);
ALTER TABLE validaciones ADD CONSTRAINT IF NOT EXISTS fk_validaciones_evidencia FOREIGN KEY (evidencia_id) REFERENCES evidencias(id) ON DELETE CASCADE;
ALTER TABLE validaciones ADD CONSTRAINT IF NOT EXISTS fk_validaciones_validador FOREIGN KEY (validador_id) REFERENCES usuarios(id) ON DELETE CASCADE;
ALTER TABLE validaciones ADD CONSTRAINT IF NOT EXISTS fk_validaciones_validador_secundario FOREIGN KEY (validador_secundario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_validaciones_evidencia ON validaciones(evidencia_id);
CREATE INDEX IF NOT EXISTS idx_validaciones_validador ON validaciones(validador_id);
CREATE INDEX IF NOT EXISTS idx_validaciones_decision ON validaciones(decision);
CREATE INDEX IF NOT EXISTS idx_validaciones_fecha_validacion ON validaciones(fecha_validacion);

CREATE TABLE IF NOT EXISTS logros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    icono VARCHAR(100),
    categoria VARCHAR(50),
    tipo_logro VARCHAR(64) NOT NULL,
    valor_objetivo INT,
    es_secreto BOOLEAN DEFAULT FALSE,
    puntos_bonus INT DEFAULT 0,
    es_repetible BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_logros_nombre ON logros(nombre);
CREATE INDEX IF NOT EXISTS idx_logros_tipo ON logros(tipo_logro);

CREATE TABLE IF NOT EXISTS usuario_logros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    logro_id BIGINT NOT NULL,
    fecha_obtencion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reto_asociado_id BIGINT NULL,
    valor_alcanzado INT,
    es_visible BOOLEAN DEFAULT TRUE
);
ALTER TABLE usuario_logros ADD CONSTRAINT IF NOT EXISTS fk_usuario_logros_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
ALTER TABLE usuario_logros ADD CONSTRAINT IF NOT EXISTS fk_usuario_logros_logro FOREIGN KEY (logro_id) REFERENCES logros(id) ON DELETE CASCADE;
ALTER TABLE usuario_logros ADD CONSTRAINT IF NOT EXISTS fk_usuario_logros_reto FOREIGN KEY (reto_asociado_id) REFERENCES retos(id) ON DELETE SET NULL;
CREATE UNIQUE INDEX IF NOT EXISTS unique_usuario_logro ON usuario_logros(usuario_id, logro_id);
CREATE INDEX IF NOT EXISTS idx_logros_usuario ON usuario_logros(usuario_id);
CREATE INDEX IF NOT EXISTS idx_logros_logro ON usuario_logros(logro_id);
CREATE INDEX IF NOT EXISTS idx_logros_fecha_obtencion ON usuario_logros(fecha_obtencion);

-- =========================
-- EVENTS & SURVEYS (from V10)
-- =========================
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    event_type VARCHAR(100) NOT NULL,
    properties JSON NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    session_id VARCHAR(100) NULL,
    source VARCHAR(50) NULL
);
CREATE INDEX IF NOT EXISTS idx_events_user_type ON events(user_id, event_type);
CREATE INDEX IF NOT EXISTS idx_events_ts ON events(timestamp);

CREATE TABLE IF NOT EXISTS surveys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    survey_type VARCHAR(40) NOT NULL,
    answers_json JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_surveys_user ON surveys(user_id);
CREATE INDEX IF NOT EXISTS idx_surveys_created_at ON surveys(created_at);

CREATE TABLE IF NOT EXISTS user_aha_cache (
    user_id BIGINT PRIMARY KEY,
    first_aha_at TIMESTAMP NULL,
    t2v_seconds INT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- PLANS & SUBSCRIPTIONS (from V25)
-- =========================
CREATE TABLE IF NOT EXISTS plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(80) NOT NULL,
    price_cents INT NOT NULL,
    currency VARCHAR(8) NOT NULL DEFAULT 'EUR',
    period VARCHAR(16) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_plans_code ON plans(code);

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    status VARCHAR(24) NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ends_at TIMESTAMP NULL
);
ALTER TABLE subscriptions ADD CONSTRAINT IF NOT EXISTS fk_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans(id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_user_status ON subscriptions(user_id, status);

CREATE TABLE IF NOT EXISTS referral_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    code VARCHAR(32) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS referral_uses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code_id BIGINT NOT NULL,
    referred_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE referral_uses ADD CONSTRAINT IF NOT EXISTS fk_referral_code FOREIGN KEY (code_id) REFERENCES referral_codes(id);

CREATE TABLE IF NOT EXISTS referral_fraud_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    signal VARCHAR(64) NOT NULL,
    details VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_referral_fraud_user ON referral_fraud_audit(user_id);

-- =========================
-- STRIPE WEBHOOKS (V26)
-- =========================
CREATE TABLE IF NOT EXISTS stripe_webhook_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id VARCHAR(80) NOT NULL UNIQUE,
    type VARCHAR(80) NOT NULL,
    payload TEXT NOT NULL,
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- SUPPORT, NPS, CSAT, ECONOMICS (V30)
-- =========================
CREATE TABLE IF NOT EXISTS support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subject VARCHAR(160) NOT NULL,
    body TEXT NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS nps_responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    score INT NOT NULL,
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS csat_responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    score INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS economics_counters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    counter_value BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- VALIDATORS & VALIDATIONS (V35)
-- =========================
CREATE TABLE IF NOT EXISTS validators (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    expertise VARCHAR(120),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS validations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reto_id BIGINT NOT NULL,
    validator_id BIGINT NOT NULL,
    status VARCHAR(24) NOT NULL,
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- NOTIFICATIONS (V3)
-- =========================
CREATE TABLE IF NOT EXISTS notification_prefs (
  user_id BIGINT PRIMARY KEY,
  allow_email BOOLEAN DEFAULT TRUE,
  allow_push BOOLEAN DEFAULT TRUE,
  max_per_day INT DEFAULT 2,
  max_per_week INT DEFAULT 6,
  quiet_hours_start TIME DEFAULT '20:00:00',
  quiet_hours_end TIME DEFAULT '09:00:00',
  tz VARCHAR(40) DEFAULT 'Europe/Madrid',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  channel VARCHAR(20) NOT NULL,
  type VARCHAR(50) NOT NULL,
  sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_notif_user_day ON notification_log(user_id, sent_at);
CREATE INDEX IF NOT EXISTS idx_notif_type ON notification_log(type);

-- =========================
-- PRIVACY, EMAIL TEMPLATES, PRESS COUNTER (V45)
-- =========================
CREATE TABLE IF NOT EXISTS privacy_consents (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  policy_version VARCHAR(32) NOT NULL,
  granted BOOLEAN NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS email_templates (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  subject VARCHAR(160) NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS press_live_counter (
  id INT PRIMARY KEY,
  counter_value BIGINT NOT NULL
);
-- initialize single-row counter if not present (MySQL syntax adapted for Flyway)
INSERT INTO press_live_counter (id, counter_value) SELECT 1, 0 FROM (SELECT 1) AS tmp WHERE NOT EXISTS (SELECT 1 FROM press_live_counter WHERE id=1);

-- =========================
-- RISK / CRISIS / INCIDENTS (V50)
-- =========================
CREATE TABLE IF NOT EXISTS incidents (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  status VARCHAR(32) NOT NULL,
  message VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS crisis_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- INDEXES & CONSTRAINTS (V51)
-- =========================
CREATE INDEX IF NOT EXISTS idx_events_user_type_ts ON events(user_id, event_type, timestamp);
CREATE INDEX IF NOT EXISTS idx_surveys_created_at ON surveys(created_at);
CREATE INDEX IF NOT EXISTS idx_validations_evidence ON validations(reto_id);
CREATE INDEX IF NOT EXISTS idx_validations_validator ON validations(validator_id);
CREATE INDEX IF NOT EXISTS idx_validations_status ON validations(status);
CREATE INDEX IF NOT EXISTS idx_subscriptions_user_status ON subscriptions(user_id, status);
CREATE INDEX IF NOT EXISTS idx_support_user_status ON support_tickets(user_id, status);
CREATE INDEX IF NOT EXISTS idx_nps_created_at ON nps_responses(created_at);
CREATE INDEX IF NOT EXISTS idx_csat_created_at ON csat_responses(created_at);
CREATE INDEX IF NOT EXISTS idx_econ_updated_at ON economics_counters(updated_at);
CREATE INDEX IF NOT EXISTS idx_notif_user_type ON notification_log(user_id, type);

-- =========================
-- INVITES & VALIDATOR INVITES (V52)
-- =========================
CREATE TABLE IF NOT EXISTS invites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  referrer_id BIGINT NOT NULL,
  channel VARCHAR(40) NOT NULL,
  target VARCHAR(255),
  code VARCHAR(64) UNIQUE NOT NULL,
  accepted BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_invites_code ON invites(code);

CREATE TABLE IF NOT EXISTS validator_invites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  token VARCHAR(128) UNIQUE NOT NULL,
  invited_by BIGINT NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_validator_invites_email ON validator_invites(email);

-- =========================
-- AUTH TOKENS (V53)
-- =========================
CREATE TABLE IF NOT EXISTS auth_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(64) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE auth_tokens ADD CONSTRAINT IF NOT EXISTS fk_auth_tokens_usuario FOREIGN KEY (user_id) REFERENCES usuarios(id);
CREATE INDEX IF NOT EXISTS idx_auth_tokens_user ON auth_tokens(user_id);

-- =========================
-- REFERRAL REWARD FLAG / REWARD COLUMNS (V54)
-- =========================
ALTER TABLE IF EXISTS referral_codes
    ADD COLUMN IF NOT EXISTS reward_released BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE IF EXISTS referral_codes
    ADD COLUMN IF NOT EXISTS reward_released_at TIMESTAMP NULL;
CREATE INDEX IF NOT EXISTS idx_referral_codes_reward ON referral_codes(reward_released);

-- =========================
-- EMAIL SENDS (V55)
-- =========================
CREATE TABLE IF NOT EXISTS email_sends (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_code VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    rendered_body TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_email_sends_user ON email_sends(user_id);
CREATE INDEX IF NOT EXISTS idx_email_sends_template ON email_sends(template_code);

-- =========================
-- MONETIZATION (V70) - adapted
-- =========================
CREATE TABLE IF NOT EXISTS monetizacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_type VARCHAR(32) DEFAULT 'free',
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_monetizacion_user_id ON monetizacion(user_id);
CREATE INDEX IF NOT EXISTS idx_monetizacion_plan_type ON monetizacion(plan_type);

-- =========================
-- FILE MOVED TO TEST RESOURCES. See backend/src/test/resources/db/migration-test/V1__init_schema.sql
-- To restore to main resources for production, rename the .bak file back to V1__init_schema.sql
--   These have been adapted to generic types or simplified to ensure the consolidated script

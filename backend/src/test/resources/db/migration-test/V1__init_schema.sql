-- V1__init_schema.sql (TEST consolidated)
-- Consolidated schema for test profile (H2-compatible).
-- This file is a full, single-schema migration intended for the `test` profile only.

-- =========================
-- TABLA: usuarios
-- =========================
CREATE TABLE usuarios (
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
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);
CREATE INDEX idx_usuarios_fecha_creacion ON usuarios(fecha_creacion);
CREATE INDEX idx_usuarios_deleted_at ON usuarios(deleted_at);

-- =========================
-- TABLA: perfiles_usuario
-- =========================
CREATE TABLE perfiles_usuario (
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
ALTER TABLE perfiles_usuario ADD CONSTRAINT fk_perfiles_usuario_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
CREATE INDEX idx_perfiles_usuario_usuario_id ON perfiles_usuario(usuario_id);
CREATE INDEX idx_perfiles_usuario_puntos ON perfiles_usuario(puntos_totales);

-- =========================
-- TABLA: categorias
-- =========================
CREATE TABLE categorias (
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
CREATE UNIQUE INDEX idx_categorias_nombre ON categorias(nombre);
CREATE INDEX idx_categorias_activa ON categorias(activa);
CREATE INDEX idx_categorias_orden ON categorias(orden_visualizacion);

-- =========================
-- TABLA: auditoria
-- =========================
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
    tipo_accion VARCHAR(32) NOT NULL,
    nivel_critico VARCHAR(16) DEFAULT 'BAJO',
    fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE auditoria ADD CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX idx_auditoria_usuario_id ON auditoria(usuario_id);
CREATE INDEX idx_auditoria_fecha_accion ON auditoria(fecha_accion);
CREATE INDEX idx_auditoria_tipo_accion ON auditoria(tipo_accion);
CREATE INDEX idx_auditoria_nivel_critico ON auditoria(nivel_critico);
CREATE INDEX idx_auditoria_tabla_afectada ON auditoria(tabla_afectada);

-- =========================
-- TABLAS DE RETOS (retos, participaciones_reto, evidencias, validaciones, logros)
-- =========================
CREATE TABLE retos (
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
ALTER TABLE retos ADD CONSTRAINT fk_retos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id);
ALTER TABLE retos ADD CONSTRAINT fk_retos_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id);
CREATE INDEX idx_retos_categoria ON retos(categoria_id);
CREATE INDEX idx_retos_autor ON retos(autor_id);
CREATE INDEX idx_retos_estado ON retos(estado);
CREATE INDEX idx_retos_dificultad ON retos(dificultad);
CREATE INDEX idx_retos_fecha_inicio ON retos(fecha_inicio);
CREATE INDEX idx_retos_privado ON retos(es_privado);
CREATE INDEX idx_retos_deleted_at ON retos(deleted_at);
CREATE INDEX idx_retos_busqueda_titulo_descripcion ON retos(titulo, descripcion);

CREATE TABLE participaciones_reto (
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
ALTER TABLE participaciones_reto ADD CONSTRAINT fk_participaciones_reto_reto FOREIGN KEY (reto_id) REFERENCES retos(id) ON DELETE CASCADE;
ALTER TABLE participaciones_reto ADD CONSTRAINT fk_participaciones_reto_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
CREATE UNIQUE INDEX unique_participacion ON participaciones_reto(reto_id, usuario_id);
CREATE INDEX idx_participaciones_reto_reto_id ON participaciones_reto(reto_id);
CREATE INDEX idx_participaciones_reto_usuario_id ON participaciones_reto(usuario_id);
CREATE INDEX idx_participaciones_reto_estado ON participaciones_reto(estado);
CREATE INDEX idx_participaciones_reto_fecha_inscripcion ON participaciones_reto(fecha_inscripcion);

CREATE TABLE evidencias (
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
ALTER TABLE evidencias ADD CONSTRAINT fk_evidencias_participacion FOREIGN KEY (participacion_id) REFERENCES participaciones_reto(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT fk_evidencias_reto FOREIGN KEY (reto_id) REFERENCES retos(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT fk_evidencias_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
ALTER TABLE evidencias ADD CONSTRAINT fk_evidencias_validador FOREIGN KEY (validador_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX idx_evidencias_participacion ON evidencias(participacion_id);
CREATE INDEX idx_evidencias_reto ON evidencias(reto_id);
CREATE INDEX idx_evidencias_usuario ON evidencias(usuario_id);
CREATE INDEX idx_evidencias_validador ON evidencias(validador_id);
CREATE INDEX idx_evidencias_estado ON evidencias(estado);
CREATE INDEX idx_evidencias_tipo ON evidencias(tipo);
CREATE INDEX idx_evidencias_fecha_creacion ON evidencias(fecha_creacion);
CREATE INDEX idx_evidencias_deleted_at ON evidencias(deleted_at);

CREATE TABLE validaciones (
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
ALTER TABLE validaciones ADD CONSTRAINT fk_validaciones_evidencia FOREIGN KEY (evidencia_id) REFERENCES evidencias(id) ON DELETE CASCADE;
ALTER TABLE validaciones ADD CONSTRAINT fk_validaciones_validador FOREIGN KEY (validador_id) REFERENCES usuarios(id) ON DELETE CASCADE;
ALTER TABLE validaciones ADD CONSTRAINT fk_validaciones_validador_secundario FOREIGN KEY (validador_secundario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX idx_validaciones_evidencia ON validaciones(evidencia_id);
CREATE INDEX idx_validaciones_validador ON validaciones(validador_id);
CREATE INDEX idx_validaciones_decision ON validaciones(decision);
CREATE INDEX idx_validaciones_fecha_validacion ON validaciones(fecha_validacion);

CREATE TABLE logros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    icono VARCHAR(100),
    categoria VARCHAR(50),
    tipo_logro VARCHAR(64) NOT NULL,
    valor_objetivo INT,
    es_secreto BOOLEAN DEFAULT FALSE
);
CREATE UNIQUE INDEX idx_logros_nombre ON logros(nombre);

-- =========================
-- EVENTS & SURVEYS (V10)
-- =========================
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    event_type VARCHAR(100) NOT NULL,
    properties JSON NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    session_id VARCHAR(100) NULL,
    source VARCHAR(50) NULL
);
CREATE INDEX idx_events_user_type ON events(user_id, event_type);
CREATE INDEX idx_events_ts ON events(timestamp);

CREATE TABLE surveys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    survey_type VARCHAR(40) NOT NULL,
    answers_json JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_surveys_user ON surveys(user_id);
CREATE INDEX idx_surveys_created_at ON surveys(created_at);

CREATE TABLE user_aha_cache (
    user_id BIGINT PRIMARY KEY,
    first_aha_at TIMESTAMP NULL,
    t2v_seconds INT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- PLANS & SUBSCRIPTIONS (V25)
-- =========================
CREATE TABLE plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(80) NOT NULL,
    price_cents INT NOT NULL,
    currency VARCHAR(8) NOT NULL DEFAULT 'EUR',
    period VARCHAR(16) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX idx_plans_code ON plans(code);

CREATE TABLE subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    status VARCHAR(24) NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ends_at TIMESTAMP NULL
);
ALTER TABLE subscriptions ADD CONSTRAINT fk_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans(id);
CREATE INDEX idx_subscriptions_user_status ON subscriptions(user_id, status);

CREATE TABLE referral_fraud_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    signal VARCHAR(64) NOT NULL,
    details VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_referral_fraud_user ON referral_fraud_audit(user_id);

-- =========================
-- EMAIL SENDS (V55)
-- =========================
CREATE TABLE email_sends (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_code VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    rendered_body TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_email_sends_user ON email_sends(user_id);
CREATE INDEX idx_email_sends_template ON email_sends(template_code);

-- =========================
-- MONETIZACION (V70) simplified (no triggers)
-- =========================
CREATE TABLE monetizacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_type VARCHAR(32) DEFAULT 'free',
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_monetizacion_user_id ON monetizacion(user_id);
CREATE INDEX idx_monetizacion_plan_type ON monetizacion(plan_type);

-- =========================
-- ADICIONALES: índices y constraints recomendados (desde V51)
-- =========================
CREATE INDEX idx_events_user_type_ts ON events(user_id, event_type, timestamp);
CREATE INDEX idx_subscriptions_user_status ON subscriptions(user_id, status);

-- =========================
-- DATOS INICIALES (ejemplo mínimo)
-- =========================
INSERT INTO categorias (nombre, descripcion, icono, color_hex, orden_visualizacion)
    SELECT 'Fitness y Salud', 'Retos relacionados con ejercicio físico y bienestar', '', '#10B981', 1
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Fitness y Salud');

INSERT INTO usuarios (email, password_hash, nombre, apellidos, estado, rol, email_verificado, acepta_terminos, acepta_privacidad, ip_registro)
    SELECT 'admin@impulse.dev', '$2a$10$example_hash_here', 'Admin', 'IMPULSE', 'ACTIVO', 'ADMIN', TRUE, TRUE, TRUE, '127.0.0.1'
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@impulse.dev');

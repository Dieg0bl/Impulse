-- =====================================================================
-- ADVERTENCIA: Este archivo es el script maestro autocontenido del modelo IMPULSE.
-- No debe ser editado automáticamente por asistentes, scripts ni herramientas externas.
-- Solo el usuario responsable puede modificarlo manualmente desde el IDE.
-- Cualquier cambio automático será considerado inválido salvo petición explícita del usuario.

-- 0) Base de datos y usuario (principio de mínimo privilegio)
CREATE DATABASE IF NOT EXISTS impulse
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE impulse;

-- Conexión/Session hardening
SET SESSION time_zone = '+00:00';
SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';
SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'impulse_app'@'localhost' IDENTIFIED BY 'impulse_secure_2025!';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, CREATE VIEW, TRIGGER
ON impulse.* TO 'impulse_app'@'localhost';
FLUSH PRIVILEGES;

-- Para crear todo sin chocar FKs durante bootstrap
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 1) Núcleo de identidad y catálogos
-- =====================================================================

-- Usuario (privacidad por diseño + canónico + futuro rehash)
CREATE TABLE IF NOT EXISTS Usuario (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  email              VARCHAR(254) NOT NULL,
  email_norm         VARCHAR(254) GENERATED ALWAYS AS (LOWER(TRIM(email))) STORED,
  email_hash         CHAR(64) NULL,                         -- SHA-256 con pepper (app)
  email_cipher       VARBINARY(384) NULL,                   -- AES-GCM (app/KMS)
  password_hash      CHAR(60) NOT NULL,                     -- bcrypt
  password_algo      VARCHAR(16) NOT NULL DEFAULT 'bcrypt',
  needs_rehash       TINYINT(1) NOT NULL DEFAULT 0,
  nombre             VARCHAR(100) NOT NULL,
  apellidos          VARCHAR(150) NULL,
  photo_url          VARCHAR(512) NULL,
  phone              VARCHAR(32) NULL,
  phone_hash         CHAR(64) NULL,
  phone_cipher       VARBINARY(384) NULL,
  locale             VARCHAR(16) NOT NULL DEFAULT 'es-ES',
  timezone           VARCHAR(64) NOT NULL DEFAULT 'Europe/Madrid',
  estado             ENUM('ACTIVO','INACTIVO','SUSPENDIDO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  default_visibility ENUM('private','validators','public') NOT NULL DEFAULT 'private',
  marketing_opt_in   BOOLEAN NOT NULL DEFAULT FALSE,
  verified_at        DATETIME(3) NULL,
  created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at         DATETIME(3) NULL,
  CONSTRAINT chk_usuario_deleted_at CHECK (estado <> 'ELIMINADO' OR deleted_at IS NOT NULL),
  UNIQUE KEY uniq_usuario_email (email),
  UNIQUE KEY uniq_usuario_email_norm (email_norm),
  KEY idx_usuario_estado_created (estado, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Perfil validador (bio)
CREATE TABLE IF NOT EXISTS ValidadorPerfil (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT NOT NULL,
  bio         VARCHAR(280),
  created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_validador_user (user_id),
  CONSTRAINT fk_validador_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Categorías de reto
CREATE TABLE IF NOT EXISTS CategoriaReto (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(120) NOT NULL UNIQUE,
  descripcion  VARCHAR(255) NULL,
  created_at   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 2) Dominio Retos / Evidencias / Validación / Notificaciones
-- =====================================================================

-- Reto
CREATE TABLE IF NOT EXISTS Reto (
  id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
  idCreador             BIGINT NOT NULL,
  idCategoria           BIGINT NOT NULL,
  titulo                VARCHAR(200) NOT NULL,
  descripcion           TEXT NOT NULL,

  -- Fechas
  fechaInicio           DATETIME(3) NOT NULL,
  fechaFin              DATETIME(3) NOT NULL,

  -- Validación/config
  tipoValidacion        ENUM('MANUAL','AUTOMATICA','MIXTA') DEFAULT 'MANUAL',
  validadoresPermitidos ENUM('CUALQUIERA','SELECCIONADOS','NINGUNO') DEFAULT 'CUALQUIERA',
  dificultad            ENUM('BAJA','MEDIA','ALTA','EXTREMA') DEFAULT 'MEDIA',
  esPublico             BOOLEAN DEFAULT TRUE,
  requiereEvidencia     BOOLEAN DEFAULT TRUE,
  tipoEvidencia         ENUM('TEXTO','FOTO','VIDEO','AUDIO','FOTO_Y_TEXTO','VIDEO_Y_TEXTO','TEXTO_Y_AUDIO') DEFAULT 'FOTO_Y_TEXTO',
  frecuenciaReporte     ENUM('DIARIO','SEMANAL','QUINCENAL','MENSUAL','LIBRE') DEFAULT 'LIBRE',

  -- Objetivos
  metaObjetivo          VARCHAR(200),
  unidadMedida          VARCHAR(50),
  valorObjetivo         DECIMAL(10,2),

  -- Estado/Control
  estado                ENUM('BORRADOR','PENDIENTE','ACTIVO','PAUSADO','COMPLETADO','CANCELADO','ELIMINADO','ARCHIVADO','FALLIDO') DEFAULT 'BORRADOR',
  fechaCreacion         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  fechaModificacion     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  fechaPublicacion      DATETIME(3) NULL,

  -- Avanzado
  limiteParticipantes   INT DEFAULT NULL,
  edadMinima            INT DEFAULT NULL,
  edadMaxima            INT DEFAULT NULL,
  paisesPermitidos      JSON,
  etiquetas             JSON,
  recompensas           JSON,
  tipoConsecuencia      ENUM('social','reputation','economic','feedback','internal') NULL,
  esPlantilla           BOOLEAN NOT NULL DEFAULT FALSE,

  -- Visibilidad/auditoría
  visibility            ENUM('private','validators','public') NOT NULL DEFAULT 'private',
  updated_at            DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

  -- Índices y FKs
  INDEX idx_reto_creador (idCreador),
  INDEX idx_reto_categoria (idCategoria),
  INDEX idx_reto_estado (estado),
  INDEX idx_reto_fechas (fechaInicio, fechaFin),
  INDEX idx_reto_publico (esPublico),
  INDEX idx_reto_dificultad (dificultad),
  INDEX idx_reto_tipo_validacion (tipoValidacion),
  INDEX idx_visibility_status (visibility, estado),
  CONSTRAINT fk_reto_creador   FOREIGN KEY (idCreador)   REFERENCES Usuario(id)      ON DELETE CASCADE,
  CONSTRAINT fk_reto_categoria FOREIGN KEY (idCategoria) REFERENCES CategoriaReto(id) ON DELETE RESTRICT,
  public_slug           VARCHAR(128) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Invitaciones a validador (tokenizado)
CREATE TABLE IF NOT EXISTS validator_invites (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  inviter_id    BIGINT NOT NULL,
  invitee_email VARCHAR(254) NOT NULL,
  token         VARCHAR(64) NOT NULL UNIQUE,
  expires_at    DATETIME(3) NOT NULL,
  created_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  INDEX idx_inviter (inviter_id),
  CONSTRAINT fk_inviter_user FOREIGN KEY (inviter_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Asignación de validadores a retos
CREATE TABLE IF NOT EXISTS ValidadorReto (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  idReto          BIGINT NOT NULL,
  idValidador     BIGINT NOT NULL,
  fechaAsignacion DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  estado          ENUM('ACTIVO','INACTIVO','SUSPENDIDO') DEFAULT 'ACTIVO',
  tipoValidador   ENUM('PRINCIPAL','SECUNDARIO','ESPECIALISTA') DEFAULT 'PRINCIPAL',
  notasValidador  TEXT,
  metadatos       JSON,
  UNIQUE KEY uniq_validador_reto (idReto, idValidador),
  INDEX idx_validador_reto (idReto),
  INDEX idx_validador_usuario (idValidador),
  INDEX idx_validador_estado (estado),
  CONSTRAINT fk_valret_reto      FOREIGN KEY (idReto)      REFERENCES Reto(id)     ON DELETE CASCADE,
  CONSTRAINT fk_valret_validador FOREIGN KEY (idValidador) REFERENCES Usuario(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Acciones de validación (1 decisión por validador/reto)
CREATE TABLE IF NOT EXISTS Validacion (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  validator_id  BIGINT NOT NULL,
  challenge_id  BIGINT NOT NULL,  -- Reto.id
  status        ENUM('accepted','rejected') NOT NULL,
  comment       VARCHAR(512) NULL,
  created_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CHECK (status='accepted' OR (status='rejected' AND comment IS NOT NULL)),
  UNIQUE KEY uniq_validator_challenge (validator_id, challenge_id),
  KEY idx_valid_validator_created (validator_id, created_at),
  KEY idx_valid_challenge (challenge_id),
  KEY idx_valid_challenge_status (challenge_id, status, created_at),
  CONSTRAINT fk_valid_validator FOREIGN KEY (validator_id) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_valid_challenge FOREIGN KEY (challenge_id) REFERENCES Reto(id)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Participación
CREATE TABLE IF NOT EXISTS ParticipacionReto (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  idReto             BIGINT NOT NULL,
  idUsuario          BIGINT NOT NULL,
  fechaInscripcion   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  estado             ENUM('INSCRITO','ACTIVO','PAUSADO','COMPLETADO','ABANDONADO') DEFAULT 'INSCRITO',
  progreso           DECIMAL(5,2) DEFAULT 0.00, -- 0-100
  fechaCompletado    DATETIME(3) NULL,
  notasPersonales    TEXT,
  configuracionNotificaciones JSON,
  metadatos          JSON,
  UNIQUE KEY uniq_usuario_reto (idUsuario, idReto),
  INDEX idx_part_reto (idReto),
  INDEX idx_part_usuario (idUsuario),
  INDEX idx_part_estado (estado),
  INDEX idx_part_progreso (progreso),
  CONSTRAINT fk_part_reto    FOREIGN KEY (idReto)    REFERENCES Reto(id)     ON DELETE CASCADE,
  CONSTRAINT fk_part_usuario FOREIGN KEY (idUsuario) REFERENCES Usuario(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Evidencia
CREATE TABLE IF NOT EXISTS Evidencia (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  idReto               BIGINT NOT NULL,
  idUsuario            BIGINT NOT NULL,
  fechaReporte         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  tipoEvidencia        ENUM('TEXTO','FOTO','VIDEO','AUDIO','FOTO_Y_TEXTO','VIDEO_Y_TEXTO','TEXTO_Y_AUDIO') NOT NULL,
  kind                 ENUM('text','image','video','audio','mixed') NOT NULL DEFAULT 'mixed',
  descripcion          TEXT,
  archivoUrl           VARCHAR(500),
  archivoThumbnail     VARCHAR(500),
  coordenadasGPS       VARCHAR(50),
  valorReportado       DECIMAL(10,2),
  unidadMedida         VARCHAR(50),
  tiempoRegistrado     TIME,
  distanciaRegistrada  DECIMAL(10,2),
  estadoValidacion     ENUM('PENDIENTE','EN_REVISION','APROBADA','RECHAZADA','AUTOMATICA_APROBADA') DEFAULT 'PENDIENTE',
  fechaValidacion      DATETIME(3) NULL,
  idValidador          BIGINT NULL,
  comentarioValidador  TEXT,
  razonRechazo         TEXT,
  puntuacionValidador  DECIMAL(3,2),
  hashArchivo          VARCHAR(64),
  tamanoArchivo        BIGINT,
  metadatos            JSON,
  fechaCreacion        DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  fechaModificacion    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  INDEX idx_evid_reto (idReto),
  INDEX idx_evid_usuario (idUsuario),
  INDEX idx_evid_validador (idValidador),
  INDEX idx_evid_estado (estadoValidacion),
  INDEX idx_evid_fecha (fechaReporte),
  INDEX idx_evid_tipo (tipoEvidencia),
  INDEX idx_evid_kind (kind),
  CONSTRAINT fk_evid_reto      FOREIGN KEY (idReto)     REFERENCES Reto(id)     ON DELETE CASCADE,
  CONSTRAINT fk_evid_usuario   FOREIGN KEY (idUsuario)  REFERENCES Usuario(id)  ON DELETE CASCADE,
  CONSTRAINT fk_evid_validador FOREIGN KEY (idValidador) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Notificaciones + entregas por canal
CREATE TABLE IF NOT EXISTS NotificacionUsuario (
  id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
  idUsuario             BIGINT NOT NULL,
  tipo                  ENUM('RETO_NUEVO','EVIDENCIA_APROBADA','EVIDENCIA_RECHAZADA','VALIDACION_PENDIENTE','LOGRO_OBTENIDO','RECORDATORIO','SISTEMA') NOT NULL,
  titulo                VARCHAR(200) NOT NULL,
  mensaje               TEXT NOT NULL,
  leida                 BOOLEAN DEFAULT FALSE,
  requires_ack          BOOLEAN NOT NULL DEFAULT FALSE,
  fechaCreacion         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  fechaLectura          DATETIME(3) NULL,
  idReferenciaReto      BIGINT NULL,
  idReferenciaEvidencia BIGINT NULL,
  canal                 ENUM('APP','EMAIL','PUSH','SMS') DEFAULT 'APP',
  prioridad             ENUM('BAJA','MEDIA','ALTA','URGENTE') DEFAULT 'MEDIA',
  metadatos             JSON,
  INDEX idx_notif_usuario (idUsuario),
  INDEX idx_notif_leida (leida),
  INDEX idx_notif_tipo (tipo),
  INDEX idx_notif_fecha (fechaCreacion),
  INDEX idx_notif_prioridad (prioridad),
  INDEX idx_notif_canal (canal),
  KEY idx_notif_user_ack_created (idUsuario, requires_ack, fechaCreacion),
  CONSTRAINT fk_notif_usuario   FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_notif_reto      FOREIGN KEY (idReferenciaReto) REFERENCES Reto(id) ON DELETE SET NULL,
  CONSTRAINT fk_notif_evidencia FOREIGN KEY (idReferenciaEvidencia) REFERENCES Evidencia(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS notification_deliveries (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  notification_id  BIGINT NOT NULL,
  channel          ENUM('email','whatsapp','telegram','push','in_app') NOT NULL,
  status           ENUM('queued','sent','failed') NOT NULL,
  detail           VARCHAR(255) NULL,
  sent_at          DATETIME(3) NULL,
  KEY idx_notif (notification_id),
  CONSTRAINT fk_delivery_notification FOREIGN KEY (notification_id) REFERENCES NotificacionUsuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Logros y catálogo de badges
CREATE TABLE IF NOT EXISTS badges (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  code         VARCHAR(64) NOT NULL UNIQUE,
  name         VARCHAR(120) NOT NULL,
  description  VARCHAR(255) NULL,
  criteria     JSON NULL,
  active       BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS LogroUsuario (
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  idUsuario         BIGINT NOT NULL,
  tipoLogro         ENUM('RETO_COMPLETADO','RACHA_DIARIA','PRIMERA_EVIDENCIA','VALIDADOR_DESTACADO','MENTOR','CONSTANCIA','SUPERACION') NOT NULL,
  nombreLogro       VARCHAR(100) NOT NULL,
  descripcionLogro  TEXT,
  iconoLogro        VARCHAR(50),
  puntosObtenidos   INT DEFAULT 0,
  fechaObtencion    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  idRetoRelacionado BIGINT NULL,
  datosLogro        JSON,
  esPublico         BOOLEAN DEFAULT TRUE,
  badge_id          BIGINT NULL,
  INDEX idx_logro_usuario (idUsuario),
  INDEX idx_logro_tipo (tipoLogro),
  INDEX idx_logro_fecha (fechaObtencion),
  INDEX idx_logro_reto (idRetoRelacionado),
  CONSTRAINT fk_logro_usuario FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_logro_reto    FOREIGN KEY (idRetoRelacionado) REFERENCES Reto(id) ON DELETE SET NULL,
  CONSTRAINT fk_logro_badge   FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_badges (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT NOT NULL,
  badge_id    BIGINT NOT NULL,
  granted_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_user_badge (user_id, badge_id),
  KEY idx_user (user_id),
  CONSTRAINT fk_user_badges_user  FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_badges_badge FOREIGN KEY (badge_id) REFERENCES badges(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 3) Métricas de hábito / rachas
-- =====================================================================

CREATE TABLE IF NOT EXISTS habit_metrics (
  user_id        BIGINT PRIMARY KEY,
  last_action_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  streak_days    INT NOT NULL DEFAULT 0,
  updated_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT fk_habit_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE INDEX idx_habit_metrics_updated ON habit_metrics(updated_at);

CREATE TABLE IF NOT EXISTS habit_metrics_daily (
  user_id     BIGINT NOT NULL,
  day         DATE NOT NULL,
  submissions INT NOT NULL DEFAULT 0,
  accepted    INT NOT NULL DEFAULT 0,
  PRIMARY KEY (user_id, day),
  CONSTRAINT fk_hmd_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS habit_streaks (
  user_id              BIGINT PRIMARY KEY,
  current_streak_days  INT NOT NULL DEFAULT 0,
  longest_streak_days  INT NOT NULL DEFAULT 0,
  updated_at           DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT fk_hs_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 4) Cumplimiento / privacidad / moderación
-- =====================================================================

-- Versiones de consentimiento (alineada con ENUM de consents)
CREATE TABLE IF NOT EXISTS consent_versions (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  scope        ENUM('exposure_public','analytics','marketing','parental','other') NOT NULL,
  version      VARCHAR(32) NOT NULL,
  text_hash    CHAR(64) NOT NULL,
  published_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_scope_version (scope, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS consents (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT NOT NULL,
  scope       ENUM('exposure_public','analytics','marketing','parental','other') NOT NULL,
  decision    BOOLEAN NOT NULL,
  surface     VARCHAR(64) NOT NULL,
  version     VARCHAR(32) NOT NULL,
  ip          VARCHAR(64),
  ip_bin      VARBINARY(16) NULL,
  ua          VARCHAR(255),
  locale      VARCHAR(16),
  created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  revoked_at  DATETIME(3) NULL,
  INDEX idx_consents_user_scope (user_id, scope),
  KEY idx_consents_user_scope_ts (user_id, scope, created_at),
  CONSTRAINT fk_consents_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_consents_version FOREIGN KEY (scope, version) REFERENCES consent_versions(scope, version)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS visibility_changes (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  challenge_id     BIGINT NOT NULL,
  from_visibility  ENUM('private','validators','public') NOT NULL,
  to_visibility    ENUM('private','validators','public') NOT NULL,
  reason           VARCHAR(255),
  created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  INDEX idx_vis_user_challenge (user_id, challenge_id),
  CONSTRAINT fk_vis_user  FOREIGN KEY (user_id)      REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_vis_reto  FOREIGN KEY (challenge_id) REFERENCES Reto(id)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS privacy_request (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id   BIGINT NOT NULL,
  type      ENUM('access','rectification','erasure','restriction','portability','objection') NOT NULL,
  status    ENUM('received','in_progress','completed','rejected') NOT NULL DEFAULT 'received',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NULL,
  notes     TEXT,
  CONSTRAINT fk_privreq_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Moderación (genérico, polimórfico; sin FKs a contenido por tipos mixtos)
CREATE TABLE IF NOT EXISTS moderation_report (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  reporter_user_id    BIGINT NULL,
  target_content_id   BIGINT NOT NULL,
  target_content_type ENUM('challenge','evidence','comment','profile') NOT NULL,
  reason              ENUM('copyright','privacy','intimate','harassment','hate','other') NOT NULL,
  description         TEXT,
  url                 VARCHAR(512),
  created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  status              ENUM('received','triaged','actioned','dismissed') NOT NULL DEFAULT 'received',
  CONSTRAINT fk_mod_reporter FOREIGN KEY (reporter_user_id) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS moderation_action (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  report_id        BIGINT NOT NULL,
  action           ENUM('remove','restrict','suspend','no_action') NOT NULL,
  reason_code      VARCHAR(64) NOT NULL,
  statement        TEXT NOT NULL,
  notified_user_id BIGINT NOT NULL,
  created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  INDEX idx_mod_action_report (report_id),
  CONSTRAINT fk_mod_action_report FOREIGN KEY (report_id) REFERENCES moderation_report(id) ON DELETE CASCADE,
  CONSTRAINT fk_mod_action_user   FOREIGN KEY (notified_user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS moderation_appeal (
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  action_id         BIGINT NOT NULL,
  appellant_user_id BIGINT NOT NULL,
  message           TEXT NOT NULL,
  status            ENUM('received','upheld','reversed','partial') NOT NULL DEFAULT 'received',
  decided_at        DATETIME(3) NULL,
  created_at        DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  INDEX idx_mod_appeal_action (action_id),
  CONSTRAINT fk_mod_appeal_action FOREIGN KEY (action_id) REFERENCES moderation_action(id) ON DELETE CASCADE,
  CONSTRAINT fk_mod_appeal_user   FOREIGN KEY (appellant_user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Incidentes de seguridad y evaluación de brechas
CREATE TABLE IF NOT EXISTS security_incident (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  detected_at DATETIME(3) NOT NULL,
  severity    ENUM('low','medium','high') NOT NULL,
  description TEXT,
  scope       TEXT,
  contained_at DATETIME(3) NULL,
  root_cause  TEXT,
  status      ENUM('open','closed') NOT NULL DEFAULT 'open'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS breach_assessment (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  incident_id      BIGINT NOT NULL,
  risk_level       ENUM('none','low','medium','high') NOT NULL,
  dpia_impact      TEXT,
  notify_authority BOOLEAN NOT NULL DEFAULT 0,
  notify_subjects  BOOLEAN NOT NULL DEFAULT 0,
  assessed_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  INDEX idx_breach_incident (incident_id),
  CONSTRAINT fk_breach_incident FOREIGN KEY (incident_id) REFERENCES security_incident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS breach_notification (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  incident_id BIGINT NOT NULL,
  channel     ENUM('authority','email','in_app','other') NOT NULL,
  sent_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  content     TEXT,
  INDEX idx_notification_incident (incident_id),
  CONSTRAINT fk_breach_notif_incident FOREIGN KEY (incident_id) REFERENCES security_incident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- AMAR (DMA)
CREATE TABLE IF NOT EXISTS amar_metrics (
  id                           BIGINT AUTO_INCREMENT PRIMARY KEY,
  period_start                 DATE NOT NULL,
  period_end                   DATE NOT NULL,
  eu_monthly_active_recipients BIGINT NOT NULL,
  methodology                  TEXT,
  published_at                 DATETIME(3) NULL,
  UNIQUE KEY uniq_period (period_start, period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 5) Monetización (planes, suscripciones, pagos)
-- =====================================================================

CREATE TABLE IF NOT EXISTS plans (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  code         VARCHAR(64) NOT NULL UNIQUE,
  name         VARCHAR(128) NOT NULL,
  price_cents  BIGINT NOT NULL,
  interval     ENUM('month','year') NOT NULL,
  features     JSON,
  active       BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS subscriptions (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id            BIGINT NOT NULL,
  plan_id            BIGINT NOT NULL,
  status             ENUM('trial','active','unpaid','canceled') NOT NULL,
  trial_ends_at      DATETIME(3) NULL,
  current_period_end DATETIME(3) NOT NULL,
  cancel_at          DATETIME(3) NULL,
  grace_until        DATETIME(3) NULL,
  created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_sub_user (user_id),
  KEY idx_sub_status (status),
  CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_sub_plan FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS payments (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  subscription_id  BIGINT NOT NULL,
  amount_cents     BIGINT NOT NULL,
  currency         CHAR(3) NOT NULL,
  status           ENUM('succeeded','failed','refunded') NOT NULL,
  provider         ENUM('stripe') NOT NULL,
  provider_id      VARCHAR(128) NOT NULL,
  receipt_url      VARCHAR(512) NULL,
  failure_reason   VARCHAR(128) NULL,
  ts               DATETIME(3) NOT NULL,
  KEY idx_pay_user_ts (user_id, ts),
  KEY idx_pay_subscription (subscription_id),
  KEY idx_pay_user_status_ts (user_id, status, ts),
  CONSTRAINT fk_pay_user  FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_pay_sub   FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS refunds (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  payment_id         BIGINT NOT NULL,
  amount_cents       BIGINT NOT NULL,
  reason             VARCHAR(128) NULL,
  provider_refund_id VARCHAR(128) NULL,
  ts                 DATETIME(3) NOT NULL,
  KEY idx_ref_payment (payment_id),
  CONSTRAINT fk_ref_payment FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS revenue_monthly (
  month      DATE PRIMARY KEY,
  mrr_cents  BIGINT NOT NULL,
  arpu_cents BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS invoices (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id             BIGINT NOT NULL,
  subscription_id     BIGINT NOT NULL,
  amount_cents        BIGINT NOT NULL,
  currency            CHAR(3) NOT NULL,
  period_start        DATETIME(3) NOT NULL,
  period_end          DATETIME(3) NOT NULL,
  provider_invoice_id VARCHAR(128) NULL,
  ts                  DATETIME(3) NOT NULL,
  KEY idx_inv_user_ts (user_id, ts),
  CONSTRAINT fk_inv_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_inv_sub  FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS dunning_attempts (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  subscription_id  BIGINT NOT NULL,
  attempt_no       INT NOT NULL,
  scheduled_at     DATETIME(3) NOT NULL,
  status           ENUM('pending','success','failed','skipped') NOT NULL DEFAULT 'pending',
  detail           VARCHAR(255) NULL,
  created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_sub_attempt (subscription_id, attempt_no),
  CONSTRAINT fk_dunning_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS webhook_events (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  provider   ENUM('stripe') NOT NULL,
  event_id   VARCHAR(128) NOT NULL UNIQUE,
  ts         DATETIME(3) NOT NULL,
  payload    JSON NOT NULL,
  processed  BOOLEAN NOT NULL DEFAULT FALSE,
  KEY idx_webhook_processed_ts (processed, ts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS idempotency_keys (
  `key`      VARCHAR(64) PRIMARY KEY,
  user_id    BIGINT NOT NULL,
  endpoint   VARCHAR(128) NOT NULL,
  body_hash  CHAR(64) NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_idem_created (created_at),
  CONSTRAINT fk_idem_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Vendedores/plataforma y fiscalidad
CREATE TABLE IF NOT EXISTS platform_seller (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT NOT NULL,
  legal_name  VARCHAR(255) NOT NULL,
  tax_id      VARCHAR(64),
  country     CHAR(2),
  address     VARCHAR(255),
  kyc_status  ENUM('pending','verified','failed') NOT NULL DEFAULT 'pending',
  created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT fk_ps_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tax_forms (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  seller_id BIGINT NOT NULL,
  form_type VARCHAR(64) NOT NULL,
  year      INT NOT NULL,
  data      JSON,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_seller_year_form (seller_id, year, form_type),
  CONSTRAINT fk_tax_seller FOREIGN KEY (seller_id) REFERENCES platform_seller(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS payout_log (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  seller_id       BIGINT NOT NULL,
  amount_cents    BIGINT NOT NULL,
  currency        CHAR(3) NOT NULL,
  psp_transfer_id VARCHAR(128),
  created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT fk_payout_seller FOREIGN KEY (seller_id) REFERENCES platform_seller(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Vista "monetizacion"
CREATE OR REPLACE VIEW monetizacion AS
SELECT
  s.id                        AS id,
  s.user_id                   AS user_id,
  p.code                      AS plan_code,
  CASE p.code
    WHEN 'free'  THEN 'free'
    WHEN 'pro'   THEN 'pro'
    ELSE 'teams'
  END                         AS plan_type,
  (p.price_cents/100.0)       AS amount,
  CASE s.status
    WHEN 'active'   THEN 'active'
    WHEN 'canceled' THEN 'cancelled'
    ELSE 'inactive'
  END                         AS status,
  s.created_at                AS created_at,
  s.current_period_end        AS updated_at
FROM subscriptions s
JOIN plans p ON p.id = s.plan_id;

-- =====================================================================
-- 6) Auditoría / seguridad / jobs / API keys / eventos / RBAC
-- =====================================================================

CREATE TABLE IF NOT EXISTS audit_log (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  entity      VARCHAR(64) NOT NULL,
  entity_id   BIGINT NULL,
  action      VARCHAR(32) NOT NULL,
  changed_by  VARCHAR(128) NULL,
  changed_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  details     JSON NULL,
  INDEX idx_audit_entity (entity),
  INDEX idx_audit_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS AuditoriaUsuario (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  idUsuario    BIGINT NULL,
  accion       VARCHAR(100) NOT NULL,
  detalles     TEXT NULL,
  ipOrigen     VARCHAR(45) NULL,
  ipOrigen_bin VARBINARY(16) NULL,
  userAgent    TEXT NULL,
  fechaAccion  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  resultado    ENUM('EXITOSO','FALLIDO','PARCIAL') NOT NULL DEFAULT 'EXITOSO',
  metadatos    JSON NULL,
  INDEX idx_aud_usuario (idUsuario),
  INDEX idx_aud_fecha (fechaAccion),
  INDEX idx_aud_resultado (resultado),
  CONSTRAINT fk_aud_usuario FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS AuditoriaAvanzada (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  idAuditoriaUsuario   BIGINT NOT NULL,
  nivelCriticidad      ENUM('BAJO','MEDIO','ALTO','CRITICO') NOT NULL DEFAULT 'BAJO',
  modulo               VARCHAR(50) NOT NULL,
  esIpBloqueada        BOOLEAN DEFAULT FALSE,
  hashSesion           VARCHAR(64),
  timestampPreciso     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  datosExtendidos      JSON,
  INDEX idx_aav_user (idAuditoriaUsuario),
  INDEX idx_aav_crit (nivelCriticidad),
  INDEX idx_aav_mod (modulo),
  INDEX idx_aav_ts (timestampPreciso),
  CONSTRAINT fk_aav_aud FOREIGN KEY (idAuditoriaUsuario) REFERENCES AuditoriaUsuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS IPsBloqueadas (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  ip_address       VARCHAR(45) NOT NULL,
  razon_bloqueo    VARCHAR(255) NOT NULL,
  fecha_bloqueo    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  fecha_expiracion DATETIME(3) NULL,
  estado           ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  bloqueado_por    BIGINT NULL,
  metadatos        JSON,
  UNIQUE KEY unique_ip_activa (ip_address, estado),
  INDEX idx_ip_estado (ip_address, estado),
  INDEX idx_ip_expiracion (fecha_expiracion),
  CONSTRAINT fk_ips_user FOREIGN KEY (bloqueado_por) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS LogErroresSistema (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  modulo        VARCHAR(64) NOT NULL,
  error_type    VARCHAR(64) NOT NULL,
  error_message TEXT NOT NULL,
  timestamp     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  datos_contexto JSON NULL,
  INDEX idx_err_modulo (modulo),
  INDEX idx_err_tipo (error_type),
  INDEX idx_err_ts (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS AlertasSeguridad (
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  tipoAlerta        VARCHAR(64) NOT NULL,
  idUsuarioAfectado BIGINT NULL,
  descripcion       TEXT,
  nivelPrioridad    ENUM('BAJA','MEDIA','ALTA','URGENTE') NOT NULL DEFAULT 'MEDIA',
  estado            ENUM('PENDIENTE','EN_PROCESO','RESUELTA','DESCARTADA') NOT NULL DEFAULT 'PENDIENTE',
  metadatosAlerta   JSON,
  fechaCreacion     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  INDEX idx_alerta_tipo (tipoAlerta),
  INDEX idx_alerta_prioridad (nivelPrioridad),
  INDEX idx_alerta_estado (estado),
  CONSTRAINT fk_alerta_user FOREIGN KEY (idUsuarioAfectado) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS EstadisticasUsuario (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  idUsuario        BIGINT NOT NULL,
  fecha            DATE NOT NULL,
  totalAcciones    INT NOT NULL DEFAULT 0,
  accionesExitosas INT NOT NULL DEFAULT 0,
  ultimaActividad  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uq_estad_usuario_fecha (idUsuario, fecha),
  INDEX idx_estad_usuario (idUsuario),
  CONSTRAINT fk_estad_user FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS api_keys (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT NOT NULL,
  api_key    CHAR(64) NOT NULL UNIQUE,
  key_type   ENUM('read','write','admin') NOT NULL DEFAULT 'read',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  revoked_at DATETIME(3) NULL,
  INDEX idx_api_user (user_id),
  CONSTRAINT fk_api_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Log auditoría nivel “evento” inmutable
CREATE TABLE IF NOT EXISTS audit_event (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_type     VARCHAR(64) NOT NULL,
  category       ENUM('auth','account','challenge','evidence','validation','moderation','privacy','payment','admin') NOT NULL DEFAULT 'account',
  actor_user_id  BIGINT NULL,
  entity_type    VARCHAR(64) NOT NULL,
  entity_id      BIGINT NOT NULL,
  before_data    JSON NULL,
  after_data     JSON NULL,
  request_id     VARCHAR(64) NULL,
  ip             VARCHAR(64) NULL,
  ip_bin         VARBINARY(16) NULL,
  ua             VARCHAR(255) NULL,
  metadata       JSON NULL,
  prev_hash      CHAR(64) NULL,
  record_hash    CHAR(64) NULL,
  signature      VARBINARY(256) NULL,
  created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_entity (entity_type, entity_id),
  KEY idx_type_created (event_type, created_at),
  CONSTRAINT fk_audit_event_actor FOREIGN KEY (actor_user_id) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS key_rotation_log (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  key_alias    VARCHAR(128) NOT NULL,
  version_from VARCHAR(64) NOT NULL,
  version_to   VARCHAR(64) NOT NULL,
  rotated_at   DATETIME(3) NOT NULL,
  rotated_by   VARCHAR(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Jobs (definiciones y ejecuciones) + governance jobs (auditoría)
CREATE TABLE IF NOT EXISTS job_definitions (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  code       VARCHAR(64) NOT NULL UNIQUE,
  schedule   VARCHAR(64) NOT NULL, -- cron
  active     BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS job_runs (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_id      BIGINT NOT NULL,
  started_at  DATETIME(3) NOT NULL,
  finished_at DATETIME(3) NULL,
  status      ENUM('success','failed','partial') NOT NULL,
  output      TEXT NULL,
  KEY idx_job_started (job_id, started_at),
  CONSTRAINT fk_runs_job FOREIGN KEY (job_id) REFERENCES job_definitions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS governance_jobs (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_name     VARCHAR(128) NOT NULL,
  executed_by  VARCHAR(128) NOT NULL COMMENT 'PII: usuario ejecutor',
  executed_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  status       ENUM('SUCCESS','FAIL','PARTIAL') NOT NULL,
  details      TEXT NULL,
  INDEX idx_job_name (job_name),
  INDEX idx_executed_by (executed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Eventos, métricas y encuestas
CREATE TABLE IF NOT EXISTS events (
  event_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT NULL,
  event_type VARCHAR(64) NOT NULL,      -- snake_case
  timestamp  DATETIME(3) NOT NULL,
  props      JSON,
  request_id VARCHAR(64),
  trace_id   VARCHAR(64),
  KEY idx_user_type_ts (user_id, event_type, timestamp),
  KEY idx_ts (timestamp),
  KEY idx_events_user_ts (user_id, timestamp),
  CONSTRAINT fk_events_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS surveys (
  survey_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id   BIGINT NOT NULL,
  type      ENUM('sean_ellis','nps','love') NOT NULL,
  answers   JSON NOT NULL,
  ts        DATETIME(3) NOT NULL,
  KEY idx_survey_user (user_id),
  KEY idx_survey_type_ts (type, ts),
  CONSTRAINT fk_surveys_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Libro mayor de puntos
CREATE TABLE IF NOT EXISTS points_ledger (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id              BIGINT NOT NULL,
  points               INT NOT NULL,
  reason               VARCHAR(128) NOT NULL,
  related_entity_type  VARCHAR(64) NULL,
  related_entity_id    BIGINT NULL,
  ts                   DATETIME(3) NOT NULL,
  KEY idx_pl_user_ts (user_id, ts),
  CONSTRAINT fk_pl_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Media objects (archivos normalizados)
CREATE TABLE IF NOT EXISTS media_objects (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  challenge_id     BIGINT NOT NULL,
  kind             ENUM('text','image','video') NOT NULL,
  storage_key      VARCHAR(256) NOT NULL,
  sha256           CHAR(64) NOT NULL,
  size_bytes       BIGINT NOT NULL,
  mime             VARCHAR(128) NOT NULL,
  width            INT NULL,
  height           INT NULL,
  duration_sec     INT NULL,
  visibility       ENUM('private','validators','public') NOT NULL,
  antivirus_status ENUM('pending','clean','infected') NOT NULL DEFAULT 'pending',
  exif_stripped    BOOLEAN NOT NULL DEFAULT TRUE,
  text_content     TEXT NULL,
  created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_media_challenge_created (challenge_id, created_at),
  KEY idx_media_user_created (user_id, created_at),
  KEY idx_media_user_kind_created (user_id, kind, created_at),
  UNIQUE KEY uniq_media_sha (sha256),
  CONSTRAINT fk_media_user      FOREIGN KEY (user_id)      REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_media_challenge FOREIGN KEY (challenge_id) REFERENCES Reto(id)    ON DELETE CASCADE,
  CONSTRAINT chk_media_antivirus_visibility CHECK (antivirus_status <> 'infected' OR visibility = 'private')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tutorías
CREATE TABLE IF NOT EXISTS tutor_assignments (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  tutor_user_id    BIGINT NOT NULL,
  student_user_id  BIGINT NOT NULL,
  challenge_id     BIGINT NULL,
  status           ENUM('active','paused','ended') NOT NULL DEFAULT 'active',
  created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_pair (tutor_user_id, student_user_id, challenge_id),
  CONSTRAINT fk_tutor_tutor   FOREIGN KEY (tutor_user_id)   REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_tutor_student FOREIGN KEY (student_user_id) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_tutor_chal    FOREIGN KEY (challenge_id)    REFERENCES Reto(id)    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tutor_sessions (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  assignment_id  BIGINT NOT NULL,
  scheduled_at   DATETIME(3) NOT NULL,
  duration_min   INT NULL,
  mode           ENUM('chat','video','in_person') NOT NULL,
  notes          TEXT NULL,
  created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT fk_tutor_sessions_assign FOREIGN KEY (assignment_id) REFERENCES tutor_assignments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tutor_feedback (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  assignment_id  BIGINT NOT NULL,
  given_by       ENUM('tutor','student') NOT NULL,
  score          TINYINT NOT NULL,
  comment        VARCHAR(512) NULL,
  ts             DATETIME(3) NOT NULL,
  CONSTRAINT fk_tutor_feedback_assign FOREIGN KEY (assignment_id) REFERENCES tutor_assignments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- RBAC mínimo
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(255) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  granted_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 7) Triggers (auditoría, progreso, jobs, suscripciones)
-- =====================================================================

DELIMITER $$

-- Auditoría governance_jobs -> audit_log
DROP TRIGGER IF EXISTS governance_jobs_audit $$
CREATE TRIGGER governance_jobs_audit
AFTER INSERT ON governance_jobs
FOR EACH ROW
BEGIN
  INSERT INTO audit_log (entity, entity_id, action, changed_by, changed_at, details)
  VALUES ('governance_jobs', NEW.id, 'INSERT', NEW.executed_by, NEW.executed_at,
          JSON_OBJECT('job_name', NEW.job_name, 'status', NEW.status));
END $$

-- Auditoría de UPDATE en Reto (estado/visibilidad)
DROP TRIGGER IF EXISTS reto_update_audit $$
CREATE TRIGGER reto_update_audit
BEFORE UPDATE ON Reto
FOR EACH ROW
BEGIN
  INSERT INTO audit_log (entity, entity_id, action, changed_by, changed_at, details)
  VALUES ('Reto', OLD.id, 'UPDATE', CURRENT_USER(), NOW(3),
          JSON_OBJECT('from_estado', OLD.estado, 'to_estado', NEW.estado,
                      'from_visibility', OLD.visibility, 'to_visibility', NEW.visibility));
END $$

-- Progreso automático por evidencias aprobadas
DROP TRIGGER IF EXISTS tr_evidencia_actualizar_progreso $$
CREATE TRIGGER tr_evidencia_actualizar_progreso
AFTER INSERT ON Evidencia
FOR EACH ROW
BEGIN
  DECLARE v_total_dias INT DEFAULT 0;
  DECLARE v_aprobadas INT DEFAULT 0;
  DECLARE v_progreso DECIMAL(5,2) DEFAULT 0;

  SELECT DATEDIFF(fechaFin, fechaInicio) + 1 INTO v_total_dias
  FROM Reto WHERE id = NEW.idReto;

  SELECT COUNT(*) INTO v_aprobadas
  FROM Evidencia
  WHERE idReto = NEW.idReto
    AND idUsuario = NEW.idUsuario
    AND estadoValidacion = 'APROBADA';

  IF v_total_dias > 0 THEN
    SET v_progreso = LEAST(100, (v_aprobadas * 100.0) / v_total_dias);

    UPDATE ParticipacionReto
    SET progreso = v_progreso,
        fechaCompletado = CASE WHEN v_progreso >= 100 THEN NOW(3) ELSE NULL END,
        estado = CASE WHEN v_progreso >= 100 THEN 'COMPLETADO' ELSE estado END
    WHERE idReto = NEW.idReto AND idUsuario = NEW.idUsuario;
  END IF;
END $$

-- Auditoría de altas de suscripciones
DROP TRIGGER IF EXISTS subscriptions_audit $$
CREATE TRIGGER subscriptions_audit
AFTER INSERT ON subscriptions
FOR EACH ROW
BEGIN
  INSERT INTO audit_log (entity, entity_id, action, changed_by, changed_at, details)
  VALUES ('subscriptions', NEW.id, 'INSERT', CAST(NEW.user_id AS CHAR), NOW(3),
          JSON_OBJECT('plan_id', NEW.plan_id, 'status', NEW.status, 'current_period_end', NEW.current_period_end));
END $$

-- Defensa extra: si llega NULL en fechaAccion, forzar NOW(3)
DROP TRIGGER IF EXISTS auditoriausuario_default_ts $$
CREATE TRIGGER auditoriausuario_default_ts
BEFORE INSERT ON AuditoriaUsuario
FOR EACH ROW
BEGIN
  IF NEW.fechaAccion IS NULL THEN
    SET NEW.fechaAccion = CURRENT_TIMESTAMP(3);
  END IF;
END $$

DELIMITER ;

-- =====================================================================
-- 8) Procedimientos almacenados clave
-- =====================================================================

DELIMITER $$

-- Inserción avanzada de auditoría
DROP PROCEDURE IF EXISTS sp_insert_auditoria_avanzada $$
CREATE PROCEDURE sp_insert_auditoria_avanzada(
  IN p_id_usuario BIGINT,
  IN p_accion VARCHAR(100),
  IN p_detalles TEXT,
  IN p_ip_origen VARCHAR(45),
  IN p_user_agent TEXT,
  IN p_metadatos JSON,
  IN p_nivel_criticidad ENUM('BAJO','MEDIO','ALTO','CRITICO'),
  IN p_modulo VARCHAR(50),
  IN p_resultado ENUM('EXITOSO','FALLIDO','PARCIAL')
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
    INSERT INTO LogErroresSistema (modulo, error_type, error_message, timestamp, datos_contexto)
    VALUES (COALESCE(p_modulo,'AUDITORIA'),'AUDITORIA_FAILURE','Error al insertar registro de auditoría', NOW(3),
            JSON_OBJECT('usuario_id', p_id_usuario, 'accion', p_accion, 'ip', p_ip_origen));
    RESIGNAL;
  END;

  START TRANSACTION;

  IF p_id_usuario IS NOT NULL THEN
    SELECT COUNT(*) INTO v_user_exists FROM Usuario WHERE id = p_id_usuario;
    IF v_user_exists = 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usuario especificado no existe en el sistema';
    END IF;
  END IF;

  SELECT COUNT(*) INTO v_ip_blocked
  FROM IPsBloqueadas
  WHERE ip_address = p_ip_origen
    AND estado = 'ACTIVO'
    AND (fecha_expiracion IS NULL OR fecha_expiracion > NOW(3));

  IF p_id_usuario IS NOT NULL THEN
    SELECT COUNT(*) INTO v_daily_actions
    FROM AuditoriaUsuario
    WHERE idUsuario = p_id_usuario AND DATE(fechaAccion) = CURDATE();
    IF v_daily_actions >= v_max_daily_actions THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Límite diario de acciones excedido para este usuario';
    END IF;
  END IF;

  INSERT INTO AuditoriaUsuario (idUsuario, accion, detalles, ipOrigen, ipOrigen_bin, userAgent, fechaAccion, resultado, metadatos)
  VALUES (p_id_usuario, p_accion, p_detalles, p_ip_origen, INET6_ATON(p_ip_origen), p_user_agent, NOW(3), p_resultado, COALESCE(p_metadatos, JSON_OBJECT()));
  SET v_audit_id = LAST_INSERT_ID();

  INSERT INTO AuditoriaAvanzada (idAuditoriaUsuario, nivelCriticidad, modulo, esIpBloqueada, hashSesion, timestampPreciso, datosExtendidos)
  VALUES (v_audit_id, p_nivel_criticidad, p_modulo, v_ip_blocked > 0,
          SHA2(CONCAT(COALESCE(p_id_usuario,0), p_ip_origen, UNIX_TIMESTAMP()), 256),
          NOW(3),
          JSON_OBJECT('acciones_diarias_usuario', v_daily_actions,
                      'ip_previamente_bloqueada', v_ip_blocked > 0,
                      'timestamp_unix', UNIX_TIMESTAMP(),
                      'zona_horaria', @@session.time_zone));

  IF p_nivel_criticidad IN ('ALTO','CRITICO') OR p_resultado = 'FALLIDO' THEN
    INSERT INTO AlertasSeguridad (tipoAlerta, idUsuarioAfectado, descripcion, nivelPrioridad, estado, metadatosAlerta, fechaCreacion)
    VALUES ('EVENTO_AUDITORIA_CRITICO', p_id_usuario,
            CONCAT('Evento de auditoría crítico: ', p_accion, ' - ', p_detalles),
            CASE p_nivel_criticidad WHEN 'CRITICO' THEN 'URGENTE' WHEN 'ALTO' THEN 'ALTA' ELSE 'MEDIA' END,
            'PENDIENTE',
            JSON_OBJECT('accion', p_accion, 'resultado', p_resultado, 'ip_origen', p_ip_origen, 'modulo', p_modulo, 'audit_id', v_audit_id),
            NOW(3));
  END IF;

  IF p_id_usuario IS NOT NULL AND p_resultado = 'EXITOSO' THEN
    INSERT INTO EstadisticasUsuario (idUsuario, fecha, totalAcciones, accionesExitosas, ultimaActividad)
    VALUES (p_id_usuario, CURDATE(), 1, 1, NOW(3))
    ON DUPLICATE KEY UPDATE totalAcciones = totalAcciones + 1,
                            accionesExitosas = accionesExitosas + 1,
                            ultimaActividad = NOW(3);
  END IF;

  SELECT v_audit_id AS auditoria_id,
         'Registro de auditoría insertado correctamente' AS mensaje,
         p_nivel_criticidad AS nivel_asignado,
         v_daily_actions + 1 AS total_acciones_dia;

  COMMIT;
END $$

-- Reporte integral de usuario
DROP PROCEDURE IF EXISTS sp_generar_reporte_usuario $$
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
    ROLLBACK; RESIGNAL;
  END;

  START TRANSACTION;

  IF NOT EXISTS (SELECT 1 FROM Usuario WHERE id = p_user_id AND estado = 'ACTIVO') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usuario no encontrado o inactivo';
  END IF;

  SELECT COUNT(*) INTO v_total_retos
  FROM Reto r
  WHERE r.idCreador = p_user_id AND DATE(r.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

  SELECT COUNT(*) INTO v_retos_completados
  FROM Reto r
  WHERE r.idCreador = p_user_id AND r.estado = 'COMPLETADO' AND DATE(r.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

  SELECT COUNT(*) INTO v_retos_activos
  FROM Reto r
  WHERE r.idCreador = p_user_id AND r.estado = 'ACTIVO' AND DATE(r.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

  SELECT COUNT(*) INTO v_total_evidencias
  FROM Evidencia e
  WHERE e.idUsuario = p_user_id AND DATE(e.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

  SELECT COUNT(*) INTO v_evidencias_aprobadas
  FROM Evidencia e
  WHERE e.idUsuario = p_user_id AND e.estadoValidacion = 'APROBADA' AND DATE(e.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

  SELECT COALESCE(SUM(CASE WHEN e.estadoValidacion = 'APROBADA' THEN 10
                           WHEN e.estadoValidacion = 'PENDIENTE' THEN 5
                           ELSE 0 END), 0) INTO v_puntos_totales
  FROM Evidencia e
  WHERE e.idUsuario = p_user_id AND DATE(e.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta;

  SELECT p_user_id AS usuario_id, u.nombre, u.apellidos, u.email,
         p_fecha_desde AS fecha_desde, p_fecha_hasta AS fecha_hasta,
         v_total_retos AS total_retos_creados,
         v_retos_completados AS retos_completados,
         v_retos_activos AS retos_activos,
         v_total_evidencias AS total_evidencias,
         v_evidencias_aprobadas AS evidencias_aprobadas,
         CASE WHEN v_total_evidencias > 0 THEN ROUND((v_evidencias_aprobadas * 100.0)/v_total_evidencias, 2) ELSE 0 END AS porcentaje_exito,
         v_puntos_totales AS puntos_obtenidos,
         v_badges_obtenidos AS badges_obtenidos,
         NOW(3) AS fecha_reporte
  FROM Usuario u WHERE u.id = p_user_id;

  SELECT 'detalle_retos' AS seccion, r.id AS reto_id, r.titulo, r.estado, r.dificultad,
         r.fechaInicio, r.fechaFin, r.metaObjetivo, r.fechaCreacion,
         DATEDIFF(COALESCE(r.fechaFin, NOW(3)), r.fechaInicio) AS duracion_dias
  FROM Reto r
  WHERE r.idCreador = p_user_id AND DATE(r.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta
  ORDER BY r.fechaCreacion DESC;

  SELECT 'detalle_evidencias' AS seccion, e.id AS evidencia_id, r.titulo AS reto_titulo,
         e.tipoEvidencia, e.estadoValidacion, e.fechaReporte, e.fechaValidacion,
         v.nombre AS validador_nombre, e.comentarioValidador, e.descripcion
  FROM Evidencia e
  JOIN Reto r ON e.idReto = r.id
  LEFT JOIN Usuario v ON e.idValidador = v.id
  WHERE e.idUsuario = p_user_id AND DATE(e.fechaCreacion) BETWEEN p_fecha_desde AND p_fecha_hasta
  ORDER BY e.fechaReporte DESC
  LIMIT 20;

  INSERT INTO AuditoriaUsuario (idUsuario, accion, detalles, ipOrigen, ipOrigen_bin, userAgent, fechaAccion, resultado, metadatos)
  VALUES (p_user_id, 'GENERAR_REPORTE',
          CONCAT('Reporte generado para período ', p_fecha_desde, ' a ', p_fecha_hasta),
          '127.0.0.1', INET6_ATON('127.0.0.1'), 'SISTEMA_IMPULSE', NOW(3), 'EXITOSO',
          JSON_OBJECT('retos_totales', v_total_retos, 'evidencias_totales', v_total_evidencias, 'puntos_obtenidos', v_puntos_totales));

  COMMIT;
END $$

DELIMITER ;

-- =====================================================================
-- 9) Semillas mínimas (usuarios/roles/categoría/reto ejemplo)
-- =====================================================================

-- Roles
INSERT INTO roles (code, name)
VALUES ('user','Usuario'), ('validator','Validador'), ('admin','Administrador')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Usuario demo
INSERT INTO Usuario (email, password_hash, password_algo, nombre, estado)
VALUES ('demo@impulse.local', '$2y$10$DEMOhashDEMOhashDEMOhashDEMOhashDEMOhashDE', 'bcrypt', 'Usuario Demo', 'ACTIVO')
ON DUPLICATE KEY UPDATE email = email;

-- Rol para el demo (user)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM Usuario u, roles r
WHERE u.email = 'demo@impulse.local' AND r.code = 'user'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Categoría por defecto
INSERT INTO CategoriaReto (nombre, descripcion)
VALUES ('General', 'Categoría general por defecto')
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Reto de ejemplo
INSERT INTO Reto (
  idCreador, idCategoria, titulo, descripcion,
  fechaInicio, fechaFin, tipoValidacion, dificultad,
  metaObjetivo, unidadMedida, estado, esPublico, visibility, public_slug
) VALUES (
  (SELECT id FROM Usuario WHERE email='demo@impulse.local' LIMIT 1),
  (SELECT id FROM CategoriaReto WHERE nombre='General' LIMIT 1),
  'Reto de ejemplo: Caminar 10.000 pasos diarios',
  'Reto para mantener actividad física constante caminando 10.000 pasos cada día durante un mes',
  DATE_ADD(NOW(3), INTERVAL 1 DAY),
  DATE_ADD(NOW(3), INTERVAL 31 DAY),
  'MANUAL',
  'MEDIA',
  '10000 pasos diarios por 30 días',
  'pasos',
  'ACTIVO',
  TRUE,
  'public',
  'caminar-10k-pasos'
) ON DUPLICATE KEY UPDATE titulo = titulo;

-- =====================================================================
-- 10) Verificaciones finales
-- =====================================================================

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'OK - Esquema IMPULSE inicializado (endurecido)' AS status,
       (SELECT COUNT(*) FROM Usuario) AS total_usuarios,
       (SELECT COUNT(*) FROM roles) AS total_roles,
       (SELECT COUNT(*) FROM CategoriaReto) AS total_categorias,
       (SELECT COUNT(*) FROM Reto) AS total_retos;

-- =====================================================================
-- IMPULSE – Script Maestro MySQL 8.x (v4-LEAN-MASTER, 2025-08-29)
-- Concordancia total con árbol actual (public_slug, periodicidad,
-- invites/aceptación validador).
-- Cumple: Sin sprocs ni triggers, UTC, utf8mb4_0900_ai_ci, JSON extensiones.
-- Módulos: RBAC, Challenges, Evidencias, Validadores, Notificaciones,
-- GDPR/DSA (consents/DSAR/moderación/AMAR), Media, Gamificación,
-- Hábitos, Mentoring, Eventos/Surveys, Seguridad/Operación (audit, IPs,
-- API keys, jobs), Billing/Marketplace, Afiliados/Referidos.
-- =====================================================================

-- 0) Base de datos
CREATE DATABASE IF NOT EXISTS impulse
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE impulse;

-- Recomendaciones de sesión
SET SESSION time_zone = '+00:00';
SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';
SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 1) Identidad + RBAC (≤7 roles macro)
-- =====================================================================
CREATE TABLE IF NOT EXISTS usuario (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  email              VARCHAR(254) NOT NULL UNIQUE,
  nombre             VARCHAR(100) NOT NULL,
  apellidos          VARCHAR(150) NULL,
  photo_url          VARCHAR(512) NULL,
  locale             VARCHAR(16) NOT NULL DEFAULT 'es-ES',
  timezone           VARCHAR(64) NOT NULL DEFAULT 'Europe/Madrid',
  estado             ENUM('ACTIVO','INACTIVO','SUSPENDIDO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  default_visibility ENUM('private','validators','public') NOT NULL DEFAULT 'private',
  password_hash      CHAR(60) NOT NULL,
  marketing_opt_in   BOOLEAN NOT NULL DEFAULT FALSE,
  verified_at        DATETIME(3) NULL,
  public_slug        VARCHAR(128) NULL,
  created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at         DATETIME(3) NULL,
  KEY idx_usuario_estado_created (estado, created_at),
  UNIQUE KEY uniq_usuario_public_slug (public_slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  granted_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (user_id, role_id),
  KEY idx_user_roles_user (user_id),
  KEY idx_user_roles_role (role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 2) Catálogos y Retos + Validadores + Participación + Evidencias
-- =====================================================================
CREATE TABLE IF NOT EXISTS categoria_reto (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL UNIQUE,
  descripcion VARCHAR(255),
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS reto (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  creador_id   BIGINT NOT NULL,
  categoria_id BIGINT NOT NULL,
  titulo       VARCHAR(200) NOT NULL,
  descripcion  TEXT NOT NULL,
  fecha_inicio DATETIME(3) NOT NULL,
  fecha_fin    DATETIME(3) NOT NULL,
  visibility   ENUM('private','validators','public') NOT NULL DEFAULT 'private',
  estado       ENUM('BORRADOR','PENDIENTE','ACTIVO','PAUSADO','COMPLETADO','CANCELADO','ELIMINADO','FALLIDO') NOT NULL DEFAULT 'BORRADOR',
  public_slug  VARCHAR(128) UNIQUE,
  -- Programación / periodicidad
  periodicidad ENUM('one_off','daily','weekly','custom') NOT NULL DEFAULT 'one_off',
  cadence_days INT NULL,
  config       JSON,
  created_at   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  KEY idx_reto_creador (creador_id),
  KEY idx_reto_estado (estado),
  KEY idx_reto_fechas (fecha_inicio, fecha_fin),
  KEY idx_reto_visibility (visibility),
  KEY idx_reto_periodicidad_created (periodicidad, created_at),
  CONSTRAINT fk_reto_user FOREIGN KEY (creador_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_reto_cat  FOREIGN KEY (categoria_id) REFERENCES categoria_reto(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS validator_invites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  inviter_id    BIGINT NOT NULL,
  invitee_email VARCHAR(254) NOT NULL,
  token         VARCHAR(64) NOT NULL UNIQUE,
  expires_at    DATETIME(3) NOT NULL,
  -- Aceptación de la invitación (trazabilidad)
  accepted_at   DATETIME(3) NULL,
  accepted_ip   VARCHAR(64) NULL,
  accepted_ua   VARCHAR(255) NULL,
  created_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_inviter (inviter_id),
  KEY idx_validator_invites_email (invitee_email),
  KEY idx_vinv_accepted (accepted_at),
  CONSTRAINT fk_inviter FOREIGN KEY (inviter_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS validador_reto (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reto_id      BIGINT NOT NULL,
  validador_id BIGINT NOT NULL,
  fecha_asignacion DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  estado       ENUM('ACTIVO','INACTIVO','SUSPENDIDO') NOT NULL DEFAULT 'ACTIVO',
  tipo_validador ENUM('PRINCIPAL','SECUNDARIO','ESPECIALISTA') NOT NULL DEFAULT 'PRINCIPAL',
  notas_validador TEXT,
  metadatos     JSON,
  -- Aceptación del vínculo reto↔validador y versión de consentimiento mostrado
  accepted_at DATETIME(3) NULL,
  consent_version VARCHAR(32) NULL,
  UNIQUE KEY uniq_validador_reto (reto_id, validador_id),
  KEY idx_validador_reto (reto_id),
  KEY idx_validador_usuario (validador_id),
  KEY idx_vr_accepted (accepted_at),
  CONSTRAINT fk_vr_reto FOREIGN KEY (reto_id) REFERENCES reto(id) ON DELETE CASCADE,
  CONSTRAINT fk_vr_user FOREIGN KEY (validador_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS participacion_reto (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reto_id     BIGINT NOT NULL,
  usuario_id  BIGINT NOT NULL,
  created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  estado      ENUM('INSCRITO','ACTIVO','PAUSADO','COMPLETADO','ABANDONADO') NOT NULL DEFAULT 'INSCRITO',
  progreso    DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  fecha_completado DATETIME(3),
  notas_personales TEXT,
  metadatos   JSON,
  UNIQUE KEY uniq_usuario_reto (usuario_id, reto_id),
  KEY idx_part_estado (estado),
  KEY idx_participacion_user (usuario_id),
  CONSTRAINT fk_part_reto FOREIGN KEY (reto_id) REFERENCES reto(id) ON DELETE CASCADE,
  CONSTRAINT fk_part_user FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS validacion (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  validator_id BIGINT NOT NULL,
  challenge_id BIGINT NOT NULL,
  status ENUM('accepted','rejected') NOT NULL,
  comment VARCHAR(512),
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_validator_challenge (validator_id, challenge_id),
  KEY idx_valid_validator_created (validator_id, created_at),
  KEY idx_valid_challenge (challenge_id),
  KEY idx_validacion_status_created (status, created_at),
  CONSTRAINT chk_valid_comment CHECK (status='accepted' OR (status='rejected' AND comment IS NOT NULL)),
  CONSTRAINT fk_val_user FOREIGN KEY (validator_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_val_chal FOREIGN KEY (challenge_id) REFERENCES reto(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS evidencia (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reto_id     BIGINT NOT NULL,
  usuario_id  BIGINT NOT NULL,
  created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  tipo ENUM('TEXTO','FOTO','VIDEO','AUDIO','MIXTO') NOT NULL,
  descripcion TEXT,
  archivo_url TEXT,
  archivo_thumbnail TEXT,
  estado ENUM('PENDIENTE','EN_REVISION','APROBADA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
  fecha_validacion DATETIME(3),
  validador_id BIGINT NULL,
  comentario_validador TEXT,
  razon_rechazo TEXT,
  hash_archivo VARCHAR(64),
  tamano_archivo BIGINT,
  metadatos JSON,
  KEY idx_evid_reto (reto_id),
  KEY idx_evid_usuario (usuario_id),
  KEY idx_evid_estado (estado),
  KEY idx_evid_created (created_at),
  KEY idx_evid_reto_usuario (reto_id, usuario_id),
  CONSTRAINT fk_evid_reto FOREIGN KEY (reto_id) REFERENCES reto(id) ON DELETE CASCADE,
  CONSTRAINT fk_evid_user FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
-- =====================================================================
-- 3) Notificaciones
-- =====================================================================
CREATE TABLE IF NOT EXISTS notificacion_usuario (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  tipo ENUM('RETO_NUEVO','EVIDENCIA_APROBADA','EVIDENCIA_RECHAZADA','VALIDACION_PENDIENTE','LOGRO_OBTENIDO','RECORDATORIO','SISTEMA') NOT NULL,
  titulo VARCHAR(200) NOT NULL,
  mensaje TEXT NOT NULL,
  requires_ack BOOLEAN NOT NULL DEFAULT FALSE,
  leida BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  read_at DATETIME(3),
  referencia_reto_id BIGINT NULL,
  referencia_evidencia_id BIGINT NULL,
  canal ENUM('APP','EMAIL','PUSH','SMS') NOT NULL DEFAULT 'APP',
  prioridad ENUM('BAJA','MEDIA','ALTA','URGENTE') NOT NULL DEFAULT 'MEDIA',
  metadatos JSON,
  KEY idx_notif_user (usuario_id, created_at),
  KEY idx_notif_type_created (tipo, created_at),
  KEY idx_notif_leida_created (leida, created_at),
  CONSTRAINT fk_notif_user FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_notif_reto FOREIGN KEY (referencia_reto_id) REFERENCES reto(id) ON DELETE SET NULL,
  CONSTRAINT fk_notif_evid FOREIGN KEY (referencia_evidencia_id) REFERENCES evidencia(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS notification_deliveries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  notification_id BIGINT NOT NULL,
  channel ENUM('email','whatsapp','telegram','push','in_app') NOT NULL,
  status ENUM('queued','sent','failed') NOT NULL,
  detail VARCHAR(255),
  sent_at DATETIME(3),
  KEY idx_notif (notification_id),
  CONSTRAINT fk_delivery_notif FOREIGN KEY (notification_id) REFERENCES notificacion_usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 4) GDPR/DSA (consent, visibilidad, DSAR, moderación, AMAR)
-- =====================================================================
CREATE TABLE IF NOT EXISTS consent_versions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  scope ENUM('exposure_public','analytics','marketing','parental','other') NOT NULL,
  version VARCHAR(32) NOT NULL,
  text_hash CHAR(64) NOT NULL,
  published_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_scope_version (scope, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS consents (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  scope ENUM('exposure_public','analytics','marketing','parental','other') NOT NULL,
  decision BOOLEAN NOT NULL,
  surface VARCHAR(64) NOT NULL,
  version VARCHAR(32) NOT NULL,
  ip VARCHAR(64),
  ua VARCHAR(255),
  locale VARCHAR(16),
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  revoked_at DATETIME(3),
  KEY idx_consents_user_scope (user_id, scope, created_at),
  KEY idx_consents_user_created (user_id, created_at),
  CONSTRAINT fk_consents_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_consents_version FOREIGN KEY (scope, version) REFERENCES consent_versions(scope, version)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS visibility_changes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  challenge_id BIGINT NOT NULL,
  from_visibility ENUM('private','validators','public') NOT NULL,
  to_visibility   ENUM('private','validators','public') NOT NULL,
  reason VARCHAR(255),
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_vis_user_challenge (user_id, challenge_id),
  CONSTRAINT fk_vis_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_vis_reto FOREIGN KEY (challenge_id) REFERENCES reto(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS privacy_request (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  type ENUM('access','rectification','erasure','restriction','portability','objection') NOT NULL,
  status ENUM('received','in_progress','completed','rejected') NOT NULL DEFAULT 'received',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NULL,
  notes TEXT,
  KEY idx_privreq_user_status (user_id, status, created_at),
  CONSTRAINT fk_priv_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS moderation_report (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reporter_user_id BIGINT NULL,
  target_content_id BIGINT NOT NULL,
  target_content_type ENUM('challenge','evidence','comment','profile') NOT NULL,
  reason ENUM('copyright','privacy','intimate','harassment','hate','other') NOT NULL,
  description TEXT,
  url VARCHAR(512),
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  status ENUM('received','triaged','actioned','dismissed') NOT NULL DEFAULT 'received',
  KEY idx_mod_report_status_created (status, created_at),
  CONSTRAINT fk_mod_reporter FOREIGN KEY (reporter_user_id) REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS moderation_action (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  report_id BIGINT NOT NULL,
  action ENUM('remove','restrict','suspend','no_action') NOT NULL,
  reason_code VARCHAR(64) NOT NULL,
  statement TEXT NOT NULL,
  notified_user_id BIGINT NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_mod_action_report (report_id),
  KEY idx_mod_action_user_created (notified_user_id, created_at),
  CONSTRAINT fk_mod_action_report FOREIGN KEY (report_id) REFERENCES moderation_report(id) ON DELETE CASCADE,
  CONSTRAINT fk_mod_action_user FOREIGN KEY (notified_user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS moderation_appeal (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  action_id BIGINT NOT NULL,
  appellant_user_id BIGINT NOT NULL,
  message TEXT NOT NULL,
  status ENUM('received','upheld','reversed','partial') NOT NULL DEFAULT 'received',
  decided_at DATETIME(3) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_mod_appeal_action (action_id),
  KEY idx_mod_appeal_status_created (status, created_at),
  CONSTRAINT fk_mod_appeal_action FOREIGN KEY (action_id) REFERENCES moderation_action(id) ON DELETE CASCADE,
  CONSTRAINT fk_mod_appeal_user FOREIGN KEY (appellant_user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS security_incident (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  detected_at DATETIME(3) NOT NULL,
  severity ENUM('low','medium','high') NOT NULL,
  description TEXT,
  scope TEXT,
  contained_at DATETIME(3) NULL,
  root_cause TEXT,
  status ENUM('open','closed') NOT NULL DEFAULT 'open'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS breach_assessment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  incident_id BIGINT NOT NULL,
  risk_level ENUM('none','low','medium','high') NOT NULL,
  dpia_impact TEXT,
  notify_authority BOOLEAN NOT NULL DEFAULT 0,
  notify_subjects  BOOLEAN NOT NULL DEFAULT 0,
  assessed_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_breach_incident (incident_id),
  CONSTRAINT fk_breach_incident FOREIGN KEY (incident_id) REFERENCES security_incident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS breach_notification (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  incident_id BIGINT NOT NULL,
  channel ENUM('authority','email','in_app','other') NOT NULL,
  sent_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  content TEXT,
  KEY idx_notification_incident (incident_id),
  CONSTRAINT fk_breach_notif_incident FOREIGN KEY (incident_id) REFERENCES security_incident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS amar_metrics (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  period_start DATE NOT NULL,
  period_end   DATE NOT NULL,
  eu_monthly_active_recipients BIGINT NOT NULL,
  methodology  TEXT,
  published_at DATETIME(3),
-- =====================================================================
-- 5) Media normalizada
-- =====================================================================
CREATE TABLE IF NOT EXISTS media_objects (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  challenge_id BIGINT NOT NULL,
  kind ENUM('text','image','video') NOT NULL,
  storage_key VARCHAR(256) NOT NULL,
  sha256 CHAR(64) NOT NULL,
  size_bytes BIGINT NOT NULL,
  mime VARCHAR(128) NOT NULL,
  width INT NULL,
  height INT NULL,
  duration_sec INT NULL,
  visibility ENUM('private','validators','public') NOT NULL,
  antivirus_status ENUM('pending','clean','infected') NOT NULL DEFAULT 'pending',
  exif_stripped BOOLEAN NOT NULL DEFAULT TRUE,
  text_content TEXT,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_media_sha (sha256),
  KEY idx_media_challenge_created (challenge_id, created_at),
  KEY idx_media_user_created (user_id, created_at),
  KEY idx_media_visibility_created (visibility, created_at),
  CONSTRAINT fk_media_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_media_reto FOREIGN KEY (challenge_id) REFERENCES reto(id) ON DELETE CASCADE,
  CONSTRAINT chk_media_antivirus_visibility CHECK (antivirus_status <> 'infected' OR visibility = 'private')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 6) Gamificación + Puntos
-- =====================================================================
CREATE TABLE IF NOT EXISTS badges (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(255),
  criteria JSON,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  KEY idx_badges_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_badges (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  badge_id BIGINT NOT NULL,
  granted_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_user_badge (user_id, badge_id),
  KEY idx_user (user_id),
  CONSTRAINT fk_user_badges_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_badges_badge FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS points_ledger (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  points INT NOT NULL,
  reason VARCHAR(128) NOT NULL,
  related_entity_type VARCHAR(64),
  related_entity_id BIGINT,
  ts DATETIME(3) NOT NULL,
  KEY idx_pl_user_ts (user_id, ts),
  KEY idx_points_reason_ts (reason, ts),
  CONSTRAINT fk_pl_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 7) Hábitos / Rachas
-- =====================================================================
CREATE TABLE IF NOT EXISTS habit_streaks (
  user_id BIGINT PRIMARY KEY,
  current_streak_days INT NOT NULL DEFAULT 0,
  longest_streak_days INT NOT NULL DEFAULT 0,
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT fk_hs_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS habit_metrics_daily (
  user_id BIGINT NOT NULL,
  day DATE NOT NULL,
  submissions INT NOT NULL DEFAULT 0,
  accepted INT NOT NULL DEFAULT 0,
  PRIMARY KEY (user_id, day),
  CONSTRAINT fk_hmd_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 8) Mentoring
-- =====================================================================
CREATE TABLE IF NOT EXISTS tutor_assignments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tutor_user_id BIGINT NOT NULL,
  student_user_id BIGINT NOT NULL,
  challenge_id BIGINT NULL,
  status ENUM('active','paused','ended') NOT NULL DEFAULT 'active',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_pair (tutor_user_id, student_user_id, challenge_id),
  KEY idx_tutor_assign_status (status, created_at),
  CONSTRAINT fk_tutor_tutor FOREIGN KEY (tutor_user_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_tutor_student FOREIGN KEY (student_user_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_tutor_chal FOREIGN KEY (challenge_id) REFERENCES reto(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tutor_sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  assignment_id BIGINT NOT NULL,
  scheduled_at DATETIME(3) NOT NULL,
  duration_min INT NULL,
  mode ENUM('chat','video','in_person') NOT NULL,
  notes TEXT,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_tutor_sessions_when (scheduled_at),
  CONSTRAINT fk_tutor_sessions_assign FOREIGN KEY (assignment_id) REFERENCES tutor_assignments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tutor_feedback (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  assignment_id BIGINT NOT NULL,
  given_by ENUM('tutor','student') NOT NULL,
  score TINYINT NOT NULL,
  comment VARCHAR(512) NULL,
  ts DATETIME(3) NOT NULL,
  CONSTRAINT fk_tutor_feedback_assign FOREIGN KEY (assignment_id) REFERENCES tutor_assignments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 9) Eventos de producto / Encuestas
-- =====================================================================
CREATE TABLE IF NOT EXISTS events (
  event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  event_type VARCHAR(64) NOT NULL,
  timestamp DATETIME(3) NOT NULL,
  props JSON,
  request_id VARCHAR(64),
  trace_id VARCHAR(64),
  KEY idx_user_type_ts (user_id, event_type, timestamp),
  KEY idx_ts (timestamp),
  KEY idx_events_type_ts (event_type, timestamp),
  CONSTRAINT fk_events_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS surveys (
  survey_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  type ENUM('sean_ellis','nps','love') NOT NULL,
  answers JSON NOT NULL,
  ts DATETIME(3) NOT NULL,
  KEY idx_survey_user (user_id, ts),
  KEY idx_surveys_type_ts (type, ts),
  CONSTRAINT fk_surveys_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 10) Seguridad
-- =====================================================================
CREATE TABLE IF NOT EXISTS security_incidents (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  incident_type ENUM('suspicious_login','data_breach','abuse','spam','fraud') NOT NULL,
  severity ENUM('low','medium','high','critical') NOT NULL,
  description TEXT NOT NULL,
  evidence JSON,
  status ENUM('open','investigating','resolved','false_positive') NOT NULL DEFAULT 'open',
  reported_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  resolved_at DATETIME(3) NULL,
  KEY idx_security_incidents_status (status, reported_at),
  KEY idx_security_incidents_user (user_id, reported_at),
  CONSTRAINT fk_security_incidents_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS breach_assessments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  incident_id BIGINT NOT NULL,
  affected_users_count INT NOT NULL,
  data_categories_affected JSON NOT NULL,
  risk_assessment TEXT,
  mitigation_plan TEXT,
  assessed_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  UNIQUE KEY uniq_breach_incident (incident_id),
  CONSTRAINT fk_breach_incidents FOREIGN KEY (incident_id) REFERENCES security_incidents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  action VARCHAR(128) NOT NULL,
  resource_type VARCHAR(64) NOT NULL,
  resource_id BIGINT NULL,
  old_values JSON,
  new_values JSON,
  ip_address VARCHAR(45) NULL,
  user_agent TEXT,
  ts DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_audit_user_ts (user_id, ts),
  KEY idx_audit_action_ts (action, ts),
  KEY idx_audit_resource (resource_type, resource_id),
  CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 11) Billing / Monetización
-- =====================================================================
CREATE TABLE IF NOT EXISTS subscriptions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  plan_id VARCHAR(64) NOT NULL,
  status ENUM('active','canceled','past_due','unpaid') NOT NULL,
  current_period_start DATETIME(3) NOT NULL,
  current_period_end DATETIME(3) NOT NULL,
  cancel_at_period_end BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_subscriptions_user_status (user_id, status),
  KEY idx_subscriptions_period_end (current_period_end),
  CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  subscription_id BIGINT NULL,
  amount_cents BIGINT NOT NULL,
  currency VARCHAR(3) NOT NULL DEFAULT 'EUR',
  status ENUM('pending','succeeded','failed','canceled','refunded') NOT NULL,
  payment_method VARCHAR(64) NOT NULL,
  external_id VARCHAR(128) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_payments_user_status (user_id, status),
  KEY idx_payments_created (created_at),
  CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_payments_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 12) Referrals
-- =====================================================================
CREATE TABLE IF NOT EXISTS referral_codes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  code VARCHAR(32) NOT NULL UNIQUE,
  max_uses INT NULL,
  used_count INT NOT NULL DEFAULT 0,
  expires_at DATETIME(3) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  KEY idx_referral_codes_code (code),
  KEY idx_referral_codes_user (user_id),
  CONSTRAINT fk_referral_codes_user FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS referral_uses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  referral_code_id BIGINT NOT NULL,
  new_user_id BIGINT NOT NULL,
  used_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  reward_granted BOOLEAN NOT NULL DEFAULT FALSE,
  UNIQUE KEY uniq_referral_use (referral_code_id, new_user_id),
  KEY idx_referral_uses_new_user (new_user_id),
  CONSTRAINT fk_referral_uses_code FOREIGN KEY (referral_code_id) REFERENCES referral_codes(id) ON DELETE CASCADE,
  CONSTRAINT fk_referral_uses_new_user FOREIGN KEY (new_user_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 13) Índices adicionales para optimización
-- =====================================================================
CREATE INDEX idx_reto_user_status_created ON reto(user_id, status, created_at);
CREATE INDEX idx_reto_validators_needed ON reto(validators_needed, status);
CREATE INDEX idx_reto_deadline ON reto(deadline);
CREATE INDEX idx_reto_category_status ON reto(category, status);

CREATE INDEX idx_validation_reto_status ON validation(reto_id, status);
CREATE INDEX idx_validation_validator_ts ON validation(validator_id, created_at);

CREATE INDEX idx_media_user_visibility ON media_objects(user_id, visibility, created_at);
CREATE INDEX idx_media_challenge_visibility ON media_objects(challenge_id, visibility);

CREATE INDEX idx_events_user_type ON events(user_id, event_type, timestamp);
CREATE INDEX idx_events_type_timestamp ON events(event_type, timestamp);

CREATE INDEX idx_audit_resource_ts ON audit_log(resource_type, resource_id, ts);
CREATE INDEX idx_audit_ts ON audit_log(ts);

-- =====================================================================
-- 14) Triggers para auditoría automática
-- =====================================================================
DELIMITER //

CREATE TRIGGER audit_usuario_changes
AFTER UPDATE ON usuario
FOR EACH ROW
BEGIN
  INSERT INTO audit_log (user_id, action, resource_type, resource_id, old_values, new_values, ts)
  VALUES (
    NEW.id,
    'UPDATE',
    'usuario',
    NEW.id,
    JSON_OBJECT(
      'email', OLD.email,
      'username', OLD.username,
      'status', OLD.status,
      'updated_at', OLD.updated_at
    ),
    JSON_OBJECT(
      'email', NEW.email,
      'username', NEW.username,
      'status', NEW.status,
      'updated_at', NEW.updated_at
    ),
    NOW(3)
  );
END //

CREATE TRIGGER audit_reto_changes
AFTER UPDATE ON reto
FOR EACH ROW
BEGIN
  INSERT INTO audit_log (user_id, action, resource_type, resource_id, old_values, new_values, ts)
  VALUES (
    NEW.user_id,
    'UPDATE',
    'reto',
    NEW.id,
    JSON_OBJECT(
      'title', OLD.title,
      'status', OLD.status,
      'deadline', OLD.deadline,
      'updated_at', OLD.updated_at
    ),
    JSON_OBJECT(
      'title', NEW.title,
      'status', NEW.status,
      'deadline', NEW.deadline,
      'updated_at', NEW.updated_at
    ),
    NOW(3)
  );
END //

DELIMITER ;

-- =====================================================================
-- 15) Vistas útiles para reporting
-- =====================================================================
CREATE OR REPLACE VIEW v_user_stats AS
SELECT
  u.id,
  u.username,
  u.email,
  u.created_at,
  COUNT(DISTINCT r.id) as total_challenges,
  COUNT(DISTINCT CASE WHEN r.status = 'completed' THEN r.id END) as completed_challenges,
  COUNT(DISTINCT CASE WHEN r.status = 'validated' THEN r.id END) as validated_challenges,
  COUNT(DISTINCT CASE WHEN r.status = 'rejected' THEN r.id END) as rejected_challenges,
  COUNT(DISTINCT v.id) as total_validations,
  COUNT(DISTINCT CASE WHEN v.status = 'approved' THEN v.id END) as approved_validations,
  COUNT(DISTINCT ub.id) as total_badges,
  COALESCE(SUM(pl.points), 0) as total_points,
  hs.current_streak_days,
  hs.longest_streak_days
FROM usuario u
LEFT JOIN reto r ON u.id = r.user_id
LEFT JOIN validation v ON u.id = v.validator_id
LEFT JOIN user_badges ub ON u.id = ub.user_id
LEFT JOIN points_ledger pl ON u.id = pl.user_id
LEFT JOIN habit_streaks hs ON u.id = hs.user_id
GROUP BY u.id, u.username, u.email, u.created_at, hs.current_streak_days, hs.longest_streak_days;

CREATE OR REPLACE VIEW v_challenge_stats AS
SELECT
  r.id,
  r.title,
  r.category,
  r.status,
  r.created_at,
  r.deadline,
  r.validators_needed,
  COUNT(DISTINCT v.id) as validations_received,
  COUNT(DISTINCT CASE WHEN v.status = 'approved' THEN v.id END) as validations_approved,
  COUNT(DISTINCT CASE WHEN v.status = 'rejected' THEN v.id END) as validations_rejected,
  COUNT(DISTINCT m.id) as media_count,
  AVG(CASE WHEN v.score IS NOT NULL THEN v.score END) as avg_validation_score
FROM reto r
LEFT JOIN validation v ON r.id = v.reto_id
LEFT JOIN media_objects m ON r.id = m.challenge_id
GROUP BY r.id, r.title, r.category, r.status, r.created_at, r.deadline, r.validators_needed;

-- =====================================================================
-- FIN DEL ESQUEMA DE BASE DE DATOS
-- =====================================================================

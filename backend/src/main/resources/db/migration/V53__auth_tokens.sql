-- Phase 3 (Auth): Persistencia de magic links / tokens de autenticación
-- Tabla genérica para futuros tipos de tokens (MAGIC, REFRESH, RECOVERY, etc.)
CREATE TABLE IF NOT EXISTS auth_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(64) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auth_tokens_usuario FOREIGN KEY (user_id) REFERENCES usuarios(id)
);

CREATE INDEX IF NOT EXISTS idx_auth_tokens_user ON auth_tokens(user_id);
-- Índice único ya implícito por UNIQUE(token) garantiza idempotencia.

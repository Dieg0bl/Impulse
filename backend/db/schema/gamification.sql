-- IMPULSE: Sistema de Gamificación
-- Todas las entidades y reglas reflejadas 1:1 con el frontend

-- Rachas de usuario
CREATE TABLE user_streak (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    current_streak INT NOT NULL,
    longest_streak INT NOT NULL,
    last_activity_date TIMESTAMP NOT NULL,
    freezes_used INT NOT NULL,
    freezes_available INT NOT NULL,
    streak_type VARCHAR(32) CHECK (streak_type IN ('daily_activity', 'validation_streak', 'challenge_completion')),
    is_active BOOLEAN NOT NULL
);

-- Freezes de racha
CREATE TABLE streak_freeze (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    freeze_date TIMESTAMP NOT NULL,
    reason VARCHAR(32) CHECK (reason IN ('user_requested', 'validator_delay', 'system_suggested')),
    duration INT NOT NULL, -- horas
    is_active BOOLEAN NOT NULL
);

-- Ligas personales
CREATE TABLE personal_league (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    tier VARCHAR(16) CHECK (tier IN ('bronze', 'silver', 'gold', 'platinum', 'diamond')),
    min_validation_rate NUMERIC(4,3) NOT NULL,
    min_cred_points INT NOT NULL,
    min_streak_days INT NOT NULL,
    window_days INT NOT NULL,
    badge_icon VARCHAR(8),
    frame_color VARCHAR(16),
    cred_bonus NUMERIC(4,2),
    prestige_points INT,
);

-- Misiones
CREATE TABLE mission (
    id VARCHAR(64) PRIMARY KEY,
    type VARCHAR(16) CHECK (type IN ('daily', 'weekly', 'monthly')),
    category VARCHAR(16) CHECK (category IN ('social', 'progress', 'quality', 'consistency')),
    name VARCHAR(64) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
);

-- Objetivos de misión
CREATE TABLE mission_objective (
    id VARCHAR(64) PRIMARY KEY,
    mission_id VARCHAR(64) NOT NULL,
    action VARCHAR(64) NOT NULL,
    target_count INT NOT NULL,
    current_count INT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    description TEXT
);

-- Tabla para eventos temáticos (ThematicEvent)
CREATE TABLE IF NOT EXISTS thematic_event (
     id VARCHAR(64) PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     description TEXT,
     start_date DATE,
     end_date DATE,
     theme VARCHAR(128),
     reward_type VARCHAR(128),
     reward_value VARCHAR(128),
     is_active BOOLEAN DEFAULT TRUE
);

-- Recompensas de misión
CREATE TABLE mission_reward (
    id SERIAL PRIMARY KEY,
    mission_id VARCHAR(64) NOT NULL,
    type VARCHAR(16) CHECK (type IN ('currency', 'chest', 'item', 'badge')),
    currency VARCHAR(32),
    amount NUMERIC(16,2),
    chest_id VARCHAR(64),
    item_id VARCHAR(64),
    badge_id VARCHAR(64),
    name VARCHAR(64),
    FOREIGN KEY (mission_id) REFERENCES mission(id)
);

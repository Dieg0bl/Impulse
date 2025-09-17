-- IMPULSE: Sistema de Coaching
-- Todas las entidades y reglas reflejadas 1:1 con el frontend

-- Niveles de coaching
CREATE TABLE coaching_tier (
    tier VARCHAR(16) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    description TEXT,
    monthly_price NUMERIC(10,2) NOT NULL,
    response_time_hours INT NOT NULL,
    monthly_interactions INT,
    includes_video_calls BOOLEAN,
    personalized_plan BOOLEAN,
    priority_support BOOLEAN
);

-- Características de cada nivel
CREATE TABLE coaching_feature (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    description TEXT,
    type VARCHAR(16) CHECK (type IN ('messaging', 'planning', 'calls', 'analysis', 'priority')),
    unlimited_usage BOOLEAN NOT NULL,
    monthly_quota INT,
    tier VARCHAR(16) NOT NULL,
    FOREIGN KEY (tier) REFERENCES coaching_tier(tier)
);

-- Perfil de coach humano
CREATE TABLE coach_profile (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    photo VARCHAR(256),
    specializations TEXT,
    languages TEXT,
    timezone VARCHAR(32),
    years_experience INT,
    certifications_count INT,
    total_students INT,
    average_rating NUMERIC(3,2),
    success_rate NUMERIC(5,2),
    is_active BOOLEAN
);

-- Plan personalizado por usuario
CREATE TABLE personal_plan (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    coach_id VARCHAR(64) NOT NULL,
    plan_details TEXT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_active BOOLEAN,
    FOREIGN KEY (coach_id) REFERENCES coach_profile(id)
);

-- Sesiones de coaching (incluye video calls)
CREATE TABLE coaching_session (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    coach_id VARCHAR(64) NOT NULL,
    session_type VARCHAR(16) CHECK (session_type IN ('message', 'video_call', 'analysis', 'plan')),
    scheduled_date TIMESTAMP,
    duration_minutes INT,
    notes TEXT,
    completed BOOLEAN,
    FOREIGN KEY (coach_id) REFERENCES coach_profile(id)
);

-- Historial de interacciones coach-usuario
CREATE TABLE coaching_interaction (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    coach_id VARCHAR(64) NOT NULL,
    interaction_type VARCHAR(16) CHECK (interaction_type IN ('message', 'call', 'plan', 'feedback')),
    content TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (coach_id) REFERENCES coach_profile(id)
);

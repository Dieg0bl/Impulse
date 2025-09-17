-- IMPULSE: Sistema de Validación SLA
-- Todas las entidades y reglas reflejadas 1:1 con el frontend

-- Niveles SLA
CREATE TABLE validation_sla (
    level VARCHAR(16) PRIMARY KEY,
    timeout_hours INT NOT NULL,
    sla_reward DECIMAL(16,2) NOT NULL,
    description TEXT,
    user_message TEXT,
    coach_message TEXT
);

-- Perfil de validador
CREATE TABLE validator_profile (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    name VARCHAR(64) NOT NULL,
    average_response_hours DECIMAL(6,2),
    optimal_validations INT,
    standard_validations INT,
    delayed_validations INT,
    timeout_validations INT,
    total_validations INT,
    sla_score DECIMAL(5,2),
    current_streak INT,
    last_activity_date TIMESTAMP,
    is_active TINYINT(1),
    max_daily_validations INT,
    current_daily_validations INT,
    timezone VARCHAR(32),
    auto_assign_enabled TINYINT(1),
    reputation_score DECIMAL(6,2),
    specializations TEXT,
    language_preferences TEXT
);

-- Solicitud de validación
CREATE TABLE validation_request (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    challenge_id VARCHAR(64) NOT NULL,
    evidence_id VARCHAR(64) NOT NULL,
    request_date TIMESTAMP NOT NULL,
    assigned_validator_id VARCHAR(64),
    backup_validator_ids TEXT,
    current_sla_level VARCHAR(16),
    escalation_level INT,
    is_redistributed TINYINT(1),
    urgency_boost DECIMAL(4,2),
    status VARCHAR(16), -- CHECK (status IN ('pending', 'assigned', 'in_review', 'completed', 'timeout')),
    completed_date TIMESTAMP,
    actual_response_hours DECIMAL(6,2),
    FOREIGN KEY (current_sla_level) REFERENCES validation_sla(level)
);

-- Pool de validadores
CREATE TABLE validator_pool (
    id INT AUTO_INCREMENT PRIMARY KEY,
    region VARCHAR(32),
    timezone VARCHAR(32),
    available_validators TEXT,
    avg_response_time DECIMAL(6,2),
    capacity_utilization DECIMAL(5,2),
    last_updated TIMESTAMP
);

-- Reglas de redistribución
CREATE TABLE redistribution_rule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trigger_type VARCHAR(32), -- CHECK (trigger_type IN ('timeout_reached', 'validator_inactive', 'capacity_exceeded', 'user_request')),
    timeout_hours INT,
    redistribution_strategy VARCHAR(32), -- CHECK (redistribution_strategy IN ('nearest_available', 'best_sla_score', 'timezone_match', 'specialization_match')),
    compensation_required TINYINT(1),
    compensation_amount DECIMAL(16,2),
    notification_template VARCHAR(64)
);

-- Notificaciones SLA
CREATE TABLE sla_notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(32), -- CHECK (type IN ('reminder', 'escalation', 'timeout_warning', 'redistribution')),
    recipient VARCHAR(16), -- CHECK (recipient IN ('validator', 'user', 'coach', 'admin')),
    trigger_hours INT,
    template VARCHAR(64),
    urgency VARCHAR(16), -- CHECK (urgency IN ('low', 'medium', 'high', 'critical')),
    include_compensation TINYINT(1)
);

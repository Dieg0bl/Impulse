-- Hardening indexes & minimal constraints (Phase 1 completion)
-- NOTE: Only additive, non-breaking operations.

-- Events: composite index to optimize user + type + time range queries
CREATE INDEX IF NOT EXISTS idx_events_user_type_ts ON events(user_id, event_type, timestamp);

-- Surveys: already has user index; add created_at for time filtering
CREATE INDEX IF NOT EXISTS idx_surveys_created_at ON surveys(created_at);

-- Validations: speed lookups by evidence / validator / status
CREATE INDEX IF NOT EXISTS idx_validations_evidence ON validations(reto_id);
CREATE INDEX IF NOT EXISTS idx_validations_validator ON validations(validator_id);
CREATE INDEX IF NOT EXISTS idx_validations_status ON validations(status);

-- Subscriptions: status filtering (if status column exists in current schema)
CREATE INDEX IF NOT EXISTS idx_subscriptions_user_status ON subscriptions(user_id, status);

-- Support tickets: user + status
CREATE INDEX IF NOT EXISTS idx_support_user_status ON support_tickets(user_id, status);

-- NPS / CSAT time filters
CREATE INDEX IF NOT EXISTS idx_nps_created_at ON nps_responses(created_at);
CREATE INDEX IF NOT EXISTS idx_csat_created_at ON csat_responses(created_at);

-- Economics counters: updated_at queries
CREATE INDEX IF NOT EXISTS idx_econ_updated_at ON economics_counters(updated_at);

-- Notification log: sent_at already partially indexed; add user+type
CREATE INDEX IF NOT EXISTS idx_notif_user_type ON notification_log(user_id, type);

-- Referral fraud audit (if table exists)
CREATE INDEX IF NOT EXISTS idx_referral_fraud_user ON referral_fraud_audit(user_id);

-- Press live counter is single-row; no index needed.

-- (Optional) Foreign keys are intentionally deferred or omitted to avoid flyway failures
-- in partially populated dev DBs and to keep write throughput high. Add in Phase 5 hardening if required.

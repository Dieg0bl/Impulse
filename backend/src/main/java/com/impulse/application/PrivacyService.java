package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.impulse.security.SecurityAuditService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class PrivacyService {
    private final JdbcTemplate jdbc;
    private final com.impulse.analytics.EventTracker tracker;
    private final SecurityAuditService audit;
    public PrivacyService(JdbcTemplate jdbc, com.impulse.analytics.EventTracker tracker, SecurityAuditService audit){this.jdbc=jdbc;this.tracker=tracker;this.audit=audit;}

    // --- Consents ---
    @Transactional
    public void captureConsent(Long userId, String scope, String version, boolean decision, String surface, String ip, String ua, String locale){
        jdbc.update("INSERT INTO consents(user_id, scope, decision, surface, version, ip, ua, locale) VALUES (?,?,?,?,?,?,?,?)",
            userId, scope, decision, surface, version, ip, ua, locale);
    audit.audit(userId, ip, "consent_captured", "consent", scope+":"+version, java.util.Map.of("decision", decision), "low");
        tracker.track(userId, "consent_captured", java.util.Map.of(
            "scope", scope,
            "version", version,
            "decision", decision,
            "surface", surface
        ), null, "api");
    }
    public boolean hasActiveConsent(Long userId, String scope){
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM consents WHERE user_id=? AND scope=? AND decision=1 AND revoked_at IS NULL", Integer.class, userId, scope);
        return c != null && c > 0;
    }
    @Transactional
    public void revokeConsent(Long userId, String scope){
        jdbc.update("UPDATE consents SET revoked_at=NOW() WHERE user_id=? AND scope=? AND revoked_at IS NULL", userId, scope);
    audit.audit(userId, null, "consent_revoked", "consent", scope, null, "medium");
        tracker.track(userId, "consent_revoked", java.util.Map.of("scope", scope), null, "api");
    }
    @Transactional
    public void registerConsentVersion(String scope, String version, String text){
        String hash = sha256(text);
        jdbc.update("INSERT IGNORE INTO consent_versions(scope, version, text_hash) VALUES (?,?,?)", scope, version, hash);
    }

    // --- Privacy Requests (DSAR) ---
    @Transactional
    public void requestExport(Long userId){
        jdbc.update("INSERT INTO privacy_request(user_id, type, status) VALUES (?,?,?)", userId, "access", "received");
    audit.audit(userId, null, "privacy_request_created", "privacy_request", "access", java.util.Map.of("status","received"), "low");
        tracker.track(userId, "privacy_export_requested", java.util.Map.of(), null, "api");
    }
    @Transactional
    public void requestDeletion(Long userId){
        jdbc.update("INSERT INTO privacy_request(user_id, type, status) VALUES (?,?,?)", userId, "erasure", "received");
    audit.audit(userId, null, "privacy_request_created", "privacy_request", "erasure", java.util.Map.of("status","received"), "medium");
        tracker.track(userId, "privacy_deletion_requested", java.util.Map.of(), null, "api");
    }
    @Transactional public void startRequest(long requestId){
        jdbc.update("UPDATE privacy_request SET status='in_progress', updated_at=NOW(), notes=CONCAT(IFNULL(notes,''),'\\nStarted at ', NOW()) WHERE id=? AND status='received'", requestId);
    audit.audit(null, null, "privacy_request_started", "privacy_request", String.valueOf(requestId), null, "low");
    }
    @Transactional public void completeRequest(long requestId, String exportLocation, String notes){
        jdbc.update("UPDATE privacy_request SET status='completed', updated_at=NOW(), export_location=?, notes=CONCAT(IFNULL(notes,''),'\\n',?) WHERE id=?", exportLocation, notes, requestId);
    audit.audit(null, null, "privacy_request_completed", "privacy_request", String.valueOf(requestId), java.util.Map.of("export_location", exportLocation), "low");
    }
    @Transactional public void rejectRequest(long requestId, String reason){
        jdbc.update("UPDATE privacy_request SET status='rejected', updated_at=NOW(), notes=CONCAT(IFNULL(notes,''),'\\nRejected: ',?) WHERE id=?", reason, requestId);
    audit.audit(null, null, "privacy_request_rejected", "privacy_request", String.valueOf(requestId), java.util.Map.of("reason", reason), "medium");
    }
    public java.util.List<java.util.Map<String,Object>> listRequests(String status, String type, Long userId, Integer limit){
        StringBuilder sql = new StringBuilder("SELECT id,user_id,type,status,created_at,updated_at,export_location FROM privacy_request WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if(status!=null){ sql.append(" AND status=?"); params.add(status);}    
        if(type!=null){ sql.append(" AND type=?"); params.add(type);}      
        if(userId!=null){ sql.append(" AND user_id=?"); params.add(userId);} 
        sql.append(" ORDER BY created_at DESC");
        if(limit==null||limit>200) limit=100; sql.append(" LIMIT ").append(limit);
        RowMapper<java.util.Map<String,Object>> rm = (rs,i)-> java.util.Map.of(
            "id", rs.getLong("id"),
            "user_id", rs.getLong("user_id"),
            "type", rs.getString("type"),
            "status", rs.getString("status"),
            "created_at", rs.getTimestamp("created_at").toInstant(),
            "updated_at", rs.getTimestamp("updated_at")==null? null: rs.getTimestamp("updated_at").toInstant(),
            "export_location", rs.getString("export_location")
        );
        return jdbc.query(sql.toString(), rm, params.toArray());
    }
    public java.util.Map<String,Object> generateExport(Long userId){
        return java.util.Map.of(
            "user_id", userId,
            "generated_at", java.time.Instant.now().toString(),
            "consents", jdbc.query("SELECT scope,decision,version,created_at,revoked_at FROM consents WHERE user_id=?", rs -> {
                java.util.List<java.util.Map<String,Object>> list = new java.util.ArrayList<>();
                while(rs.next()){
                    list.add(java.util.Map.of(
                        "scope", rs.getString(1),
                        "decision", rs.getBoolean(2),
                        "version", rs.getString(3),
                        "created_at", rs.getTimestamp(4).toInstant(),
                        "revoked_at", rs.getTimestamp(5)==null? null: rs.getTimestamp(5).toInstant()
                    ));
                }
                return list;
            }, userId)
        );
    }

    // --- Visibility changes audit ---
    @Transactional
    public void changeVisibility(Long userId, Long challengeId, String fromVisibility, String toVisibility, String reason){
        jdbc.update("INSERT INTO visibility_changes(user_id, challenge_id, from_visibility, to_visibility, reason) VALUES (?,?,?,?,?)",
            userId, challengeId, fromVisibility, toVisibility, reason);
    audit.audit(userId, null, "visibility_changed", "challenge", String.valueOf(challengeId), java.util.Map.of("from", fromVisibility, "to", toVisibility), "low");
        tracker.track(userId, "visibility_changed", java.util.Map.of(
            "challenge_id", challengeId,
            "from", fromVisibility,
            "to", toVisibility
        ), null, "api");
    }

    // --- Helpers ---
    private String sha256(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

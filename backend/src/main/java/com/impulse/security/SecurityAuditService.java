package com.impulse.security;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
public class SecurityAuditService {
    private final JdbcTemplate jdbc;
    private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    public SecurityAuditService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    @Transactional
    public void audit(Long actorUserId, String actorIp, String event, String targetType, String targetId, Map<String,Object> metadata, String severity){
        String json;
        try { json = metadata==null? null: mapper.writeValueAsString(metadata);} catch (Exception e){ json = null; }
        jdbc.update("INSERT INTO audit_log(actor_user_id, actor_ip, event, target_type, target_id, metadata, severity) VALUES (?,?,?,?,?,?,?)",
            actorUserId, actorIp, event, targetType, targetId, json, severity==null?"low":severity);
    }
}

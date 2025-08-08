package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.impulse.analytics.EventTracker;
import com.impulse.moderation.ModerationEnums.*;
import com.impulse.security.SecurityAuditService;

import java.util.Map;

@Service
public class ModerationService {
    private final JdbcTemplate jdbc;
    private final EventTracker tracker;
    private final SecurityAuditService audit;
    public ModerationService(JdbcTemplate jdbc, EventTracker tracker, SecurityAuditService audit){this.jdbc=jdbc;this.tracker=tracker;this.audit=audit;}

    @Transactional
    public long report(Long reporterUserId, Long targetContentId, String targetType, String reason, String description, String url){
        ContentType.valueOf(targetType); // valida
        ReportReason.valueOf(reason);
        Integer existing = jdbc.query("SELECT id FROM moderation_report WHERE reporter_user_id=? AND target_content_id=? AND target_content_type=? AND status IN ('received','triaged') LIMIT 1",
            rs -> rs.next()? rs.getInt(1): null, reporterUserId, targetContentId, targetType);
        if(existing!=null) return existing.longValue();
        jdbc.update("INSERT INTO moderation_report(reporter_user_id,target_content_id,target_content_type,reason,description,url,created_at,status) VALUES (?,?,?,?,?,?,NOW(),'received')",
            reporterUserId, targetContentId, targetType, reason, description, url);
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        long val = id == null ? 0L : id;
        tracker.track(reporterUserId, "content_report_submitted", Map.of(
            "report_id", val,
            "target_id", targetContentId,
            "target_type", targetType,
            "reason", reason
        ), null, "api");
        return val;
    }

    @Transactional
    public long action(long reportId, String action, String reasonCode, String statement, Long notifiedUserId, Long adminUserId){
        ActionType.valueOf(action);
        jdbc.update("INSERT INTO moderation_action(report_id,action,reason_code,statement,notified_user_id,admin_user_id,created_at) VALUES (?,?,?,?,?,?,NOW())",
            reportId, action, reasonCode, statement, notifiedUserId, adminUserId);
        jdbc.update("UPDATE moderation_report SET status='actioned' WHERE id=?", reportId);
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        long val = id == null ? 0L : id;
        tracker.track(notifiedUserId, "content_action_taken", Map.of(
            "action_id", val,
            "report_id", reportId,
            "action", action,
            "reason_code", reasonCode
        ), null, "api");
        audit.audit(adminUserId, null, "moderation_action", "report", String.valueOf(reportId), Map.of(
            "action_id", val,
            "action", action,
            "reason_code", reasonCode
        ), "medium");
        return val;
    }

    @Transactional
    public long appeal(long actionId, Long userId, String message){
        jdbc.update("INSERT INTO moderation_appeal(action_id,appellant_user_id,message,created_at,status) VALUES (?,?,?,NOW(),'received')",
            actionId, userId, message);
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        long val = id == null ? 0L : id;
        tracker.track(userId, "content_appeal_submitted", Map.of(
            "appeal_id", val,
            "action_id", actionId
        ), null, "api");
        return val;
    }

    @Transactional
    public void triage(long reportId, String newStatus){
        ReportStatus.valueOf(newStatus);
        jdbc.update("UPDATE moderation_report SET status=? WHERE id=? AND status='received'", newStatus, reportId);
        tracker.track(null, "content_report_triaged", Map.of("report_id", reportId, "status", newStatus), null, "api");
    }

    @Transactional
    public void resolveAppeal(long appealId, String finalStatus, Long adminUserId){
        AppealStatus.valueOf(finalStatus);
        jdbc.update("UPDATE moderation_appeal SET status=?, decided_at=NOW() WHERE id=?", finalStatus, appealId);
        if("reversed".equals(finalStatus)){
            Long actionId = jdbc.queryForObject("SELECT action_id FROM moderation_appeal WHERE id=?", Long.class, appealId);
            if(actionId!=null){
                Long reportId = jdbc.queryForObject("SELECT report_id FROM moderation_action WHERE id=?", Long.class, actionId);
                if(reportId!=null){
                    jdbc.update("UPDATE moderation_report SET status='dismissed' WHERE id=?", reportId);
                    tracker.track(null, "content_action_reversed", Map.of("report_id", reportId, "appeal_id", appealId), null, "api");
                }
            }
        }
        tracker.track(adminUserId, "content_appeal_resolved", Map.of("appeal_id", appealId, "status", finalStatus), null, "api");
    audit.audit(adminUserId, null, "appeal_resolved", "appeal", String.valueOf(appealId), Map.of("status", finalStatus), "low");
    }

    public java.util.List<Map<String,Object>> listReports(String status, String type, Integer limit){
        StringBuilder sql = new StringBuilder("SELECT id, reporter_user_id, target_content_id, target_content_type, reason, status, created_at FROM moderation_report WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if(status!=null){ sql.append(" AND status=?"); params.add(status); }
        if(type!=null){ sql.append(" AND target_content_type=?"); params.add(type); }
        sql.append(" ORDER BY created_at DESC");
        if(limit==null||limit>200) limit=50; sql.append(" LIMIT ").append(limit);
        RowMapper<Map<String,Object>> rm = (rs, i) -> Map.of(
            "id", rs.getLong("id"),
            "reporter_user_id", rs.getObject("reporter_user_id"),
            "target_content_id", rs.getLong("target_content_id"),
            "target_content_type", rs.getString("target_content_type"),
            "reason", rs.getString("reason"),
            "status", rs.getString("status"),
            "created_at", rs.getTimestamp("created_at").toInstant()
        );
        return jdbc.query(sql.toString(), rm, params.toArray());
    }

    public String fetchStatement(long actionId){
        return jdbc.query("SELECT statement FROM moderation_action WHERE id=?", rs -> rs.next()? rs.getString(1): null, actionId);
    }

    public Map<String,Object> reportStats(){
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM moderation_report", Long.class);
        Long open = jdbc.queryForObject("SELECT COUNT(*) FROM moderation_report WHERE status IN ('received','triaged')", Long.class);
        return Map.of("total", total==null?0:total, "open", open==null?0:open);
    }
}

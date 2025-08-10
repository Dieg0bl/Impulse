package com.impulse.monetization;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PlanService {
    private final JdbcTemplate jdbc;
    public PlanService(JdbcTemplate jdbc){ this.jdbc=jdbc; }

    public List<Map<String,Object>> listPlans(){
        return jdbc.queryForList("SELECT id,code,name,price_cents,currency,max_retos,max_validadores,max_invites_per_day,features_json,active FROM plans WHERE active=TRUE ORDER BY price_cents ASC");
    }

    public Map<String,Object> subscriptionStatus(Long userId){
        var list = jdbc.queryForList("SELECT s.*, p.code, p.name, p.max_retos, p.max_validadores, p.max_invites_per_day, p.features_json FROM user_subscriptions s JOIN plans p ON s.plan_id=p.id WHERE s.user_id=? AND s.status='ACTIVE' AND s.period_end>NOW() ORDER BY s.period_end DESC LIMIT 1", userId);
        if(list.isEmpty()){
            // fallback to FREE plan if exists
            var free = jdbc.queryForList("SELECT id,code,name,max_retos,max_validadores,max_invites_per_day,features_json FROM plans WHERE code='FREE' LIMIT 1");
            return free.isEmpty()? Map.of(): free.get(0);
        }
        return list.get(0);
    }

    public boolean canCreateReto(Long userId){
        Map<String,Object> sub = subscriptionStatus(userId);
        if(sub.isEmpty()) return true; // allow if something odd
        int limit = ((Number)sub.getOrDefault("max_retos",3)).intValue();
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM retos WHERE usuario_id=? AND deleted_at IS NULL", Integer.class, userId);
        return count < limit;
    }

    @Transactional
    public void applyPlan(Long userId, String planCode){
        var plans = jdbc.queryForList("SELECT id FROM plans WHERE code=? AND active=TRUE", planCode);
        if(plans.isEmpty()) throw new IllegalArgumentException("plan_invalido");
        Long planId = ((Number)plans.get(0).get("id")).longValue();
        // expire existing active
        jdbc.update("UPDATE user_subscriptions SET status='EXPIRED', updated_at=NOW() WHERE user_id=? AND status='ACTIVE'", userId);
        jdbc.update("INSERT INTO user_subscriptions(user_id,plan_id,status,period_start,period_end) VALUES (?,?,?,?,?)",
            userId, planId, "ACTIVE", java.sql.Timestamp.valueOf(LocalDateTime.now()), java.sql.Timestamp.valueOf(LocalDateTime.now().plusMonths(1)));
    }

    @Transactional
    public void cancel(Long userId){
        jdbc.update("UPDATE user_subscriptions SET status='CANCELED', cancel_at=NOW(), updated_at=NOW() WHERE user_id=? AND status='ACTIVE'", userId);
    }
}

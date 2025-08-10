package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Advanced economics KPIs (approximate heuristics): MRR, ARPU, churn_rate_30d, LTV.
 * Assumes plan.period in {MONTH,YEAR}; YEAR normalized to month by /12.
 */
@Service
public class EconomicsAdvancedService {
    private final JdbcTemplate jdbc;
    public EconomicsAdvancedService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    public Map<String,Object> summary(){
        Map<String,Object> out = new HashMap<>();
        double mrr = currentMRR();
        long activeUsers = activePayingUsers();
        double arpu = activeUsers==0?0.0:mrr/activeUsers;
        double churn = churnRate30d();
        double ltv = churn<=0.0001? arpu*12*3 : arpu / churn; // cap when churn ~0
        out.put("mrr", round(mrr));
        out.put("active_paying_users", activeUsers);
        out.put("arpu", round(arpu));
        out.put("churn_rate_30d", round(churn));
        out.put("ltv_estimate", round(ltv));
        out.put("currency", "USD");
        return out;
    }

    private double currentMRR(){
        Double cents = jdbc.queryForObject("SELECT COALESCE(SUM(CASE p.period WHEN 'MONTH' THEN p.price_cents WHEN 'YEAR' THEN p.price_cents/12 ELSE p.price_cents END),0) FROM subscriptions s JOIN plans p ON s.plan_id=p.id WHERE s.status='ACTIVE'", Double.class);
        return (cents==null?0.0:cents)/100.0;
    }

    private long activePayingUsers(){
        Long v = jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM subscriptions WHERE status='ACTIVE'", Long.class);
        return v==null?0L:v;
    }

    private double churnRate30d(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(30);
        Long base = jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM subscriptions WHERE started_at < ? AND (ends_at IS NULL OR ends_at > ?)", Long.class, from, from);
        if(base==null || base==0) return 0.0;
        Long canceled = jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM subscriptions WHERE status='CANCELED' AND ends_at BETWEEN ? AND ?", Long.class, from, now);
        long c = canceled==null?0L:canceled;
        return (double)c / base;
    }

    private double round(double v){ return Math.round(v*100.0)/100.0; }
}

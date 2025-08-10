package com.impulse.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import com.impulse.domain.reto.Reto;
import com.impulse.infrastructure.reto.RetoRepository;

/**
 * Service to compute user QuickWin (first small victory) and onboarding progress.
 * Phase 2 - Activation. Avoids heavy queries: uses simple heuristics.
 */
@Service
public class QuickWinService {
    private final RetoRepository retoRepository;
    private final JdbcTemplate jdbc;
    private static final List<String> DEFAULT_STEPS = List.of("welcome","choose_goal","first_evidence","share_success");
    public QuickWinService(RetoRepository retoRepository, JdbcTemplate jdbc) {
        this.retoRepository = retoRepository; this.jdbc=jdbc;
    }

    /**
     * Simple heuristic: first reto with estado COMPLETADO within 48h of creation -> quick win achieved.
     * Returns map with status and hoursToWin if achieved.
     */
    public Map<String,Object> quickWinStatus(Long userId) {
        Map<String,Object> out = new HashMap<>();
        var retos = retoRepository.findAll(); // TODO optimize with query by user when field indexed
        Optional<Reto> firstCompleted = retos.stream()
            .filter(r -> r.getUsuario()!=null && r.getUsuario().getId().equals(userId))
            .filter(r -> "COMPLETADO".equalsIgnoreCase(r.getEstado()))
            .sorted((a,b)->a.getCreatedAt().compareTo(b.getCreatedAt()))
            .findFirst();
        if(firstCompleted.isPresent()) {
            Reto r = firstCompleted.get();
            Duration d = Duration.between(r.getCreatedAt(), r.getUpdatedAt());
            out.put("achieved", d.toHours() <= 48);
            out.put("hoursToWin", d.toHours());
        } else {
            out.put("achieved", false);
        }
        return out;
    }

    /**
     * Onboarding progress heuristic: ratio of retos created in first 7 days that are COMPLETADO.
     */
    public Map<String,Object> onboardingProgress(Long userId) {
        Map<String,Object> out = new HashMap<>();
        var now = LocalDateTime.now();
        var retos = retoRepository.findAll();
        var early = retos.stream()
            .filter(r -> r.getUsuario()!=null && r.getUsuario().getId().equals(userId))
            .filter(r -> Duration.between(r.getCreatedAt(), now).toDays() <= 7)
            .toList();
        long total = early.size();
        long completed = early.stream().filter(r -> "COMPLETADO".equalsIgnoreCase(r.getEstado())).count();
        out.put("total", total);
        out.put("completed", completed);
        out.put("ratio", total==0?0.0:(double)completed/total);
        out.put("steps", fetchSteps(userId));
        out.put("completedSteps", completedSteps(userId));
        out.put("ahaAchieved", hasAha(userId));
        return out;
    }

    // ---------- Onboarding steps persistence (simple) ----------
    public List<Map<String,Object>> fetchSteps(Long userId){
        var rows = jdbc.queryForList("SELECT step_key, step_order, started_at, completed_at FROM onboarding_steps WHERE user_id=? ORDER BY step_order", userId);
        if(rows.isEmpty()){
            // seed
            for(int i=0;i<DEFAULT_STEPS.size();i++){
                jdbc.update("INSERT IGNORE INTO onboarding_steps(user_id, step_key, step_order) VALUES (?,?,?)", userId, DEFAULT_STEPS.get(i), i+1);
            }
            rows = jdbc.queryForList("SELECT step_key, step_order, started_at, completed_at FROM onboarding_steps WHERE user_id=? ORDER BY step_order", userId);
        }
        return rows;
    }

    public void startStep(Long userId, String key){
        jdbc.update("UPDATE onboarding_steps SET started_at=COALESCE(started_at,NOW()) WHERE user_id=? AND step_key=?", userId, key);
    }
    public boolean completeStep(Long userId, String key){
        int updated = jdbc.update("UPDATE onboarding_steps SET completed_at=COALESCE(completed_at,NOW()) WHERE user_id=? AND step_key=?", userId, key);
        return updated>0;
    }
    public int completedSteps(Long userId){
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM onboarding_steps WHERE user_id=? AND completed_at IS NOT NULL", Integer.class, userId);
        return c==null?0:c;
    }
    public boolean hasAha(Long userId){
        // aha if quick win achieved OR at least 3 steps completed including first_evidence
        boolean quick = Boolean.TRUE.equals(quickWinStatus(userId).get("achieved"));
        if(quick) return true;
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM onboarding_steps WHERE user_id=? AND completed_at IS NOT NULL", Integer.class, userId);
        if(c!=null && c>=3){
            Integer fe = jdbc.queryForObject("SELECT COUNT(*) FROM onboarding_steps WHERE user_id=? AND step_key='first_evidence' AND completed_at IS NOT NULL", Integer.class, userId);
            return fe!=null && fe>0;
        }
        return false;
    }
}

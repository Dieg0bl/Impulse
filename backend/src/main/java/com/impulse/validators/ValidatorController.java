package com.impulse.validators;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.impulse.common.flags.FlagService;
import org.springframework.http.ResponseEntity;
import java.time.Instant;import java.time.temporal.ChronoUnit;import java.util.*;import java.util.Date;

@RestController
@RequestMapping("/api/validators")
public class ValidatorController {
    private final JdbcTemplate jdbc;
    private final FlagService flags;

    public ValidatorController(JdbcTemplate jdbc, FlagService flags) {
        this.jdbc = jdbc;
        this.flags = flags;
    }

    private boolean enabled(){ return flags.isOn("validators.enabled"); }

    @PostMapping("/invite")
    public ResponseEntity<Object> invite(@RequestBody Map<String,String> body){
        if(!enabled()) return ResponseEntity.notFound().build();
    String email = body.get("email");
    Long inviter = Long.valueOf(body.getOrDefault("invited_by","1"));
        String token = UUID.randomUUID().toString().replace("-","");
        jdbc.update("INSERT INTO validator_invites(email,token,invited_by,expires_at) VALUES (?,?,?,?)",
                email, token, inviter, Date.from(Instant.now().plus(14, ChronoUnit.DAYS)));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/pending/{validatorId}")
    public ResponseEntity<Object> pending(@PathVariable Long validatorId){
        if(!enabled()) return ResponseEntity.notFound().build();
        var rows = jdbc.queryForList("SELECT * FROM validations WHERE validator_id=?", validatorId); // placeholder
        return ResponseEntity.ok(rows);
    }

    @PostMapping("/validate/{retoId}")
    public ResponseEntity<Object> validate(@PathVariable Long retoId, @RequestBody Map<String,String> body){
        if(!enabled()) return ResponseEntity.notFound().build();
        Long validatorId = Long.valueOf(body.getOrDefault("validator_id","0"));
        String status = body.getOrDefault("status","REJECTED");
        String feedback = body.getOrDefault("feedback","");
        if("REJECTED".equals(status) && feedback.isBlank()) return ResponseEntity.badRequest().body(Map.of("error","comment_required_on_reject"));
        jdbc.update("INSERT INTO validations(reto_id,validator_id,status,feedback) VALUES (?,?,?,?)", retoId, validatorId, status, feedback);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> stats(){
        if(!flags.isOn("validators.metrics")) return ResponseEntity.notFound().build();
        Long accepted = safeCount("SELECT COUNT(*) FROM validations WHERE status='ACCEPTED'");
        Long rejected = safeCount("SELECT COUNT(*) FROM validations WHERE status='REJECTED'");
        return ResponseEntity.ok(Map.of(
            "accepted", accepted,
            "rejected", rejected,
            "accept_rate", (accepted+rejected)==0?0.0: (double)accepted/(accepted+rejected)
        ));
    }

    private Long safeCount(String sql){
        try {
            Long val = jdbc.queryForObject(sql, Long.class);
            return val == null ? 0L : val;
        } catch (org.springframework.dao.EmptyResultDataAccessException ex){
            return 0L;
        }
    }
}

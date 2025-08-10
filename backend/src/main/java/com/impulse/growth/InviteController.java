package com.impulse.growth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.Map;import java.util.UUID;import java.util.HashMap;
import com.impulse.common.flags.FlagService;
import org.springframework.http.ResponseEntity;
import com.impulse.analytics.EventTracker;
import java.util.concurrent.ConcurrentHashMap;import java.time.Instant;

@RestController
@RequestMapping("/api/invites")
public class InviteController {
    private final JdbcTemplate jdbc; private final FlagService flags; private final EventTracker tracker;
    private final ConcurrentHashMap<Long, RateWindow> windows = new ConcurrentHashMap<>();
    private static final int MAX_PER_WINDOW = 5; // invites
    private static final long WINDOW_SECONDS = 3600; // 1h
    public InviteController(JdbcTemplate jdbc, FlagService flags, EventTracker tracker){ this.jdbc = jdbc; this.flags=flags; this.tracker=tracker; }

    private boolean enabled(){ return flags.isOn("growth.referrals"); }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String,String> body){
        if(!enabled()) return ResponseEntity.notFound().build();
        Long ref = Long.valueOf(body.getOrDefault("referrer_id","1"));
    if(isRateLimited(ref)) return ResponseEntity.status(429).body(Map.of("error","rate_limited"));
        String channel = body.getOrDefault("channel","link");
        String code = UUID.randomUUID().toString().replace("-","").substring(0,16);
        jdbc.update("INSERT INTO invites(referrer_id,channel,target,code) VALUES (?,?,?,?)", ref, channel, body.getOrDefault("target",""), code);
        try { tracker.track(ref, "invite_created", Map.of("channel", channel, "code_len", code.length()), null, "api"); } catch (Exception ignore) {}
        return ResponseEntity.ok(Map.of("code", code, "url", "https://impulse.app/join/"+code));
    }

    @GetMapping("/join/{code}")
    public ResponseEntity<?> join(@PathVariable String code){
        if(!enabled()) return ResponseEntity.notFound().build();
        var list = jdbc.queryForList("SELECT * FROM invites WHERE code=?", code);
        boolean ok = !list.isEmpty();
        try { tracker.track(null, "invite_join_checked", Map.of("code_present", ok), null, "api"); } catch (Exception ignore) {}
        if(!ok) return ResponseEntity.ok(Map.of("ok", false));
        return ResponseEntity.ok(Map.of("ok", true, "code", code));
    }

    @PostMapping("/convert/{code}")
    public ResponseEntity<?> convert(@PathVariable String code, @RequestBody(required=false) Map<String,Object> body){
        if(!enabled()) return ResponseEntity.notFound().build();
        var rows = jdbc.queryForList("SELECT * FROM invites WHERE code=?", code);
        if(rows.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","codigo_invalido"));
        var row = rows.get(0);
        if(Boolean.TRUE.equals(row.get("accepted"))){
            return ResponseEntity.ok(Map.of("already", true, "referrer_id", row.get("referrer_id")));
        }
        jdbc.update("UPDATE invites SET accepted=TRUE, accepted_at=NOW() WHERE id=?", row.get("id"));
        try { tracker.track(null, "invite_converted", Map.of("code", code, "referrer_id", row.get("referrer_id")), null, "api"); } catch (Exception ignore) {}
        return ResponseEntity.ok(Map.of("converted", true, "referrer_id", row.get("referrer_id")));
    }

    @GetMapping("/stats/{referrerId}")
    public ResponseEntity<?> stats(@PathVariable Long referrerId){
        if(!enabled()) return ResponseEntity.notFound().build();
        Integer totalObj = jdbc.queryForObject("SELECT COUNT(*) FROM invites WHERE referrer_id=?", Integer.class, referrerId);
        Integer acceptedObj = jdbc.queryForObject("SELECT COUNT(*) FROM invites WHERE referrer_id=? AND accepted=TRUE", Integer.class, referrerId);
        int total = totalObj==null?0:totalObj;
        int accepted = acceptedObj==null?0:acceptedObj;
        Map<String,Object> data = new HashMap<>();
        data.put("referrer_id", referrerId);
        data.put("total", total);
        data.put("accepted", accepted);
        data.put("conversion_rate", total>0 ? (double)accepted/total : 0.0);
        RateWindow w = windows.get(referrerId);
        if(w!=null) data.put("remaining", Math.max(0, MAX_PER_WINDOW - w.count));
        return ResponseEntity.ok(data);
    }

    private boolean isRateLimited(Long referrer){
        long now = Instant.now().getEpochSecond();
        windows.compute(referrer, (k,v) -> {
            if(v==null || now - v.windowStart > WINDOW_SECONDS){
                return new RateWindow(now,1);
            }
            v.count++; return v;
        });
        RateWindow w = windows.get(referrer);
        return w.count > MAX_PER_WINDOW;
    }

    static class RateWindow { long windowStart; int count; RateWindow(long s,int c){windowStart=s;count=c;} }
}

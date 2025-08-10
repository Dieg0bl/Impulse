package com.impulse.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/security/kill-switch")
public class KillSwitchController {
    private final KillSwitchService killSwitch;
    public KillSwitchController(KillSwitchService killSwitch){ this.killSwitch = killSwitch; }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggle(@RequestBody Map<String,Object> body, HttpServletRequest req){
        boolean active = Boolean.parseBoolean(String.valueOf(body.getOrDefault("active", "false")));
        killSwitch.setActive(active, currentUserId(), req.getRemoteAddr());
        return ResponseEntity.ok(Map.of("active", active));
    }

    @GetMapping
    public Map<String,Object> status(){ return Map.of("active", killSwitch.isActive()); }

    private Long currentUserId(){
        try{ Authentication a = SecurityContextHolder.getContext().getAuthentication(); if(a!=null) return Long.valueOf(a.getName()); }catch(Exception ignored){}
        return null;
    }
}

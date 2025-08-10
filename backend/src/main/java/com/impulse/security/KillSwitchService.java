package com.impulse.security;

import com.impulse.flags.FlagService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Runtime override for privacy kill switch with auditing.
 */
@Service
public class KillSwitchService {
    private final FlagService flags; private final SecurityAuditService audit;
    private volatile boolean overrideActive = false;
    public KillSwitchService(FlagService flags, SecurityAuditService audit){ this.flags=flags; this.audit=audit; }

    public boolean isActive(){ return overrideActive || flags.isEnabled("privacy.kill_switch"); }

    public void setActive(boolean active, Long actorUserId, String ip){
        boolean old = this.overrideActive;
        this.overrideActive = active;
        if(old != active){
            audit.audit(actorUserId, ip, "KILL_SWITCH_TOGGLED", "kill_switch", "privacy", Map.of("old", old, "new", active), "high");
        }
    }
}

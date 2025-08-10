package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.ReferralService;
import com.impulse.application.ReferralFraudService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/referrals")
public class ReferralController {
    private final ReferralService referralService;
    private final FlagService flags;
    private final ReferralFraudService fraudService;

    public ReferralController(ReferralService referralService, FlagService flags, ReferralFraudService fraudService){
        this.referralService = referralService;
        this.flags = flags;
        this.fraudService = fraudService;
    }

    private boolean enabled(){ return flags.isOn("growth.referrals"); }

    @PostMapping("/generate/{userId}")
    public ResponseEntity<?> generate(@PathVariable Long userId){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(referralService.generate(userId));
    }

    @PostMapping("/apply/{code}/{userId}")
    public ResponseEntity<?> apply(@PathVariable String code, @PathVariable Long userId){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(referralService.apply(code, userId));
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> stats(@PathVariable Long userId){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(referralService.stats(userId));
    }

    @GetMapping("/fraud/{userId}")
    public ResponseEntity<?> fraud(@PathVariable Long userId){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(java.util.Map.of("userId", userId, "suspicious", fraudService.suspiciousUser(userId)));
    }
}

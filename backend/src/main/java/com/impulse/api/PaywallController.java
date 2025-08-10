package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.PaywallService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/paywall")
public class PaywallController {
    private final PaywallService paywall;
    private final FlagService flags;

    public PaywallController(PaywallService paywall, FlagService flags){
        this.paywall = paywall;
        this.flags = flags;
    }

    private boolean enabled(){ return flags.isOn("monetization.paywall"); }

    @GetMapping("/plans")
    public ResponseEntity<?> plans(){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(paywall.listActivePlans());
    }

    @PostMapping("/subscribe/{userId}/{planCode}")
    public ResponseEntity<?> subscribe(@PathVariable Long userId, @PathVariable String planCode){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(paywall.subscribe(userId, planCode));
    }

    @GetMapping("/entitlement/{userId}/{feature}")
    public ResponseEntity<?> entitlement(@PathVariable Long userId, @PathVariable String feature){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(java.util.Map.of("feature", feature, "entitled", paywall.hasEntitlement(userId, feature)));
    }
}

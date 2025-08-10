package com.impulse.api.secured;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.impulse.monetizacion.RequiresPlan;

@RestController
@RequestMapping("/api/secured/feature")
public class SecuredFeatureController {

    @GetMapping("/premium-report/{userId}")
    @RequiresPlan("premium_report")
    public ResponseEntity<?> premiumReport(@PathVariable Long userId){
        return ResponseEntity.ok(java.util.Map.of("userId", userId, "report", "premium-metrics", "generated", true));
    }
}

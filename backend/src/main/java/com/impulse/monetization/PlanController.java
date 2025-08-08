package com.impulse.monetization;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/monetization")
public class PlanController {
    private final PlanService planService;
    public PlanController(PlanService planService){ this.planService=planService; }

    @GetMapping("/plans")
    public ResponseEntity<?> plans(){ return ResponseEntity.ok(planService.listPlans()); }

    @GetMapping("/status")
    public ResponseEntity<?> status(Principal principal){
        Long userId = extractUserId(principal);
        return ResponseEntity.ok(planService.subscriptionStatus(userId));
    }

    @PostMapping("/apply/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> apply(@PathVariable String code, @RequestParam Long userId){
        planService.applyPlan(userId, code);
        return ResponseEntity.ok(java.util.Map.of("applied", code));
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancel(Principal principal){
        Long userId = extractUserId(principal);
        planService.cancel(userId);
        return ResponseEntity.ok(java.util.Map.of("canceled", true));
    }

    private Long extractUserId(Principal p){
        // Replace with proper user lookup; placeholder 0L if null
        if(p==null) return 0L;
        // Could query userService by email to get ID; omitted for brevity
        return 0L;
    }
}

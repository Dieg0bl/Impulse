package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.ValidationService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {
    private final ValidationService service;
    private final FlagService flags;
    public ValidationController(ValidationService service, FlagService flags){this.service=service;this.flags=flags;}

    private boolean enabled(){return flags.isOn("validators.enabled");}

    @PostMapping("/submit/{retoId}/{validatorId}")
    public ResponseEntity<?> submit(@PathVariable Long retoId, @PathVariable Long validatorId, @RequestParam String status, @RequestParam(required=false) String feedback){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(service.submit(retoId, validatorId, status, feedback));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(service.list());
    }
}

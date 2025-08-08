package com.impulse.retention;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/retention")
public class RetentionController {
    private final RetentionService service;
    public RetentionController(RetentionService service){ this.service=service; }

    @PostMapping("/activity/{userId}")
    public ResponseEntity<?> record(@PathVariable Long userId){
        service.recordActivity(userId);
        return ResponseEntity.ok(service.getStreak(userId));
    }

    @GetMapping("/streak/{userId}")
    public ResponseEntity<?> streak(@PathVariable Long userId){
        return ResponseEntity.ok(service.getStreak(userId));
    }
}

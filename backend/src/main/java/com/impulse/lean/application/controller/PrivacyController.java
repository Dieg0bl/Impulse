package com.impulse.lean.application.controller;

import java.nio.file.Files;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.application.service.interfaces.UserService;
import com.impulse.lean.infrastructure.storage.StorageService;

@RestController
@RequestMapping("/api/v1/privacy")
public class PrivacyController {

    private static final String STATUS_KEY = "status";

    private final UserService userService;
    private final StorageService storageService;

    public PrivacyController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    @PostMapping("/export")
    public ResponseEntity<?> exportData(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
        if (userDetails == null) return ResponseEntity.status(401).build();
        String username = userDetails.getUsername();
        // find user by username
        var userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).build();

        // Build a simple export payload (JSON) - in real life we'd gather all user data
        String payload = "{\"username\":\"" + username + "\"}";
        String key = storageService.generateKey("privacy/exports", username + "-export.json");
        var path = java.nio.file.Paths.get(storageService.storeFromBytes(key, payload.getBytes()));
        if (Files.exists(path)) {
            return ResponseEntity.ok().body(java.util.Map.of(STATUS_KEY, "ready", "path", path.toString()));
        }
        return ResponseEntity.status(500).body("export_failed");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        String username = userDetails.getUsername();
        var userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).build();

        userService.deleteUserAccount(userOpt.get().getUuid());
        return ResponseEntity.ok().body(java.util.Map.of(STATUS_KEY, "scheduled"));
    }

    @PostMapping("/anonymize")
    public ResponseEntity<?> anonymize(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        String username = userDetails.getUsername();
        var userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).build();

        userService.anonymizeUserData(userOpt.get().getUuid());
        return ResponseEntity.ok().body(java.util.Map.of(STATUS_KEY, "anonymized"));
    }
}

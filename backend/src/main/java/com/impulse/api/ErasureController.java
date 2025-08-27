package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.infrastructure.db.Procedures;

@RestController
@RequestMapping("/api/privacy")
public class ErasureController {
    private final Procedures procedures;

    public ErasureController(Procedures procedures) {
        this.procedures = procedures;
    }

    @PostMapping("/erase/{userId}")
    public ResponseEntity<?> eraseUser(@PathVariable Long userId, @RequestParam String actor) {
        var result = procedures.insertAuditoriaAvanzada(userId, "gdpr_erasure", "", null, null, "privacy", "SUCCESS", "HIGH");
        // Llama al SP real de borrado GDPR (ajusta si tienes un método específico)
        procedures.callGdprErasure(userId, actor);
        return ResponseEntity.ok(result);
    }
}

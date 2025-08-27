package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.application.ports.ProceduresPort;

@RestController
@RequestMapping("/api/privacy")
public class ErasureController {
    private final ProceduresPort procedures;

    public ErasureController(ProceduresPort procedures) {
        this.procedures = procedures;
    }

    @PostMapping("/erase/{userId}")
    public ResponseEntity<Object> eraseUser(@PathVariable Long userId, @RequestParam String actor) {
        // No dependemos de la implementación infra; pasamos un Map con los campos mínimos
        var params = java.util.Map.<String,Object>of(
            "userId", userId,
            "procedure", "gdpr_erasure",
            "actor", actor,
            "category", "privacy",
            "result", "SUCCESS",
            "severity", "HIGH"
        );
        var result = procedures.insertAuditoriaAvanzada(params);
        // Llama al SP real de borrado GDPR (ajusta si tienes un método específico)
        procedures.callGdprErasure(userId, actor);
        return ResponseEntity.ok(result);
    }
}

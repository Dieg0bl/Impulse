package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.application.PrivacyService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/privacy")
public class PrivacyController {
    private final PrivacyService privacy;
    private final FlagService flags;

    public PrivacyController(PrivacyService privacy, FlagService flags) {
        this.privacy = privacy;
        this.flags = flags;
    }

    // Para capturar consentimiento
    @PostMapping("/consents/{userId}/{scope}/{version}")
    public ResponseEntity<?> capture(@PathVariable Long userId, @PathVariable String scope, @PathVariable String version, @RequestParam boolean decision, @RequestParam String surface, @RequestHeader(value="X-Forwarded-For",required=false) String ip, @RequestHeader(value="User-Agent",required=false) String ua, @RequestParam(required=false) String locale){
        privacy.captureConsent(userId, scope, version, decision, surface, ip, ua, locale);
        return ResponseEntity.accepted().build();
    }

    // Para obtener consentimientos activos de un usuario y scope
    @GetMapping("/consents/{userId}/{scope}")
    public ResponseEntity<?> has(@PathVariable Long userId, @PathVariable String scope){
        return ResponseEntity.ok(java.util.Map.of("active", privacy.hasActiveConsent(userId, scope)));
    }

    // Para revocar consentimiento
    @DeleteMapping("/consents/{userId}/{scope}")
    public ResponseEntity<?> revoke(@PathVariable Long userId, @PathVariable String scope) {
        privacy.revokeConsent(userId, scope);
        return ResponseEntity.accepted().build();
    }

    // Para solicitar exportación de datos
    @PostMapping("/export/{userId}")
    public ResponseEntity<?> requestExport(@PathVariable Long userId) {
        privacy.requestExport(userId);
        return ResponseEntity.accepted().build();
    }

    // Para solicitar eliminación de datos
    @PostMapping("/delete/{userId}")
    public ResponseEntity<?> requestDelete(@PathVariable Long userId) {
        privacy.requestDeletion(userId);
        return ResponseEntity.accepted().build();
    }

    // Para iniciar una solicitud
    @PostMapping("/requests/{requestId}/start")
    public ResponseEntity<?> start(@PathVariable long requestId) {
        privacy.startRequest(requestId);
        return ResponseEntity.ok().build();
    }

    public record CompletePayload(String exportLocation, String notes) {}

    // Para completar una solicitud
    @PostMapping("/requests/{requestId}/complete")
    public ResponseEntity<?> complete(@PathVariable long requestId, @RequestBody CompletePayload p) {
        privacy.completeRequest(requestId, p.exportLocation(), p.notes() == null ? "" : p.notes());
        return ResponseEntity.ok().build();
    }

    public record RejectPayload(String reason) {}

    // Para rechazar una solicitud
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<?> reject(@PathVariable long requestId, @RequestBody RejectPayload p) {
        privacy.rejectRequest(requestId, p.reason());
        return ResponseEntity.ok().build();
    }

    // Para listar solicitudes
    @GetMapping("/requests")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> requests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(privacy.listRequests(status, type, userId, limit));
    }

    // Para obtener datos exportados
    @GetMapping("/export/data/{userId}")
    public ResponseEntity<java.util.Map<String, Object>> exportData(@PathVariable Long userId) {
        return ResponseEntity.ok(privacy.generateExport(userId));
    }

    // Para registrar una nueva versión de consentimiento
    @PostMapping("/consents/version")
    public ResponseEntity<?> registerConsentVersion(@RequestParam String scope, @RequestParam String version, @RequestParam String text) {
        privacy.registerConsentVersion(scope, version, text);
        return ResponseEntity.ok().build();
    }

    public record VisibilityChangePayload(Long challengeId, String fromVisibility, String toVisibility, String reason) {}

    // Para cambiar la visibilidad de un desafío
    @PostMapping("/visibility/{userId}")
    public ResponseEntity<?> changeVisibility(@PathVariable Long userId, @RequestBody VisibilityChangePayload p) {
        privacy.changeVisibility(userId, p.challengeId(), p.fromVisibility(), p.toVisibility(), p.reason());
        return ResponseEntity.ok().build();
    }
}

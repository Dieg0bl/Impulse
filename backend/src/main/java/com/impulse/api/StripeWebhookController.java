package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import com.impulse.application.ports.StripeWebhookEventPort;
import com.impulse.domain.monetizacion.StripeWebhookEvent;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {
    private final StripeWebhookEventPort repo;
    private final FlagService flags;
    @Value("${stripe.webhook.secret:changeme}")
    private String webhookSecret;

    public StripeWebhookController(StripeWebhookEventPort repo, FlagService flags){
        this.repo = repo;
        this.flags = flags;
    }

    private boolean enabled(){ return flags.isOn("monetization.paywall"); }

    @PostMapping("/webhook")
    public ResponseEntity<java.util.Map<String,Object>> webhook(@RequestBody String payload,
                                     @RequestHeader(name="Stripe-Signature", required=false) String sig,
                                     @RequestHeader(name="Stripe-Event-Id", required=false) String providedEventId){
        if(!enabled()) return ResponseEntity.notFound().build();
        if(!validateSignature(sig, payload)){
            return ResponseEntity.status(400).body(java.util.Map.of("error","invalid_signature"));
        }
        String eventId = providedEventId != null && !providedEventId.isBlank() ? providedEventId : java.util.UUID.randomUUID().toString();
        // Idempotencia: si ya existe no volver a guardar
        if(repo.findByEventId(eventId).isPresent()){
            return ResponseEntity.ok(java.util.Map.of("stored", false, "duplicate", true, "id", eventId));
        }
        String type = extractType(payload);
        var evt = new StripeWebhookEvent(eventId, type, payload);
        repo.save(evt);
        return ResponseEntity.accepted().body(java.util.Map.of("stored", true, "id", eventId));
    }

    private String extractType(String payload){
        if (payload == null) return "unknown";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\\"type\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
        java.util.regex.Matcher m = p.matcher(payload);
        if (m.find()) {
            return m.group(1);
        }
        return "unknown";
    }

    private boolean validateSignature(String sigHeader, String payload){
        if (sigHeader == null || sigHeader.isBlank()) return false;
        java.util.Map<String,String> values = parseSignatureHeader(sigHeader);
        String v1 = values.get("v1");
        String t = values.get("t");
        if (v1 == null) return false;
        if (t == null) return false;
        String signedPayload = t + "." + payload;
        try {
            String expectedHex = computeHmacHex(webhookSecret, signedPayload);
            // Compare as byte arrays using constant-time comparison
            byte[] expectedBytes = java.util.HexFormat.of().parseHex(expectedHex);
            byte[] providedBytes = java.util.HexFormat.of().parseHex(v1);
            return java.security.MessageDigest.isEqual(expectedBytes, providedBytes);
        } catch (java.security.NoSuchAlgorithmException | java.security.InvalidKeyException | IllegalArgumentException ex) {
            // IllegalArgumentException can be thrown by parseHex if v1 is malformed
            return false;
        }
    }

    private java.util.Map<String,String> parseSignatureHeader(String header){
        var map = new java.util.HashMap<String,String>();
        if(header == null) return map;
        String[] parts = header.split(",");
        for(String p: parts){
            String[] kv = p.split("=",2);
            if(kv.length==2){
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    private String computeHmacHex(String secret, String signedPayload) throws java.security.NoSuchAlgorithmException, java.security.InvalidKeyException{
        var mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(signedPayload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return java.util.HexFormat.of().formatHex(digest);
    }
}

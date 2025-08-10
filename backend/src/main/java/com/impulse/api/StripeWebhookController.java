package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import com.impulse.infrastructure.monetizacion.StripeWebhookEventRepository;
import com.impulse.domain.monetizacion.StripeWebhookEvent;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {
    private final StripeWebhookEventRepository repo;
    private final FlagService flags;
    @Value("${stripe.webhook.secret:changeme}")
    private String webhookSecret;

    public StripeWebhookController(StripeWebhookEventRepository repo, FlagService flags){
        this.repo = repo;
        this.flags = flags;
    }

    private boolean enabled(){ return flags.isOn("monetization.paywall"); }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody String payload,
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
        try {
            int idx = payload.indexOf("\"type\"");
            if(idx>-1){
                int colon = payload.indexOf(':', idx);
                int quoteStart = payload.indexOf('"', colon+1);
                int quoteEnd = payload.indexOf('"', quoteStart+1);
                if(quoteStart>-1 && quoteEnd>-1){
                    return payload.substring(quoteStart+1, quoteEnd);
                }
            }
        } catch(Exception ignored){}
        return "unknown";
    }

    private boolean validateSignature(String sigHeader, String payload){
        if(sigHeader == null || sigHeader.isBlank()) return false;
        try {
            String[] parts = sigHeader.split(",");
            String v1 = null; String t = null;
            for(String p: parts){
                String[] kv = p.split("=",2);
                if(kv.length==2){
                    if("v1".equals(kv[0])) v1 = kv[1];
                    if("t".equals(kv[0])) t = kv[1];
                }
            }
            if(v1==null || t==null) return false;
            String signedPayload = t + "." + payload;
            var mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(webhookSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(signedPayload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String expected = java.util.HexFormat.of().formatHex(digest);
            if(expected.length()!=v1.length()) return false;
            int diff=0; for(int i=0;i<expected.length();i++){ diff |= expected.charAt(i) ^ v1.charAt(i); }
            return diff==0;
        } catch(Exception e){
            return false;
        }
    }
}

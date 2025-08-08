package com.impulse.api;

import com.impulse.application.EmailService;
import com.impulse.common.flags.FlagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;
    private final FlagService flags;
    public EmailController(EmailService emailService, FlagService flags){this.emailService=emailService;this.flags=flags;}

    private boolean enabled(){return flags.isOn("communication.email");}

    @PostMapping("/send/{template}/{userId}")
    public ResponseEntity<?> send(@PathVariable String template, @PathVariable Long userId, @RequestBody(required=false) Map<String,Object> params){
        if(!enabled()) return ResponseEntity.notFound().build();
        try {
            return ResponseEntity.ok(emailService.send(template, userId, params==null?Map.of():params));
        } catch(IllegalArgumentException iae){
            return ResponseEntity.status(404).body(Map.of("error", iae.getMessage()));
        }
    }
}

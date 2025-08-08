package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.EmailTemplateService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/email-templates")
public class EmailTemplateController {
    private final EmailTemplateService emails;
    public EmailTemplateController(EmailTemplateService emails, FlagService flags){this.emails=emails;}

    private boolean enabled(){return true;}

    @PostMapping("/upsert/{code}")
    public ResponseEntity<?> upsert(@PathVariable String code, @RequestParam String subject, @RequestParam String body){
        if(!enabled()) return ResponseEntity.notFound().build();
        emails.upsert(code, subject, body);
        return ResponseEntity.ok(emails.get(code));
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> get(@PathVariable String code){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(emails.get(code));
    }
}

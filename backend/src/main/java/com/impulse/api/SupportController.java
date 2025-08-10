package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.SupportService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/support")
public class SupportController {
    private final SupportService support;
    public SupportController(SupportService support, FlagService flags){this.support=support;}
    private boolean enabled(){return true;} // podría añadirse flag dedicado

    @PostMapping("/ticket/{userId}")
    public ResponseEntity<?> create(@PathVariable Long userId, @RequestParam String subject, @RequestParam String body){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(support.create(userId, subject, body));
    }

    @GetMapping("/tickets")
    public ResponseEntity<?> list(){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(support.list());
    }

    @PostMapping("/ticket/{id}/close")
    public ResponseEntity<?> close(@PathVariable Long id){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(support.close(id));
    }
}

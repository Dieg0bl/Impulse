package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.IncidentService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final IncidentService incidents;
    public IncidentController(IncidentService incidents, FlagService flags){this.incidents=incidents;}
    private boolean enabled(){return true;}

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam String status, @RequestParam String message){
        if(!enabled()) return ResponseEntity.notFound().build();
        incidents.create(status, message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<?> list(){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(incidents.list());
    }
}

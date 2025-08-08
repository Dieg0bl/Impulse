package com.impulse.api;

import com.impulse.application.TransparencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dsa")
public class TransparencyController {
    private final TransparencyService transparency;
    public TransparencyController(TransparencyService transparency){this.transparency=transparency;}

    @GetMapping("/amar")
    public ResponseEntity<?> amar(){
        return ResponseEntity.ok(transparency.latestAmar());
    }
}

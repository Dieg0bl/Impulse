package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/errors")
public class ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @PostMapping("/critical")
    public ResponseEntity<String> reportCritical(@RequestBody java.util.Map<String, Object> error) {
        // Logging profesional
        logger.error("CRITICAL ERROR: {}", error);
        return ResponseEntity.ok("Error crítico reportado");
    }
}

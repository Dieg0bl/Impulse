package com.impulse.metrics;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/metrics")
public class ProductMetricsController {
    private final ProductMetricsService service;
    public ProductMetricsController(ProductMetricsService service){ this.service=service; }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/daily/{day}")
    public ResponseEntity<?> daily(@PathVariable String day){
        LocalDate d = LocalDate.parse(day);
        return ResponseEntity.ok(service.daily(d));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/daily/range")
    public ResponseEntity<?> range(@RequestParam String from, @RequestParam String to){
        return ResponseEntity.ok(service.range(LocalDate.parse(from), LocalDate.parse(to)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value="/daily/export.csv", produces= MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> exportCsv(@RequestParam String from, @RequestParam String to){
        return ResponseEntity.ok(service.rangeCsv(LocalDate.parse(from), LocalDate.parse(to)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/funnel")
    public ResponseEntity<?> funnel(@RequestParam String from, @RequestParam String to){
        return ResponseEntity.ok(service.funnel(LocalDate.parse(from), LocalDate.parse(to)));
    }
}

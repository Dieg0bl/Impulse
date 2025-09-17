package com.impulse.coaching.controller;

import com.impulse.domain.model.CoachingFeature;
import com.impulse.coaching.service.CoachingFeatureService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coaching/features")
public class CoachingFeatureController {
    private final CoachingFeatureService service;

    public CoachingFeatureController(CoachingFeatureService service) {
        this.service = service;
    }

    @GetMapping
    public List<CoachingFeature> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<CoachingFeature> getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public CoachingFeature create(@RequestBody CoachingFeature feature) {
        return service.save(feature);
    }

    @PutMapping("/{id}")
    public CoachingFeature update(@PathVariable String id, @RequestBody CoachingFeature feature) {
        feature.setId(id);
        return service.save(feature);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteById(id);
    }
}

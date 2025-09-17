package com.impulse.coaching.controller;

import com.impulse.domain.model.CoachingTier;
import com.impulse.coaching.service.CoachingTierService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coaching/tiers")
public class CoachingTierController {
    private final CoachingTierService service;

    public CoachingTierController(CoachingTierService service) {
        this.service = service;
    }

    @GetMapping
    public List<CoachingTier> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<CoachingTier> getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public CoachingTier create(@RequestBody CoachingTier tier) {
        return service.save(tier);
    }

    @PutMapping("/{id}")
    public CoachingTier update(@PathVariable String id, @RequestBody CoachingTier tier) {
        tier.setTier(id);
        return service.save(tier);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteById(id);
    }
}

package com.impulse.coaching.controller;

import com.impulse.domain.coachinginteraction.CoachingInteraction;
import com.impulse.coaching.service.CoachingInteractionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coaching/interactions")
public class CoachingInteractionController {
    private final CoachingInteractionService service;

    public CoachingInteractionController(CoachingInteractionService service) {
        this.service = service;
    }

    @GetMapping
    public List<CoachingInteraction> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<CoachingInteraction> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public CoachingInteraction create(@RequestBody CoachingInteraction interaction) {
        return service.save(interaction);
    }

    @PutMapping("/{id}")
    public CoachingInteraction update(@PathVariable Long id, @RequestBody CoachingInteraction interaction) {
        interaction.setId(id);
        return service.save(interaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}


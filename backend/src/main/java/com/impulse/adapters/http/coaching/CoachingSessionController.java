package com.impulse.coaching.controller;

import com.impulse.domain.coachingsession.CoachingSession;
import com.impulse.coaching.service.CoachingSessionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coaching/sessions")
public class CoachingSessionController {
    private final CoachingSessionService service;

    public CoachingSessionController(CoachingSessionService service) {
        this.service = service;
    }

    @GetMapping
    public List<CoachingSession> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<CoachingSession> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public CoachingSession create(@RequestBody CoachingSession session) {
        return service.save(session);
    }

    @PutMapping("/{id}")
    public CoachingSession update(@PathVariable Long id, @RequestBody CoachingSession session) {
        session.setId(id);
        return service.save(session);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}


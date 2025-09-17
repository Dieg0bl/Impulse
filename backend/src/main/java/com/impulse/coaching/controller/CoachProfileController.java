package com.impulse.coaching.controller;

import com.impulse.domain.model.CoachProfile;
import com.impulse.coaching.service.CoachProfileService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coaching/profiles")
public class CoachProfileController {
    private final CoachProfileService service;

    public CoachProfileController(CoachProfileService service) {
        this.service = service;
    }

    @GetMapping
    public List<CoachProfile> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<CoachProfile> getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public CoachProfile create(@RequestBody CoachProfile profile) {
        return service.save(profile);
    }

    @PutMapping("/{id}")
    public CoachProfile update(@PathVariable String id, @RequestBody CoachProfile profile) {
        profile.setId(id);
        return service.save(profile);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteById(id);
    }
}

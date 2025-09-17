package com.impulse.coaching.controller;

import com.impulse.domain.model.PersonalPlan;
import com.impulse.coaching.service.PersonalPlanService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coaching/personal-plans")
public class PersonalPlanController {
    private final PersonalPlanService service;

    public PersonalPlanController(PersonalPlanService service) {
        this.service = service;
    }

    @GetMapping
    public List<PersonalPlan> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PersonalPlan> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public PersonalPlan create(@RequestBody PersonalPlan plan) {
        return service.save(plan);
    }

    @PutMapping("/{id}")
    public PersonalPlan update(@PathVariable Long id, @RequestBody PersonalPlan plan) {
        plan.setId(id);
        return service.save(plan);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}

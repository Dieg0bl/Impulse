package com.impulse.domain.gamification;

import com.impulse.application.gamification.ThematicEventService;
import com.impulse.domain.gamification.ThematicEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gamification/thematic-events")
public class ThematicEventController {
    @Autowired
    private ThematicEventService service;

    @GetMapping
    public List<ThematicEvent> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ThematicEvent> getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public ThematicEvent create(@RequestBody ThematicEvent event) {
        return service.save(event);
    }

    @PutMapping("/{id}")
    public ThematicEvent update(@PathVariable String id, @RequestBody ThematicEvent event) {
        event.setId(id);
        return service.save(event);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteById(id);
    }
}

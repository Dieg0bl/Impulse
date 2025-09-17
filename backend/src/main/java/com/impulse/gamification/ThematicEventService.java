package com.impulse.gamification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ThematicEventService {
    @Autowired
    private ThematicEventRepository repository;

    public List<ThematicEvent> findAll() {
        return repository.findAll();
    }

    public Optional<ThematicEvent> findById(String id) {
        return repository.findById(id);
    }

    public ThematicEvent save(ThematicEvent event) {
        return repository.save(event);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}

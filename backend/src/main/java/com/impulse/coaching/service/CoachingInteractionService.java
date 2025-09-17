package com.impulse.coaching.service;

import com.impulse.domain.model.CoachingInteraction;
import com.impulse.coaching.repository.CoachingInteractionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoachingInteractionService {
    private final CoachingInteractionRepository repository;

    public CoachingInteractionService(CoachingInteractionRepository repository) {
        this.repository = repository;
    }

    public List<CoachingInteraction> findAll() {
        return repository.findAll();
    }

    public Optional<CoachingInteraction> findById(Long id) {
        return repository.findById(id);
    }

    public CoachingInteraction save(CoachingInteraction interaction) {
        return repository.save(interaction);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

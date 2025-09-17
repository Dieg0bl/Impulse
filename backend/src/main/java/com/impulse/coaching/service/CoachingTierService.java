package com.impulse.coaching.service;

import com.impulse.domain.model.CoachingTier;
import com.impulse.coaching.repository.CoachingTierRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoachingTierService {
    private final CoachingTierRepository repository;

    public CoachingTierService(CoachingTierRepository repository) {
        this.repository = repository;
    }

    public List<CoachingTier> findAll() {
        return repository.findAll();
    }

    public Optional<CoachingTier> findById(String id) {
        return repository.findById(id);
    }

    public CoachingTier save(CoachingTier tier) {
        return repository.save(tier);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}

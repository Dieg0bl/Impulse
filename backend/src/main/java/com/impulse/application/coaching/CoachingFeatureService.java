package com.impulse.application.coaching;

import com.impulse.domain.coaching.CoachingFeature;
import com.impulse.adapters.persistence.coaching.CoachingFeatureRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoachingFeatureService {
    private final CoachingFeatureRepository repository;

    public CoachingFeatureService(CoachingFeatureRepository repository) {
        this.repository = repository;
    }

    public List<CoachingFeature> findAll() {
        return repository.findAll();
    }

    public Optional<CoachingFeature> findById(String id) {
        return repository.findById(id);
    }

    public CoachingFeature save(CoachingFeature feature) {
        return repository.save(feature);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}

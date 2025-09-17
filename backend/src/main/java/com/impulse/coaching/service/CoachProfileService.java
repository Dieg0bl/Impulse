package com.impulse.coaching.service;

import com.impulse.domain.model.CoachProfile;
import com.impulse.coaching.repository.CoachProfileRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoachProfileService {
    private final CoachProfileRepository repository;

    public CoachProfileService(CoachProfileRepository repository) {
        this.repository = repository;
    }

    public List<CoachProfile> findAll() {
        return repository.findAll();
    }

    public Optional<CoachProfile> findById(String id) {
        return repository.findById(id);
    }

    public CoachProfile save(CoachProfile profile) {
        return repository.save(profile);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}

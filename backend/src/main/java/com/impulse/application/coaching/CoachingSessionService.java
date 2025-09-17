package com.impulse.coaching.service;

import com.impulse.domain.coachingsession.CoachingSession;
import com.impulse.coaching.repository.CoachingSessionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoachingSessionService {
    private final CoachingSessionRepository repository;

    public CoachingSessionService(CoachingSessionRepository repository) {
        this.repository = repository;
    }

    public List<CoachingSession> findAll() {
        return repository.findAll();
    }

    public Optional<CoachingSession> findById(Long id) {
        return repository.findById(id);
    }

    public CoachingSession save(CoachingSession session) {
        return repository.save(session);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}


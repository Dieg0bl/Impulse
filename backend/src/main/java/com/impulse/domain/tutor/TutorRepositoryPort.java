package com.impulse.domain.tutor;

import java.util.Optional;
import java.util.List;

public interface TutorRepositoryPort {
    Optional<Tutor> findById(Long id);
    Tutor save(Tutor tutor);
    List<Tutor> findAll();
    void deleteById(Long id);
}

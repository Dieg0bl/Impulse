package com.impulse.domain.tutor;

import java.util.Optional;
import java.util.List;

public interface ValidationRepositoryPort {
    Optional<Validation> findById(Long id);
    Validation save(Validation validation);
    List<Validation> findAll();
    void deleteById(Long id);
}

package com.impulse.domain.monetizacion;

import java.util.Optional;
import java.util.List;

public interface PlanRepositoryPort {
    Optional<Plan> findById(Long id);
    Plan save(Plan plan);
    List<Plan> findAll();
    void deleteById(Long id);
}

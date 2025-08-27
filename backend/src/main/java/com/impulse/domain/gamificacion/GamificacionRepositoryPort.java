package com.impulse.domain.gamificacion;

import java.util.Optional;
import java.util.List;

public interface GamificacionRepositoryPort {
    Optional<Gamificacion> findById(Long id);
    Gamificacion save(Gamificacion g);
    List<Gamificacion> findAll();
    void deleteById(Long id);
}

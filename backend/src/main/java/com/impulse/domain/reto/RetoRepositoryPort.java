package com.impulse.domain.reto;

import java.util.Optional;
import java.util.List;

public interface RetoRepositoryPort {
    Optional<Reto> findById(Long id);
    Reto save(Reto reto);
    List<Reto> findAll();
    void deleteById(Long id);
    // Agrega aquí los métodos necesarios usados por RetoService
}

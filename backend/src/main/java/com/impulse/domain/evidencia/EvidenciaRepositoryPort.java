package com.impulse.domain.evidencia;

import java.util.Optional;
import java.util.List;

public interface EvidenciaRepositoryPort {
    Optional<Evidencia> findById(Long id);
    Evidencia save(Evidencia evidencia);
    List<Evidencia> findAll();
    void deleteById(Long id);
    // Agrega aquí los métodos necesarios usados por EvidenciaService
}

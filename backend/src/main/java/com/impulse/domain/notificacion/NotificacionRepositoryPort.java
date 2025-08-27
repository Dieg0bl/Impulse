package com.impulse.domain.notificacion;

import java.util.Optional;
import java.util.List;

public interface NotificacionRepositoryPort {
    Optional<Notificacion> findById(Long id);
    Notificacion save(Notificacion notificacion);
    List<Notificacion> findAll();
    void deleteById(Long id);
    // Agrega aquí los métodos necesarios usados por NotificacionService
}

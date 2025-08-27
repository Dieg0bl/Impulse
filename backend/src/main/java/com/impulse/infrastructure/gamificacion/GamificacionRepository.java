package com.impulse.infrastructure.gamificacion;

/**
 * Repositorio JPA para Gamificacion. Solo acceso a datos, sin lógica de negocio.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * No se requieren métodos personalizados en esta fase según la lógica de negocio y la guía IMPULSE. Añadir solo si la lógica lo exige y documentar.
 */
import com.impulse.application.ports.GamificacionPort;
import org.springframework.stereotype.Repository;

@Repository
public interface GamificacionRepository extends GamificacionPort {
}

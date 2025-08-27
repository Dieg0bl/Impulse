package com.impulse.infrastructure.notificacion;

/**
 * Repositorio JPA para Notificacion. Solo acceso a datos, sin lógica de negocio.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * No se requieren métodos personalizados en esta fase según la lógica de negocio y la guía IMPULSE. Añadir solo si la lógica lo exige y documentar.
 */
import com.impulse.domain.notificacion.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.notificacion.NotificacionRepositoryPort;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long>, NotificacionRepositoryPort {
}

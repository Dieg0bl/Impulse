package com.impulse.infrastructure.tutor;

/**
 * Repositorio JPA para Tutor. Solo acceso a datos, sin lógica de negocio.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * No se requieren métodos personalizados en esta fase según la lógica de negocio y la guía IMPULSE. Añadir solo si la lógica lo exige y documentar.
 */
import com.impulse.domain.tutor.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
}

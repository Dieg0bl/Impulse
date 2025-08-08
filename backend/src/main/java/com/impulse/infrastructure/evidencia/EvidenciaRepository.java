package com.impulse.infrastructure.evidencia;

import com.impulse.domain.evidencia.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para Evidencia. Solo acceso a datos, sin lógica de negocio.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {
    // Métodos personalizados solo si son necesarios y documentados
}

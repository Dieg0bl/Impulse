package com.impulse.infrastructure.monetizacion;

/**
 * Repositorio JPA para Pago (Monetizacion). Solo acceso a datos, sin lógica de negocio.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * No se requieren métodos personalizados en esta fase según la lógica de negocio y la guía IMPULSE. Añadir solo si la lógica lo exige y documentar.
 */
import com.impulse.domain.monetizacion.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
}

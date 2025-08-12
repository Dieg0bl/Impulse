
package com.impulse.infrastructure.reto;
/**
 * Repositorio JPA para Reto. Solo acceso a datos, sin lógica de negocio.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * No se requieren métodos personalizados en esta fase según la lógica de negocio y la guía IMPULSE. Añadir solo si la lógica lo exige y documentar.
 */

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.impulse.domain.reto.Reto;

@Repository
public interface RetoRepository extends JpaRepository<Reto, Long> {
	/**
	 * Obtiene todos los retos de un usuario específico.
	 * Requiere índice en la columna usuario_id para eficiencia.
	 */
	List<Reto> findByUsuario_Id(Long usuarioId);
}

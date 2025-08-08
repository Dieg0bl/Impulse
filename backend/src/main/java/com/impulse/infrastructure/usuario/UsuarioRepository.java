package com.impulse.infrastructure.usuario;

import com.impulse.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para Usuario. Solo acceso a datos, sin lógica de negocio.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Busca un usuario por email (público, requiere consentimiento).
     * Cumple la guía: solo se añaden métodos personalizados si la lógica de negocio lo exige.
     * @param email Email del usuario
     * @return Usuario o null
     */
    Usuario findByEmail(String email);
}

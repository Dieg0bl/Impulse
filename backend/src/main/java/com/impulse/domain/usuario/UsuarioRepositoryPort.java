package com.impulse.domain.usuario;

import java.util.Optional;
import java.util.List;

/**
 * Puerto de dominio para acceso a usuarios (implementado en infrastructure).
 */
public interface UsuarioRepositoryPort {
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);
    List<Usuario> findAll();
    void deleteById(Long id);
    boolean existsByEmail(String email);
    // Agrega aquí los métodos necesarios usados por UsuarioService
}

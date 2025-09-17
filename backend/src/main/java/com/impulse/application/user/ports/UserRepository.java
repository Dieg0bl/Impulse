package com.impulse.application.user.ports;

import java.util.List;
import java.util.Optional;

import com.impulse.domain.user.Email;
import com.impulse.domain.user.User;
import com.impulse.domain.user.UserId;

/**
 * UserRepository - Puerto de salida para persistencia de usuarios
 */
public interface UserRepository {

    /**
     * Guarda un usuario (crear o actualizar)
     */
    User save(User user);

    /**
     * Busca un usuario por ID
     */
    Optional<User> findById(UserId id);

    /**
     * Busca un usuario por email
     */
    Optional<User> findByEmail(Email email);

    /**
     * Lista todos los usuarios con paginación
     */
    List<User> findAll(int page, int size);

    /**
     * Cuenta el total de usuarios
     */
    long count();

    /**
     * Elimina un usuario por ID
     */
    void deleteById(UserId id);

    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(Email email);

    /**
     * Lista usuarios por rol
     */
    List<User> findByRole(String role, int page, int size);
}

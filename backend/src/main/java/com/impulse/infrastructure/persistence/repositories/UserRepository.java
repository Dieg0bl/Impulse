package com.impulse.infrastructure.persistence.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.domain.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por email
    Optional<User> findByEmail(String email);

    // Buscar usuario por username (usando email como username)
    default Optional<User> findByUsername(String username) {
        return findByEmail(username);
    }

    // Verificar si existe email
    boolean existsByEmail(String email);

    // Verificar si existe username (usando email como username)
    default boolean existsByUsername(String username) {
        return existsByEmail(username);
    }

    // Buscar usuarios por rol
    Page<User> findByRole(String role, Pageable pageable);

    // Buscar usuarios por estado
    Page<User> findByStatus(String status, Pageable pageable);

    // Buscar usuarios activos
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    Page<User> findActiveUsers(Pageable pageable);

    // Buscar usuarios por nombre que contenga texto
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    // Buscar usuarios por email que contenga texto
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<User> findByEmailContainingIgnoreCase(@Param("email") String email, Pageable pageable);

    // Buscar usuarios registrados en un rango de fechas
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Page<User> findByRegistrationDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);

    // Contar usuarios por rol
    long countByRole(String role);

    // Contar usuarios activos
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();

    // Buscar usuarios verificados
    @Query("SELECT u FROM User u WHERE u.emailVerified = true")
    Page<User> findVerifiedUsers(Pageable pageable);
}


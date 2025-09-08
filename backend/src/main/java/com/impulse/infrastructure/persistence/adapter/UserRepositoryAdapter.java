package com.impulse.infrastructure.persistence.adapter;

import com.impulse.domain.user.User;
import com.impulse.domain.user.UserStatus;
import com.impulse.ports.out.UserRepositoryPort;
import com.impulse.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementation of UserRepositoryPort
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        return userJpaRepository.findByStatus(status);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    public List<User> findRecentUsers(LocalDateTime since) {
        return userJpaRepository.findByStatusAndCreatedAtAfter(UserStatus.ACTIVE, since);
    }

    @Override
    public List<User> findInactiveUsers(LocalDateTime before) {
        return userJpaRepository.findInactiveUsers(before);
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        return userJpaRepository.searchUsers(searchTerm);
    }

    @Override
    public long countByStatus(UserStatus status) {
        return userJpaRepository.countByStatus(status);
    }
}

package com.impulse.adapters.persistence.user.repositories;

import com.impulse.adapters.persistence.user.entities.UserJpaEntity;
import com.impulse.adapters.persistence.user.mappers.UserJpaMapper;
import com.impulse.application.user.ports.UserRepository;
import com.impulse.domain.user.User;
import com.impulse.domain.user.UserId;
import com.impulse.domain.user.Email;
import com.impulse.domain.enums.UserRole;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserRepositoryImpl - ImplementaciÃ³n del puerto UserRepository usando JPA
 */
@Component
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataUserRepository springDataRepository;
    private final UserJpaMapper mapper;

    public UserRepositoryImpl(SpringDataUserRepository springDataRepository,
                             UserJpaMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = mapper.toJpaEntity(user);
        UserJpaEntity savedEntity = springDataRepository.save(jpaEntity);
        return mapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        Optional<UserJpaEntity> jpaEntity = springDataRepository.findById(id.asLong());
        return jpaEntity.map(mapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        Optional<UserJpaEntity> jpaEntity = springDataRepository.findByEmail(email.getValue());
        return jpaEntity.map(mapper::toDomainEntity);
    }

    @Override
    public List<User> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return springDataRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return springDataRepository.count();
    }

    @Override
    public void deleteById(UserId id) {
        springDataRepository.deleteById(id.asLong());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataRepository.existsByEmail(email.getValue());
    }

    @Override
    public List<User> findByRole(String role, int page, int size) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        Pageable pageable = PageRequest.of(page, size);
        return springDataRepository.findByRole(userRole, pageable)
                .getContent()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}



package com.impulse.infrastructure.repository;

import com.impulse.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> { }

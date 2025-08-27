package com.impulse.application.ports;

import com.impulse.domain.tutor.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ValidationPort extends JpaRepository<Validation, Long> {}

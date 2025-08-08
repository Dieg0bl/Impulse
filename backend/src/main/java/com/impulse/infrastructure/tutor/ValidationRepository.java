package com.impulse.infrastructure.tutor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.tutor.Validation;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {
}

package com.impulse.infrastructure.tutor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.tutor.Validator;

@Repository
public interface ValidatorRepository extends JpaRepository<Validator, Long> {
}

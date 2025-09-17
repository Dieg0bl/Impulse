package com.impulse.application.validator.port;

import com.impulse.domain.validator.Validator;
import com.impulse.domain.validator.ValidatorId;
import java.util.List;
import java.util.Optional;

public interface ValidatorRepository {
    Validator save(Validator validator);
    Optional<Validator> findById(ValidatorId id);
    List<Validator> findByIsActive(Boolean isActive);
    Optional<Validator> findByEmail(String email);
}

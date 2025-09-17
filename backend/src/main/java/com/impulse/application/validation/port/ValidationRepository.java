package com.impulse.application.validation.port;

import com.impulse.domain.validation.Validation;
import com.impulse.domain.validation.ValidationId;
import com.impulse.domain.enums.ValidationStatus;
import java.util.Optional;

public interface ValidationRepository {
    Validation save(Validation validation);
    Optional<Validation> findById(ValidationId id);
    long countByStatus(ValidationStatus status);
}

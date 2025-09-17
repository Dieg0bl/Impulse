package com.impulse.adapters.persistence.validation.repository;

import com.impulse.adapters.persistence.validation.entity.ValidationJpaEntity;
import com.impulse.domain.enums.ValidationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataValidationRepository extends JpaRepository<ValidationJpaEntity, Long> {
    long countByStatus(ValidationStatus status);
}



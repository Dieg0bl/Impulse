package com.impulse.validation.repository;

import com.impulse.validation.ValidationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidationRequestRepository extends JpaRepository<ValidationRequest, String> {
}

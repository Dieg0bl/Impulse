package com.impulse.validation.service;

import com.impulse.validation.ValidationSLA;
import com.impulse.validation.repository.ValidationSLARepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ValidationSLAService {
    private final ValidationSLARepository validationSLARepository;
    public ValidationSLAService(ValidationSLARepository validationSLARepository) {
        this.validationSLARepository = validationSLARepository;
    }
    public List<ValidationSLA> getAllSLALevels() {
        return validationSLARepository.findAll();
    }
}

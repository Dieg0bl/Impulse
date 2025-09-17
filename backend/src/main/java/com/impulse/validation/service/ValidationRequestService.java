package com.impulse.validation.service;

import com.impulse.validation.ValidationRequest;
import com.impulse.validation.repository.ValidationRequestRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ValidationRequestService {
    private final ValidationRequestRepository validationRequestRepository;
    public ValidationRequestService(ValidationRequestRepository validationRequestRepository) {
        this.validationRequestRepository = validationRequestRepository;
    }
    public List<ValidationRequest> getAllValidationRequests() {
        return validationRequestRepository.findAll();
    }
}

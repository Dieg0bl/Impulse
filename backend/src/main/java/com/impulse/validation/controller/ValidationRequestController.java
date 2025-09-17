package com.impulse.validation.controller;

import com.impulse.validation.ValidationRequest;
import com.impulse.validation.service.ValidationRequestService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validation/requests")
public class ValidationRequestController {
    private final ValidationRequestService validationRequestService;
    public ValidationRequestController(ValidationRequestService validationRequestService) {
        this.validationRequestService = validationRequestService;
    }
    @GetMapping
    public List<ValidationRequest> getAllValidationRequests() {
        return validationRequestService.getAllValidationRequests();
    }
}

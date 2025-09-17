package com.impulse.validation.controller;

import com.impulse.validation.ValidationSLA;
import com.impulse.validation.service.ValidationSLAService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validation/sla")
public class ValidationSLAController {
    private final ValidationSLAService validationSLAService;
    public ValidationSLAController(ValidationSLAService validationSLAService) {
        this.validationSLAService = validationSLAService;
    }
    @GetMapping
    public List<ValidationSLA> getAllSLALevels() {
        return validationSLAService.getAllSLALevels();
    }
}

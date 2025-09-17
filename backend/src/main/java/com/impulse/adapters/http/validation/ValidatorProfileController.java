package com.impulse.validation.controller;

import com.impulse.validation.ValidatorProfile;
import com.impulse.validation.service.ValidatorProfileService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validation/validators")
public class ValidatorProfileController {
    private final ValidatorProfileService validatorProfileService;
    public ValidatorProfileController(ValidatorProfileService validatorProfileService) {
        this.validatorProfileService = validatorProfileService;
    }
    @GetMapping
    public List<ValidatorProfile> getAllValidatorProfiles() {
        return validatorProfileService.getAllValidatorProfiles();
    }
}

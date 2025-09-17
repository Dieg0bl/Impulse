package com.impulse.validation.controller;

import com.impulse.validation.ValidatorPool;
import com.impulse.validation.service.ValidatorPoolService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validation/pools")
public class ValidatorPoolController {
    private final ValidatorPoolService validatorPoolService;
    public ValidatorPoolController(ValidatorPoolService validatorPoolService) {
        this.validatorPoolService = validatorPoolService;
    }
    @GetMapping
    public List<ValidatorPool> getAllValidatorPools() {
        return validatorPoolService.getAllValidatorPools();
    }
}

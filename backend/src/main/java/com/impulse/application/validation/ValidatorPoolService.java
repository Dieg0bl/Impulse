package com.impulse.validation.service;

import com.impulse.validation.ValidatorPool;
import com.impulse.validation.repository.ValidatorPoolRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ValidatorPoolService {
    private final ValidatorPoolRepository validatorPoolRepository;
    public ValidatorPoolService(ValidatorPoolRepository validatorPoolRepository) {
        this.validatorPoolRepository = validatorPoolRepository;
    }
    public List<ValidatorPool> getAllValidatorPools() {
        return validatorPoolRepository.findAll();
    }
}

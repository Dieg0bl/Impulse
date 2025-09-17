package com.impulse.validation.service;

import com.impulse.validation.ValidatorProfile;
import com.impulse.validation.repository.ValidatorProfileRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ValidatorProfileService {
    private final ValidatorProfileRepository validatorProfileRepository;
    public ValidatorProfileService(ValidatorProfileRepository validatorProfileRepository) {
        this.validatorProfileRepository = validatorProfileRepository;
    }
    public List<ValidatorProfile> getAllValidatorProfiles() {
        return validatorProfileRepository.findAll();
    }
}

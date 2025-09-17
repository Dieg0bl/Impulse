package com.impulse.economy.service;

import com.impulse.economy.EconomicGuardrails;
import com.impulse.economy.repository.EconomicGuardrailsRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EconomicGuardrailsService {
    private final EconomicGuardrailsRepository economicGuardrailsRepository;
    public EconomicGuardrailsService(EconomicGuardrailsRepository economicGuardrailsRepository) {
        this.economicGuardrailsRepository = economicGuardrailsRepository;
    }
    public Optional<EconomicGuardrails> getGuardrails() {
        return economicGuardrailsRepository.findAll().stream().findFirst();
    }
}

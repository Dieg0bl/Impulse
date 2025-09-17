package com.impulse.economy.controller;

import com.impulse.economy.EconomicGuardrails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/economy/guardrails")
public class EconomicGuardrailsController {
    // TODO: Inyectar EconomicGuardrailsService

    @GetMapping
    public EconomicGuardrails getGuardrails() {
        // TODO: Implementar lógica para devolver la configuración de guardrails
        return null;
    }
}

package com.impulse.economy.controller;

import com.impulse.economy.EarningRule;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/economy/earning-rules")
public class EarningRuleController {
    // TODO: Inyectar EarningRuleService

    @GetMapping
    public List<EarningRule> getAllEarningRules() {
        // TODO: Implementar lógica para devolver todas las earning rules
        return List.of();
    }
}

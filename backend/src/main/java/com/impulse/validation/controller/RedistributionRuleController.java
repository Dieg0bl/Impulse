package com.impulse.validation.controller;

import com.impulse.validation.RedistributionRule;
import com.impulse.validation.service.RedistributionRuleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validation/redistribution-rules")
public class RedistributionRuleController {
    private final RedistributionRuleService redistributionRuleService;
    public RedistributionRuleController(RedistributionRuleService redistributionRuleService) {
        this.redistributionRuleService = redistributionRuleService;
    }
    @GetMapping
    public List<RedistributionRule> getAllRedistributionRules() {
        return redistributionRuleService.getAllRedistributionRules();
    }
}

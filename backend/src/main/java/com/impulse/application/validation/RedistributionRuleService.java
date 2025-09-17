package com.impulse.validation.service;

import com.impulse.validation.RedistributionRule;
import com.impulse.validation.repository.RedistributionRuleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RedistributionRuleService {
    private final RedistributionRuleRepository redistributionRuleRepository;
    public RedistributionRuleService(RedistributionRuleRepository redistributionRuleRepository) {
        this.redistributionRuleRepository = redistributionRuleRepository;
    }
    public List<RedistributionRule> getAllRedistributionRules() {
        return redistributionRuleRepository.findAll();
    }
}

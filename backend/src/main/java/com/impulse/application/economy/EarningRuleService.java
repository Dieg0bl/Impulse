package com.impulse.economy.service;

import com.impulse.economy.EarningRule;
import com.impulse.economy.repository.EarningRuleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EarningRuleService {
    private final EarningRuleRepository earningRuleRepository;
    public EarningRuleService(EarningRuleRepository earningRuleRepository) {
        this.earningRuleRepository = earningRuleRepository;
    }
    public List<EarningRule> getAllEarningRules() {
        return earningRuleRepository.findAll();
    }
}

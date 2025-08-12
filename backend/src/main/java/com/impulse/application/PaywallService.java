package com.impulse.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.domain.monetizacion.Plan;
import com.impulse.domain.monetizacion.Subscription;
import com.impulse.infrastructure.monetizacion.PlanRepository;
import com.impulse.infrastructure.monetizacion.SubscriptionRepository;

@Service
public class PaywallService {
    private final PlanRepository planRepo;
    private final SubscriptionRepository subRepo;

    public PaywallService(PlanRepository planRepo, SubscriptionRepository subRepo){
        this.planRepo = planRepo;
        this.subRepo = subRepo;
    }

    public List<Map<String,Object>> listActivePlans(){
        return planRepo.findAll().stream()
            .filter(Plan::getActive)
            .map(p -> {
                Map<String,Object> m = new java.util.LinkedHashMap<>();
                m.put("code", p.getCode());
                m.put("name", p.getName());
                m.put("priceCents", p.getPriceCents());
                m.put("currency", p.getCurrency());
                m.put("period", p.getPeriod());
                return m;
            }).collect(Collectors.toList());
    }

    @Transactional
    public Subscription subscribe(Long userId, String planCode){
        Plan plan = planRepo.findByCode(planCode).orElseThrow();
        List<Subscription> existing = subRepo.findByUserId(userId);
        existing.stream().filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus())).forEach(s -> s.setStatus("CANCELED"));
        Subscription sub = new Subscription(userId, plan, "ACTIVE");
        return subRepo.save(sub);
    }

    public boolean hasEntitlement(Long userId, String feature){
        // Currently any active plan grants all entitlements until feature matrix added to Plan entity.
        return subRepo.findByUserId(userId).stream().anyMatch(s -> "ACTIVE".equalsIgnoreCase(s.getStatus()));
    }
}

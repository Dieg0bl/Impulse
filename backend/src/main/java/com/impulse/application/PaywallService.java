package com.impulse.application;

import java.util.List;
import java.util.Map;
// ...existing imports...

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.domain.monetizacion.Plan;
import com.impulse.domain.monetizacion.Subscription;
import com.impulse.application.ports.PlanPort;
import com.impulse.application.ports.SubscriptionPort;

@Service
public class PaywallService {
    private final PlanPort planRepo;
    private final SubscriptionPort subRepo;
    private static final String ACTIVE = "ACTIVE";

    public PaywallService(PlanPort planRepo, SubscriptionPort subRepo){
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
            }).toList();
    }

    @Transactional
    public Subscription subscribe(Long userId, String planCode){
        Plan plan = planRepo.findAll().stream().filter(p -> planCode.equals(p.getCode())).findFirst().orElseThrow();
    List<Subscription> existing = subRepo.findAll().stream().filter(s -> userId.equals(s.getUserId())).toList();
        existing.stream().filter(s -> ACTIVE.equalsIgnoreCase(s.getStatus())).forEach(s -> s.setStatus("CANCELED"));
    Subscription sub = new Subscription(userId, plan, ACTIVE);
        return subRepo.save(sub);
    }

    public boolean hasEntitlement(Long userId){
        // Currently any active plan grants all entitlements until feature matrix added to Plan entity.
        return subRepo.findAll().stream().filter(s -> userId.equals(s.getUserId())).anyMatch(s -> ACTIVE.equalsIgnoreCase(s.getStatus()));
    }

    // Backwards-compatible overload used by controllers/aspects that pass a feature string
    /**
     * @deprecated Keep compatibility with older controllers that passed a feature string.
     *             New code should call {@link #hasEntitlement(Long)} instead.
     */
    @Deprecated(since = "2024-01")
    public boolean hasEntitlement(Long userId, String feature){
        // Keep behavior for older callers while guiding them to the new API.
        return hasEntitlement(userId);
    }
}

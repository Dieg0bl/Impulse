package com.impulse.infrastructure.monetizacion;

import org.springframework.stereotype.Repository;
import com.impulse.application.ports.PlanPort;
import com.impulse.domain.monetizacion.Plan;
import java.util.Optional;

@Repository
public interface PlanRepository extends PlanPort {
    Optional<Plan> findByCode(String code);
}

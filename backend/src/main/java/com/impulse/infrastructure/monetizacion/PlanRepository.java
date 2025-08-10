package com.impulse.infrastructure.monetizacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.monetizacion.Plan;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByCode(String code);
}

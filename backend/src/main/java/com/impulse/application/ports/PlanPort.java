package com.impulse.application.ports;

import com.impulse.domain.monetizacion.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PlanPort extends JpaRepository<Plan, Long> {
	java.util.Optional<Plan> findByCode(String code);
}

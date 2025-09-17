package com.impulse.adapters.persistence.coaching;

import com.impulse.domain.personalplan.PersonalPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalPlanRepository extends JpaRepository<PersonalPlan, Long> {
}



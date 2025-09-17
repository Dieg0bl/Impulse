package com.impulse.coaching.repository;

import com.impulse.domain.model.PersonalPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalPlanRepository extends JpaRepository<PersonalPlan, Long> {
}

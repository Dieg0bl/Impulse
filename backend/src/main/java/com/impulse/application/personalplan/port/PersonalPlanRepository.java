package com.impulse.application.personalplan.port;

import com.impulse.domain.personalplan.PersonalPlan;
import com.impulse.domain.personalplan.PersonalPlanId;
import java.util.Optional;

public interface PersonalPlanRepository {
    PersonalPlan save(PersonalPlan personalPlan);
    Optional<PersonalPlan> findById(PersonalPlanId id);
    long countByUserId(String userId);
}

package com.impulse.adapters.persistence.personalplan.repository;

import com.impulse.adapters.persistence.personalplan.entity.PersonalPlanJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataPersonalPlanRepository extends JpaRepository<PersonalPlanJpaEntity, Long> {
    long countByUserId(String userId);
}



package com.impulse.adapters.persistence.personalplan.repository;

import com.impulse.application.personalplan.port.PersonalPlanRepository;
import com.impulse.domain.personalplan.PersonalPlan;
import com.impulse.domain.personalplan.PersonalPlanId;
import com.impulse.adapters.persistence.personalplan.entity.PersonalPlanJpaEntity;
import com.impulse.adapters.persistence.personalplan.mapper.PersonalPlanJpaMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class PersonalPlanRepositoryImpl implements PersonalPlanRepository {
    private final SpringDataPersonalPlanRepository springDataPersonalPlanRepository;
    private final PersonalPlanJpaMapper personalPlanJpaMapper;

    public PersonalPlanRepositoryImpl(SpringDataPersonalPlanRepository springDataPersonalPlanRepository, PersonalPlanJpaMapper personalPlanJpaMapper) {
        this.springDataPersonalPlanRepository = springDataPersonalPlanRepository;
        this.personalPlanJpaMapper = personalPlanJpaMapper;
    }

    @Override
    public PersonalPlan save(PersonalPlan personalPlan) {
        PersonalPlanJpaEntity entity = personalPlanJpaMapper.toEntity(personalPlan);
        PersonalPlanJpaEntity saved = springDataPersonalPlanRepository.save(entity);
        return personalPlanJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<PersonalPlan> findById(PersonalPlanId id) {
        return springDataPersonalPlanRepository.findById(id.getValue())
                .map(personalPlanJpaMapper::toDomain);
    }

    @Override
    public long countByUserId(String userId) {
        return springDataPersonalPlanRepository.countByUserId(userId);
    }
}



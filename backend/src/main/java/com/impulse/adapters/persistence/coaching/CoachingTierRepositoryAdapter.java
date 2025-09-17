package com.impulse.adapters.persistence.coaching;

import com.impulse.application.coaching.port.out.CoachingTierRepository;
import com.impulse.domain.coaching.CoachingTier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CoachingTierRepositoryAdapter implements CoachingTierRepository {

    private final CoachingTierJpaRepository jpaRepository;

    public CoachingTierRepositoryAdapter(CoachingTierJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<CoachingTier> findByTier(String tier) {
        return jpaRepository.findById(tier)
                .map(this::toDomain);
    }

    @Override
    public List<CoachingTier> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachingTier> findByPriceRange(double minPrice, double maxPrice) {
        return jpaRepository.findByMonthlyPriceBetween(minPrice, maxPrice).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public CoachingTier save(CoachingTier coachingTier) {
        CoachingTierEntity entity = toEntity(coachingTier);
        CoachingTierEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public void deleteByTier(String tier) {
        jpaRepository.deleteById(tier);
    }

    private CoachingTier toDomain(CoachingTierEntity entity) {
        return CoachingTier.builder()
                .tier(entity.getTier())
                .name(entity.getName())
                .description(entity.getDescription())
                .monthlyPrice(entity.getMonthlyPrice())
                .responseTimeHours(entity.getResponseTimeHours())
                .monthlyInteractions(entity.getMonthlyInteractions())
                .includesVideoCalls(entity.isIncludesVideoCalls())
                .personalizedPlan(entity.isPersonalizedPlan())
                .prioritySupport(entity.isPrioritySupport())
                .build();
    }

    private CoachingTierEntity toEntity(CoachingTier domain) {
        return new CoachingTierEntity(
                domain.getTier(),
                domain.getName(),
                domain.getDescription(),
                domain.getMonthlyPrice(),
                domain.getResponseTimeHours(),
                domain.getMonthlyInteractions(),
                domain.includesVideoCalls(),
                domain.hasPersonalizedPlan(),
                domain.hasPrioritySupport()
        );
    }
}

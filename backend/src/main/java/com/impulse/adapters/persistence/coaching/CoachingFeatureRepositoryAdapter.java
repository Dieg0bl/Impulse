package com.impulse.adapters.persistence.coaching;

import com.impulse.application.coaching.port.out.CoachingFeatureRepository;
import com.impulse.domain.coaching.CoachingFeature;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CoachingFeatureRepositoryAdapter implements CoachingFeatureRepository {

    private final CoachingFeatureJpaRepository jpaRepository;

    public CoachingFeatureRepositoryAdapter(CoachingFeatureJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<CoachingFeature> findById(String id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<CoachingFeature> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachingFeature> findByTier(String tier) {
        return jpaRepository.findByTier(tier).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachingFeature> findByType(String type) {
        return jpaRepository.findByType(type).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public CoachingFeature save(CoachingFeature coachingFeature) {
        CoachingFeatureEntity entity = toEntity(coachingFeature);
        CoachingFeatureEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    private CoachingFeature toDomain(CoachingFeatureEntity entity) {
        return new CoachingFeature(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getType(),
                entity.isUnlimitedUsage(),
                entity.getMonthlyQuota(),
                entity.getTier()
        );
    }

    private CoachingFeatureEntity toEntity(CoachingFeature domain) {
        return new CoachingFeatureEntity(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getType(),
                domain.isUnlimitedUsage(),
                domain.getMonthlyQuota(),
                domain.getTier()
        );
    }
}

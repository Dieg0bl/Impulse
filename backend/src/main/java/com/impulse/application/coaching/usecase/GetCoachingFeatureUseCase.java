package com.impulse.application.coaching.usecase;

import com.impulse.application.coaching.port.in.GetCoachingFeatureQuery;
import com.impulse.application.coaching.port.out.CoachingFeatureRepository;
import com.impulse.domain.coaching.CoachingFeature;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetCoachingFeatureUseCase implements GetCoachingFeatureQuery {

    private final CoachingFeatureRepository coachingFeatureRepository;

    public GetCoachingFeatureUseCase(CoachingFeatureRepository coachingFeatureRepository) {
        this.coachingFeatureRepository = coachingFeatureRepository;
    }

    @Override
    public Optional<CoachingFeature> findById(String id) {
        return coachingFeatureRepository.findById(id);
    }

    @Override
    public List<CoachingFeature> findAll() {
        return coachingFeatureRepository.findAll();
    }

    @Override
    public List<CoachingFeature> findByTier(String tier) {
        return coachingFeatureRepository.findByTier(tier);
    }

    @Override
    public List<CoachingFeature> findByType(String type) {
        return coachingFeatureRepository.findByType(type);
    }
}

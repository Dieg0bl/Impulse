package com.impulse.application.coaching.usecase;

import com.impulse.application.coaching.port.in.GetCoachingTierQuery;
import com.impulse.application.coaching.port.out.CoachingTierRepository;
import com.impulse.domain.coaching.CoachingTier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetCoachingTierUseCase implements GetCoachingTierQuery {

    private final CoachingTierRepository coachingTierRepository;

    public GetCoachingTierUseCase(CoachingTierRepository coachingTierRepository) {
        this.coachingTierRepository = coachingTierRepository;
    }

    @Override
    public Optional<CoachingTier> findByTier(String tier) {
        return coachingTierRepository.findByTier(tier);
    }

    @Override
    public List<CoachingTier> findAll() {
        return coachingTierRepository.findAll();
    }

    @Override
    public List<CoachingTier> findByPriceRange(double minPrice, double maxPrice) {
        return coachingTierRepository.findByPriceRange(minPrice, maxPrice);
    }
}

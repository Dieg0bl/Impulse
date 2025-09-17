package com.impulse.application.rating.usecase;

import com.impulse.application.rating.dto.RatingResponse;
import com.impulse.application.rating.mapper.RatingAppMapper;
import com.impulse.application.rating.port.RatingRepository;
import com.impulse.domain.rating.Rating;
import com.impulse.domain.rating.RatingId;
import com.impulse.domain.rating.RatingDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetRatingByIdUseCase {
    private final RatingRepository ratingRepository;
    private final RatingAppMapper ratingMapper;
    public GetRatingByIdUseCase(RatingRepository ratingRepository, RatingAppMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.ratingMapper = ratingMapper;
    }
    public RatingResponse execute(Long ratingId) {
        if (ratingId == null) throw new RatingDomainError("Rating ID cannot be null");
        RatingId id = RatingId.of(ratingId);
        Rating rating = ratingRepository.findById(id)
            .orElseThrow(() -> new RatingDomainError("Rating not found with ID: " + ratingId));
        return ratingMapper.toResponse(rating);
    }
}

package com.impulse.application.rating.usecase;

import com.impulse.application.rating.dto.CreateRatingCommand;
import com.impulse.application.rating.dto.RatingResponse;
import com.impulse.application.rating.mapper.RatingAppMapper;
import com.impulse.application.rating.port.RatingRepository;
import com.impulse.domain.rating.Rating;
import com.impulse.domain.rating.RatingDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateRatingUseCase {
    private final RatingRepository ratingRepository;
    private final RatingAppMapper ratingMapper;
    public CreateRatingUseCase(RatingRepository ratingRepository, RatingAppMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.ratingMapper = ratingMapper;
    }
    public RatingResponse execute(CreateRatingCommand command) {
        validateCommand(command);
        Rating rating = ratingMapper.toDomain(command);
        Rating saved = ratingRepository.save(rating);
        return ratingMapper.toResponse(saved);
    }
    private void validateCommand(CreateRatingCommand command) {
        if (command == null) throw new RatingDomainError("CreateRatingCommand cannot be null");
        if (command.getScore() == null || command.getScore() < 1 || command.getScore() > 5) throw new RatingDomainError("Score must be between 1 and 5");
        if (command.getType() == null) throw new RatingDomainError("Rating type is required");
        if (command.getRaterUserId() == null || command.getRaterUserId().trim().isEmpty()) throw new RatingDomainError("RaterUserId is required");
    }
}

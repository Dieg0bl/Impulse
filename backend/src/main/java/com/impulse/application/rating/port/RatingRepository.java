package com.impulse.application.rating.port;

import com.impulse.domain.rating.Rating;
import com.impulse.domain.rating.RatingId;
import com.impulse.domain.enums.RatingType;
import java.util.Optional;

public interface RatingRepository {
    Rating save(Rating rating);
    Optional<Rating> findById(RatingId id);
    long countByType(RatingType type);
}

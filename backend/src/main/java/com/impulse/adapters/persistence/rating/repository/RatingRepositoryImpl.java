package com.impulse.adapters.persistence.rating.repository;

import com.impulse.application.rating.port.RatingRepository;
import com.impulse.domain.rating.Rating;
import com.impulse.domain.rating.RatingId;
import com.impulse.domain.enums.RatingType;
import com.impulse.adapters.persistence.rating.entity.RatingJpaEntity;
import com.impulse.adapters.persistence.rating.mapper.RatingJpaMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class RatingRepositoryImpl implements RatingRepository {
    private final SpringDataRatingRepository springDataRatingRepository;
    private final RatingJpaMapper ratingJpaMapper;

    public RatingRepositoryImpl(SpringDataRatingRepository springDataRatingRepository, RatingJpaMapper ratingJpaMapper) {
        this.springDataRatingRepository = springDataRatingRepository;
        this.ratingJpaMapper = ratingJpaMapper;
    }

    @Override
    public Rating save(Rating rating) {
        RatingJpaEntity entity = ratingJpaMapper.toEntity(rating);
        RatingJpaEntity saved = springDataRatingRepository.save(entity);
        return ratingJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Rating> findById(RatingId id) {
        return springDataRatingRepository.findById(id.getValue())
                .map(ratingJpaMapper::toDomain);
    }

    @Override
    public long countByType(RatingType type) {
        return springDataRatingRepository.countByType(type);
    }
}



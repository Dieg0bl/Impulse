package com.impulse.adapters.persistence.rating.repository;

import com.impulse.adapters.persistence.rating.entity.RatingJpaEntity;
import com.impulse.domain.enums.RatingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataRatingRepository extends JpaRepository<RatingJpaEntity, Long> {
    long countByType(RatingType type);
}



package com.impulse.adapters.persistence.coaching;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingTierJpaRepository extends JpaRepository<CoachingTierEntity, String> {

    @Query("SELECT ct FROM CoachingTierEntity ct WHERE ct.monthlyPrice BETWEEN :minPrice AND :maxPrice")
    List<CoachingTierEntity> findByMonthlyPriceBetween(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
}

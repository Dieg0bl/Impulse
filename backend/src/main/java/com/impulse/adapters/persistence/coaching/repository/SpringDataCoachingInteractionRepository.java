package com.impulse.adapters.persistence.coaching.repository;

import com.impulse.adapters.persistence.coaching.entity.CoachingInteractionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCoachingInteractionRepository extends JpaRepository<CoachingInteractionJpaEntity, UUID> {

    List<CoachingInteractionJpaEntity> findBySessionId(UUID sessionId);

    List<CoachingInteractionJpaEntity> findByUserIdOrderByTimestampDesc(UUID userId);

    List<CoachingInteractionJpaEntity> findByCoachIdOrderByTimestampDesc(UUID coachId);

    @Query("SELECT c FROM CoachingInteractionJpaEntity c WHERE c.type = :type ORDER BY c.timestamp DESC")
    List<CoachingInteractionJpaEntity> findByTypeOrderByTimestampDesc(@Param("type") String type);

    @Query("SELECT c FROM CoachingInteractionJpaEntity c WHERE c.sessionId = :sessionId AND c.type = :type")
    List<CoachingInteractionJpaEntity> findBySessionIdAndType(@Param("sessionId") UUID sessionId, @Param("type") String type);
}

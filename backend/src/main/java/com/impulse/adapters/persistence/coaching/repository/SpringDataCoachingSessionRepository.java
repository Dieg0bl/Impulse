package com.impulse.adapters.persistence.coaching.repository;

import com.impulse.adapters.persistence.coaching.entity.CoachingSessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCoachingSessionRepository extends JpaRepository<CoachingSessionJpaEntity, UUID> {

    List<CoachingSessionJpaEntity> findByUserIdOrderByScheduledTimeDesc(UUID userId);

    List<CoachingSessionJpaEntity> findByCoachIdOrderByScheduledTimeDesc(UUID coachId);

    @Query("SELECT c FROM CoachingSessionJpaEntity c WHERE c.status = :status ORDER BY c.scheduledTime")
    List<CoachingSessionJpaEntity> findByStatusOrderByScheduledTime(@Param("status") String status);

    @Query("SELECT c FROM CoachingSessionJpaEntity c WHERE c.scheduledTime BETWEEN :startDate AND :endDate")
    List<CoachingSessionJpaEntity> findByScheduledTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c FROM CoachingSessionJpaEntity c WHERE c.coachId = :coachId AND c.scheduledTime >= :date")
    List<CoachingSessionJpaEntity> findUpcomingSessionsByCoach(@Param("coachId") UUID coachId, @Param("date") LocalDateTime date);
}

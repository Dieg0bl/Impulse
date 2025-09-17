package com.impulse.adapters.persistence.challengeparticipation.repository;

import com.impulse.adapters.persistence.challengeparticipation.entity.ChallengeParticipationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataChallengeParticipationRepository extends JpaRepository<ChallengeParticipationJpaEntity, UUID> {

    List<ChallengeParticipationJpaEntity> findByUserId(String userId);

    List<ChallengeParticipationJpaEntity> findByChallengeId(Long challengeId);

    @Query("SELECT cp FROM ChallengeParticipationJpaEntity cp WHERE cp.userId = :userId AND cp.challengeId = :challengeId")
    List<ChallengeParticipationJpaEntity> findByUserIdAndChallengeId(@Param("userId") String userId, @Param("challengeId") Long challengeId);
}

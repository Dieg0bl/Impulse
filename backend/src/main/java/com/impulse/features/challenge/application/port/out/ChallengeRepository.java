package com.impulse.features.challenge.application.port.out;

import com.impulse.features.challenge.domain.Challenge;
import com.impulse.features.challenge.domain.ChallengeId;
import com.impulse.shared.enums.ChallengeStatus;
import com.impulse.shared.enums.Visibility;
import java.util.List;
import java.util.Optional;

/**
 * Output Port: ChallengeRepository
 * Defines contract for challenge persistence
 */
public interface ChallengeRepository {

    Challenge save(Challenge challenge);

    Optional<Challenge> findById(ChallengeId id);

    List<Challenge> findByOwnerUserId(Long ownerUserId);

    List<Challenge> findByStatus(ChallengeStatus status);

    List<Challenge> findByVisibility(Visibility visibility);

    List<Challenge> findPublicChallenges();

    List<Challenge> findByCategory(String category);

    void delete(ChallengeId id);

    boolean existsById(ChallengeId id);

    long countByOwnerUserId(Long ownerUserId);

    long countByStatus(ChallengeStatus status);
}

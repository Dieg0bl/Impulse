package com.impulse.application.coach.port;

import com.impulse.domain.coach.Coach;
import com.impulse.domain.coach.CoachId;
import com.impulse.domain.user.UserId;
import com.impulse.domain.enums.CoachStatus;
import java.util.List;
import java.util.Optional;

/**
 * CoachRepository - Puerto del repositorio para Coach
 */
public interface CoachRepository {
    Coach save(Coach coach);
    Optional<Coach> findById(CoachId coachId);
    Optional<Coach> findByUserId(UserId userId);
    List<Coach> findByStatus(CoachStatus status);
    List<Coach> findAll();
    void deleteById(CoachId coachId);
    boolean existsById(CoachId coachId);
    long count();
    long countByStatus(CoachStatus status);
}

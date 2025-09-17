package com.impulse.adapters.persistence.coaching;

import com.impulse.domain.coachinginteraction.CoachingInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachingInteractionRepository extends JpaRepository<CoachingInteraction, Long> {
}



package com.impulse.lean.infrastructure.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.impulse.lean.domain.model.Challenge;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.repository.ChallengeParticipationRepository;
import com.impulse.lean.domain.repository.ChallengeRepository;
import com.impulse.lean.domain.repository.UserRepository;

@Component
@Profile("local")
public class DevDataLoader {

    private static final Logger log = LoggerFactory.getLogger(DevDataLoader.class);

    private final ChallengeRepository challengeRepo;
    private final UserRepository userRepo;
    private final ChallengeParticipationRepository participationRepo;

    public DevDataLoader(ChallengeRepository challengeRepo, UserRepository userRepo, ChallengeParticipationRepository participationRepo) {
        this.challengeRepo = challengeRepo;
        this.userRepo = userRepo;
        this.participationRepo = participationRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        try {
            // guard against early startup when DB schema wasn't created cleanly
            if (challengeRepo.count() == 0) {
                User u = new User();
                // username must be alphanumeric and underscores according to validation
                u.setUsername("devuser_local");
                u.setEmail("devuser@local");
                // set a 60-char placeholder for passwordHash to satisfy @Size(min=60)
                u.setPasswordHash("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                u.setFirstName("Dev");
                u.setLastName("User");
                u.setStatus(com.impulse.lean.domain.model.UserStatus.ACTIVE);
                userRepo.save(u);

                // Use the full constructor so slug and derived fields are generated
                Challenge c = new Challenge(
                    u,
                    "Dev Challenge",
                    "Auto-created challenge for local testing",
                    com.impulse.lean.domain.model.ChallengeCategory.FITNESS,
                    com.impulse.lean.domain.model.ChallengeDifficulty.BEGINNER,
                    7
                );
                challengeRepo.save(c);

                ChallengeParticipation p = new ChallengeParticipation();
                p.setChallenge(c);
                p.setUser(u);
                participationRepo.save(p);
            }
        } catch (RuntimeException ex) {
            // don't fail the whole app if schema isn't ready; log and continue
            log.warn("DevDataLoader skipped creating fixtures because DB not ready: {}", ex.getMessage());
        }
    }
}

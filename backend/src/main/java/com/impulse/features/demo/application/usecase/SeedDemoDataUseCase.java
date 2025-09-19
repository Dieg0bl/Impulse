package com.impulse.features.demo.application.usecase;

import com.impulse.features.rbac.application.port.out.UserRepository;
import com.impulse.features.challenge.application.port.out.ChallengeRepository;
import com.impulse.features.evidencereview.application.port.out.EvidenceRepository;
import com.impulse.features.rbac.domain.User;
import com.impulse.features.challenge.domain.Challenge;
import com.impulse.features.evidencereview.domain.Evidence;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Use case: SeedDemoDataUseCase
 * Pobla la base de datos con datos demo/oficiales/plantillas, sin PII ni fotos reales.
 * Solo debe ejecutarse bajo demanda (no en producción salvo explícito).
 */
public class SeedDemoDataUseCase {
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final EvidenceRepository evidenceRepository;

    public SeedDemoDataUseCase(UserRepository userRepository, ChallengeRepository challengeRepository, EvidenceRepository evidenceRepository) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.evidenceRepository = evidenceRepository;
    }

    /**
     * Ejecuta el seed de datos demo/oficiales/plantillas.
     * PROTEGIDO: Solo debe ejecutarse si la variable de entorno ALLOW_DEMO_SEED=true.
     * Cumple requisitos legales: sin PII, sin fotos reales, no indexable, no mezclar con datos reales.
     * Los datos generados llevan los flags isDemo/isOfficial/isBot/isTemplate correctamente.
     */
    public void execute() {
        String allow = System.getenv("ALLOW_DEMO_SEED");
        if (!"true".equalsIgnoreCase(allow)) {
            throw new IllegalStateException("Seed demo solo permitido si ALLOW_DEMO_SEED=true (protegido por seguridad legal y operativa)");
        }

        // 1. Crear usuario oficial demo
        String officialEmail = "equipo@impulse.app";
        Optional<User> existing = userRepository.findByEmail(officialEmail);
        User official;
        if (existing.isPresent()) {
            official = existing.get();
        } else {
            official = new User(
                null,
                "equipo",
                officialEmail,
                "Equipo Impulse",
                null,
                "ADMIN",
                true,
                LocalDateTime.now(),
                null,
                true,  // isDemo
                true,  // isOfficial
                false  // isBot
            );
            official = userRepository.save(official);
        }

        // 2. Crear retos demo/plantilla
        Challenge demoChallenge = new Challenge(
            null,
            official.getId(),
            "Reto demo: Hábitos mañaneros",
            "Despierta a las 7:00, vaso de agua, 10 min de lectura.",
            "alumno",
            LocalDateTime.now(),
            true, // isDemo
            true  // isTemplate
        );
        demoChallenge = challengeRepository.save(demoChallenge);

        // 3. Crear evidencia demo
        Evidence demoEvidence = new Evidence(
            null,
            demoChallenge.getId().value(),
            official.getId(),
            null, // EvidenceType
            "Evidencia de ejemplo",
            null,
            LocalDateTime.now(),
            true // isDemo
        );
        evidenceRepository.save(demoEvidence);

        // 4. (Opcional) Crear validaciones demo (si existe entidad/puerto)
        // ...
    }
}

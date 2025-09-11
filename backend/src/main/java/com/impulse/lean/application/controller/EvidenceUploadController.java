package com.impulse.lean.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.domain.model.Challenge;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.Evidence;
import com.impulse.lean.domain.model.IdempotencyToken;
import com.impulse.lean.domain.repository.ChallengeParticipationRepository;
import com.impulse.lean.domain.repository.ChallengeRepository;
import com.impulse.lean.domain.repository.EvidenceRepository;
import com.impulse.lean.domain.repository.IdempotencyTokenRepository;
import com.impulse.lean.infrastructure.storage.StorageService;
import com.impulse.lean.infrastructure.storage.StorageUploadPresignResponse;



@RestController
@RequestMapping("/api/v1/challenges")
public class EvidenceUploadController {

    private static final Logger logger = LoggerFactory.getLogger(EvidenceUploadController.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final EvidenceRepository evidenceRepository;
    private final IdempotencyTokenRepository idempotencyRepository;
    private final StorageService storageService;

    public EvidenceUploadController(ChallengeRepository challengeRepository,
                                    ChallengeParticipationRepository participationRepository,
                                    EvidenceRepository evidenceRepository,
                                    StorageService storageService,
                                    IdempotencyTokenRepository idempotencyRepository) {
        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
        this.evidenceRepository = evidenceRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.storageService = storageService;
    }

    @PostMapping("/{challengeId}/evidences/presign")
    public ResponseEntity<StorageUploadPresignResponse> presign(@PathVariable Long challengeId,
                                                         @RequestParam String filename,
                                                         @RequestParam(required = false) String contentType) {
        Challenge challenge = challengeRepository.findById(challengeId).orElse(null);
        if (challenge == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    String key = storageService.generateKey("challenges/" + challengeId, filename);
    StorageUploadPresignResponse res = storageService.presign(key);
    return ResponseEntity.ok(new StorageUploadPresignResponse(res.getUploadUrl(), res.getObjectKey()));
    }

    public static class EvidenceNotify {
        private String objectKey;
        private String filename;
        private Long participationId;
        private Integer dayNumber;
        private String mimeType;
        private Long fileSize;

        public String getObjectKey() { return objectKey; }
        public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        public Long getParticipationId() { return participationId; }
        public void setParticipationId(Long participationId) { this.participationId = participationId; }
        public Integer getDayNumber() { return dayNumber; }
        public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    }

    @PostMapping("/{challengeId}/evidences")
    public ResponseEntity<?> notifyEvidence(@PathVariable Long challengeId,
                                            @RequestBody EvidenceNotify body,
                                            @AuthenticationPrincipal UserDetails userDetails,
                                            @RequestHeader(value = "Idempotency-Key", required = false) String idempotency) {

        // Basic validation
        if (body == null || body.objectKey == null) return ResponseEntity.badRequest().build();

        // Idempotency: if provided and already seen, return 409
        if (idempotency != null && !idempotency.isBlank()) {
            var opt = idempotencyRepository.findByToken(idempotency);
            if (opt.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate");
            }
        }

        // Find participation
        ChallengeParticipation participation = participationRepository.findById(body.participationId).orElse(null);
        if (participation == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participation not found");

        // Verify object exists for S3-like providers
        boolean exists = storageService.headObjectExists(body.objectKey);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("uploaded object not found yet");
        }

        // Create Evidence entity
        Evidence ev = new Evidence();
        ev.setParticipation(participation);
        ev.setFilePath(body.objectKey);
        ev.setMimeType(body.mimeType);
        ev.setFileSize(body.fileSize);
        int dayNumber = body.getDayNumber() != null ? body.getDayNumber() : 0;
        ev.setDayNumber(dayNumber);
        ev.setContent(body.getFilename());
        ev.setSubmittedAt(java.time.LocalDateTime.now());

        evidenceRepository.save(ev);

        // persist idempotency token if provided
        if (idempotency != null && !idempotency.isBlank()) {
            saveIdempotencyToken(idempotency);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ev);
    }

    private void saveIdempotencyToken(String idempotency) {
        try {
            // Create and save idempotency token
            IdempotencyToken token = new IdempotencyToken();
            token.setToken(idempotency);
            token.setCreatedAt(java.time.LocalDateTime.now());
            idempotencyRepository.save(token);
            
            logger.debug("Saved idempotency token: {}", idempotency);
        } catch (Exception e) {
            logger.warn("Failed to save idempotency token: {}", e.getMessage());
            // Non-critical failure, don't throw
        }
    }
}

package com.impulse.lean.application.controller;

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
import com.impulse.lean.domain.repository.ChallengeParticipationRepository;
import com.impulse.lean.domain.repository.ChallengeRepository;
import com.impulse.lean.domain.repository.EvidenceRepository;
import com.impulse.lean.infrastructure.storage.StorageService;
import com.impulse.lean.infrastructure.storage.StorageUploadPresignResponse;



@RestController
@RequestMapping("/api/v1/challenges")
public class EvidenceUploadController {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final EvidenceRepository evidenceRepository;
    private final StorageService storageService;

    public EvidenceUploadController(ChallengeRepository challengeRepository,
                                    ChallengeParticipationRepository participationRepository,
                                    EvidenceRepository evidenceRepository,
                                    StorageService storageService) {
        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
        this.evidenceRepository = evidenceRepository;
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

        // Find participation
        ChallengeParticipation participation = participationRepository.findById(body.participationId).orElse(null);
        if (participation == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participation not found");

        // Create Evidence entity
        Evidence ev = new Evidence();
        ev.setParticipation(participation);
        ev.setFilePath(body.objectKey);
        ev.setMimeType(body.mimeType);
        ev.setFileSize(body.fileSize);
    ev.setDayNumber(body.getDayNumber() == null ? 0 : body.getDayNumber());
    ev.setContent(body.getFilename());
    ev.setSubmittedAt(java.time.LocalDateTime.now());

        evidenceRepository.save(ev);

        return ResponseEntity.status(HttpStatus.CREATED).body(ev);
    }
}

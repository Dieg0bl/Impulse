package com.impulse.evidence.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.application.dto.common.ApiResponse;
import com.impulse.lean.application.dto.common.PaginationResponse;
import com.impulse.lean.application.dto.evidence.EvidenceCreateRequestDto;
import com.impulse.lean.application.dto.evidence.EvidenceResponseDto;
import com.impulse.lean.application.dto.evidence.EvidenceUpdateRequestDto;
import com.impulse.lean.application.dto.evidence.EvidenceValidationRequestDto;
import com.impulse.lean.application.service.interfaces.EvidenceService;
import com.impulse.lean.application.service.interfaces.UserService;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.Evidence;
import com.impulse.lean.domain.model.EvidenceStatus;
import com.impulse.lean.domain.model.EvidenceType;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.repository.ChallengeParticipationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * IMPULSE LEAN v1 - Evidence REST Controller
 * 
 * Complete CRUD operations for evidence management
 * Supports file upload, validation workflow, and evidence analytics
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/evidence")
@Tag(name = "Evidence", description = "Evidence management operations")
public class EvidenceController {

    private final EvidenceService evidenceService;
    private final UserService userService;
    private final ChallengeParticipationRepository participationRepository;

    @Autowired
    public EvidenceController(EvidenceService evidenceService,
                             UserService userService,
                             ChallengeParticipationRepository participationRepository) {
        this.evidenceService = evidenceService;
        this.userService = userService;
        this.participationRepository = participationRepository;
    }

    // ========== CRUD OPERATIONS ==========

    @PostMapping
    @Operation(summary = "Create evidence", description = "Submit new evidence for a challenge participation")
    @PreAuthorize("hasRole('USER')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Evidence created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid evidence data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Participation not found")
    })
    public ResponseEntity<ApiResponse<EvidenceResponseDto>> createEvidence(
            @RequestBody @Valid EvidenceCreateRequestDto request,
            @RequestParam Long participationId,
            Authentication authentication) {
        
        try {
            // Get authenticated user
            User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Find participation
            ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participation not found"));
            
            // Verify user owns this participation
            if (!participation.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You can only submit evidence for your own participations"));
            }
            
            Evidence evidence = evidenceService.createEvidence(participation, request);
            EvidenceResponseDto responseDto = EvidenceResponseDto.fromEntity(evidence);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Evidence created successfully", responseDto));
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create evidence: " + e.getMessage()));
        }
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get evidence by UUID", description = "Retrieve a specific evidence by its UUID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidence not found")
    })
    public ResponseEntity<ApiResponse<EvidenceResponseDto>> getEvidenceByUuid(
            @Parameter(description = "Evidence UUID") @PathVariable String uuid) {

        Optional<Evidence> evidenceOpt = evidenceService.findByUuid(uuid);
        
        if (evidenceOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Evidence not found"));
        }
        
        Evidence evidence = evidenceOpt.get();
        EvidenceResponseDto responseDto = EvidenceResponseDto.fromEntity(evidence);
        
        return ResponseEntity.ok(ApiResponse.success("Evidence retrieved successfully", responseDto));
    }

    @PatchMapping("/{uuid}")
    @Operation(summary = "Update evidence", description = "Update evidence content and metadata")
    @PreAuthorize("hasRole('USER')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid update data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Not authorized to update this evidence"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidence not found")
    })
    public ResponseEntity<ApiResponse<EvidenceResponseDto>> updateEvidence(
            @Parameter(description = "Evidence UUID") @PathVariable String uuid,
            @RequestBody @Valid EvidenceUpdateRequestDto request,
            Authentication authentication) {
        
        try {
            // Get authenticated user
            User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Check if user owns this evidence
            Evidence existingEvidence = evidenceService.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found"));
            
            if (!existingEvidence.getParticipation().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You can only update your own evidence"));
            }
            
            Evidence evidence = evidenceService.updateEvidence(uuid, request);
            EvidenceResponseDto responseDto = EvidenceResponseDto.fromEntity(evidence);
            
            return ResponseEntity.ok(ApiResponse.success("Evidence updated successfully", responseDto));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Evidence not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update evidence: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete evidence", description = "Soft delete evidence (mark as rejected)")
    @PreAuthorize("hasRole('USER')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Not authorized to delete this evidence"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidence not found")
    })
    public ResponseEntity<ApiResponse<String>> deleteEvidence(
            @Parameter(description = "Evidence UUID") @PathVariable String uuid,
            Authentication authentication) {
        
        try {
            // Get authenticated user
            User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Check if user owns this evidence
            Evidence existingEvidence = evidenceService.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found"));
            
            if (!existingEvidence.getParticipation().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You can only delete your own evidence"));
            }
            
            evidenceService.deleteEvidence(uuid);
            
            return ResponseEntity.ok(ApiResponse.success("Evidence deleted successfully", "Evidence has been marked as deleted"));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Evidence not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete evidence: " + e.getMessage()));
        }
    }

    // ========== VALIDATION OPERATIONS ==========

    @PostMapping("/{uuid}/approve")
    @Operation(summary = "Approve evidence", description = "Approve evidence with score and comments")
    @PreAuthorize("hasRole('VALIDATOR') or hasRole('MODERATOR')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence approved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot approve this evidence"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidence not found")
    })
    public ResponseEntity<ApiResponse<EvidenceResponseDto>> approveEvidence(
            @Parameter(description = "Evidence UUID") @PathVariable String uuid,
            @RequestParam BigDecimal score,
            @RequestParam(required = false) String comments,
            Authentication authentication) {
        
        try {
            User validator = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
            
            Evidence evidence = evidenceService.approveEvidence(uuid, validator, score, comments);
            EvidenceResponseDto responseDto = EvidenceResponseDto.fromEntity(evidence);
            
            return ResponseEntity.ok(ApiResponse.success("Evidence approved successfully", responseDto));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Evidence not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to approve evidence: " + e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/reject")
    @Operation(summary = "Reject evidence", description = "Reject evidence with reason")
    @PreAuthorize("hasRole('VALIDATOR') or hasRole('MODERATOR')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence rejected successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot reject this evidence"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidence not found")
    })
    public ResponseEntity<ApiResponse<EvidenceResponseDto>> rejectEvidence(
            @Parameter(description = "Evidence UUID") @PathVariable String uuid,
            @RequestParam String reason,
            Authentication authentication) {
        
        try {
            User validator = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
            
            Evidence evidence = evidenceService.rejectEvidence(uuid, validator, reason);
            EvidenceResponseDto responseDto = EvidenceResponseDto.fromEntity(evidence);
            
            return ResponseEntity.ok(ApiResponse.success("Evidence rejected successfully", responseDto));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Evidence not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to reject evidence: " + e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/validate")
    @Operation(summary = "Validate evidence", description = "Perform detailed validation with scoring criteria")
    @PreAuthorize("hasRole('VALIDATOR') or hasRole('MODERATOR')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence validated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot validate this evidence"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidence not found")
    })
    public ResponseEntity<ApiResponse<EvidenceResponseDto>> validateEvidence(
            @Parameter(description = "Evidence UUID") @PathVariable String uuid,
            @RequestBody @Valid EvidenceValidationRequestDto request,
            Authentication authentication) {
        
        try {
            User validator = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
            
            Evidence evidence = evidenceService.validateEvidence(uuid, validator, request);
            EvidenceResponseDto responseDto = EvidenceResponseDto.fromEntity(evidence);
            
            return ResponseEntity.ok(ApiResponse.success("Evidence validated successfully", responseDto));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Evidence not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to validate evidence: " + e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/flag")
    @Operation(summary = "Flag evidence", description = "Flag evidence for content review")
    @PreAuthorize("hasRole('USER')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence flagged successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidence not found")
    })
    public ResponseEntity<ApiResponse<String>> flagEvidence(
            @Parameter(description = "Evidence UUID") @PathVariable String uuid,
            @RequestParam String reason,
            Authentication authentication) {
        
        try {
            User reporter = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            evidenceService.reportInappropriateContent(uuid, reporter, reason);
            
            return ResponseEntity.ok(ApiResponse.success("Evidence flagged for review", "Thank you for reporting inappropriate content"));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Evidence not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to flag evidence: " + e.getMessage()));
        }
    }

    // ========== QUERY OPERATIONS ==========

    @GetMapping
    @Operation(summary = "List evidence", description = "Get paginated list of evidence with filtering")
    public ResponseEntity<ApiResponse<PaginationResponse<EvidenceResponseDto>>> listEvidence(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) EvidenceStatus status,
            @RequestParam(required = false) EvidenceType type,
            @RequestParam(required = false) Long challengeId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Evidence> evidencePage;
            
            if (search != null && !search.trim().isEmpty()) {
                evidencePage = evidenceService.searchEvidence(search, pageable);
            } else if (status != null) {
                // For status filtering, we'll use the findEvidenceByStatus method
                // and convert it to a Page manually since we need pagination
                List<Evidence> evidenceList = evidenceService.findEvidenceByStatus(status);
                
                // Create a pageable subset
                int start = Math.min((int) pageable.getOffset(), evidenceList.size());
                int end = Math.min((start + pageable.getPageSize()), evidenceList.size());
                List<Evidence> pagedList = evidenceList.subList(start, end);
                
                evidencePage = new org.springframework.data.domain.PageImpl<>(
                    pagedList, pageable, evidenceList.size());
            } else {
                evidencePage = evidenceService.findEvidence(pageable);
            }
            
            List<EvidenceResponseDto> evidenceList = evidencePage.getContent().stream()
                .map(EvidenceResponseDto::fromEntity)
                .collect(Collectors.toList());
            
            PaginationResponse<EvidenceResponseDto> response = new PaginationResponse<>(
                evidenceList,
                evidencePage.getNumber(),
                evidencePage.getSize(),
                evidencePage.getTotalElements()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Evidence list retrieved successfully", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve evidence list: " + e.getMessage()));
        }
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending evidence", description = "Get evidence awaiting validation")
    @PreAuthorize("hasRole('VALIDATOR') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<EvidenceResponseDto>>> getPendingEvidence(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Evidence> pendingEvidence = evidenceService.findPendingValidation()
                .stream()
                .limit(limit)
                .toList();
            
            List<EvidenceResponseDto> responseList = pendingEvidence.stream()
                .map(EvidenceResponseDto::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Pending evidence retrieved successfully", responseList));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve pending evidence: " + e.getMessage()));
        }
    }

    @GetMapping("/flagged")
    @Operation(summary = "Get flagged evidence", description = "Get evidence flagged for review")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<EvidenceResponseDto>>> getFlaggedEvidence() {
        
        try {
            List<Evidence> flaggedEvidence = evidenceService.findFlaggedEvidence();
            
            List<EvidenceResponseDto> responseList = flaggedEvidence.stream()
                .map(EvidenceResponseDto::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Flagged evidence retrieved successfully", responseList));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve flagged evidence: " + e.getMessage()));
        }
    }

    @GetMapping("/my-validation-queue")
    @Operation(summary = "Get my validation queue", description = "Get evidence that I can validate")
    @PreAuthorize("hasRole('VALIDATOR') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<EvidenceResponseDto>>> getMyValidationQueue(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            User validator = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
            
            List<Evidence> validationQueue = evidenceService.findEvidenceNeedingValidation(validator.getUuid())
                .stream()
                .limit(limit)
                .toList();
            
            List<EvidenceResponseDto> responseList = validationQueue.stream()
                .map(EvidenceResponseDto::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Validation queue retrieved successfully", responseList));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Validator not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validation queue: " + e.getMessage()));
        }
    }

    // ========== ANALYTICS ==========

    @GetMapping("/analytics/stats")
    @Operation(summary = "Get evidence statistics", description = "Get overall evidence statistics")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Object>> getEvidenceStats() {
        
        try {
            long totalPending = evidenceService.countEvidenceByStatus(EvidenceStatus.PENDING);
            long totalApproved = evidenceService.countEvidenceByStatus(EvidenceStatus.APPROVED);
            long totalRejected = evidenceService.countEvidenceByStatus(EvidenceStatus.REJECTED);
            long totalFlagged = evidenceService.countEvidenceByStatus(EvidenceStatus.FLAGGED);
            
            Object stats = new Object() {
                public final long pending = totalPending;
                public final long approved = totalApproved;
                public final long rejected = totalRejected;
                public final long flagged = totalFlagged;
                public final long total = totalPending + totalApproved + totalRejected + totalFlagged;
            };
            
            return ResponseEntity.ok(ApiResponse.success("Evidence statistics retrieved successfully", stats));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve evidence statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/{uuid}/file-url")
    @Operation(summary = "Get evidence file URL", description = "Get download URL for evidence file")
    public ResponseEntity<ApiResponse<String>> getEvidenceFileUrl(
            @Parameter(description = "Evidence UUID") @PathVariable String uuid) {
        
        try {
            String fileUrl = evidenceService.generateFileUrl(uuid);
            
            if (fileUrl == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Evidence has no file attachment"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("File URL generated successfully", fileUrl));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Evidence not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to generate file URL: " + e.getMessage()));
        }
    }
}

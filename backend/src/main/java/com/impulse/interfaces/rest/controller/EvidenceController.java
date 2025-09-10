package com.impulse.interfaces.rest.controller;

import com.impulse.application.service.EvidenceService;
import com.impulse.domain.model.Evidence;
import com.impulse.domain.model.enums.EvidenceStatus;
import com.impulse.domain.model.enums.EvidenceType;
import com.impulse.interfaces.rest.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de Evidence
 */
@RestController
@RequestMapping("/api/evidence")
@CrossOrigin(origins = "*")
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    // ===== OPERACIONES CRUD =====

    /**
     * Enviar nueva evidencia
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EvidenceResponseDto> submitEvidence(
            @Valid @RequestBody EvidenceSubmitDto submitDto,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            Evidence evidence = evidenceService.submitEvidence(
                submitDto.getChallengeId(),
                currentUser,
                submitDto.getDescription(),
                EvidenceType.valueOf(submitDto.getEvidenceType()),
                submitDto.getSubmissionUrl(),
                submitDto.getRepositoryUrl(),
                submitDto.getDeploymentUrl(),
                submitDto.getAdditionalNotes(),
                submitDto.getIsPublic(),
                submitDto.getAllowComments()
            );
            
            EvidenceResponseDto responseDto = convertToResponseDto(evidence);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Enviar evidencia con archivos adjuntos
     */
    @PostMapping("/with-files")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EvidenceResponseDto> submitEvidenceWithFiles(
            @RequestParam Long challengeId,
            @RequestParam String description,
            @RequestParam String evidenceType,
            @RequestParam(required = false) String submissionUrl,
            @RequestParam(required = false) String repositoryUrl,
            @RequestParam(required = false) String deploymentUrl,
            @RequestParam(required = false) String additionalNotes,
            @RequestParam(defaultValue = "true") Boolean isPublic,
            @RequestParam(defaultValue = "true") Boolean allowComments,
            @RequestParam(required = false) MultipartFile[] files,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            Evidence evidence = evidenceService.submitEvidenceWithFiles(
                challengeId,
                currentUser,
                description,
                EvidenceType.valueOf(evidenceType),
                submissionUrl,
                repositoryUrl,
                deploymentUrl,
                additionalNotes,
                isPublic,
                allowComments,
                files
            );
            
            EvidenceResponseDto responseDto = convertToResponseDto(evidence);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener evidencia por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvidenceResponseDto> getEvidenceById(@PathVariable Long id) {
        
        Optional<Evidence> evidence = evidenceService.getEvidenceById(id);
        
        if (evidence.isPresent()) {
            EvidenceResponseDto responseDto = convertToResponseDto(evidence.get());
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualizar evidencia
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @evidenceService.isOwner(#id, authentication.name)")
    public ResponseEntity<EvidenceResponseDto> updateEvidence(
            @PathVariable Long id,
            @Valid @RequestBody EvidenceSubmitDto updateDto,
            Authentication authentication) {
        
        try {
            Evidence updatedEvidence = evidenceService.updateEvidence(
                id,
                updateDto.getDescription(),
                updateDto.getSubmissionUrl(),
                updateDto.getRepositoryUrl(),
                updateDto.getDeploymentUrl(),
                updateDto.getAdditionalNotes(),
                updateDto.getIsPublic(),
                updateDto.getAllowComments()
            );
            
            EvidenceResponseDto responseDto = convertToResponseDto(updatedEvidence);
            return ResponseEntity.ok(responseDto);
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar evidencia
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @evidenceService.isOwner(#id, authentication.name)")
    public ResponseEntity<Void> deleteEvidence(@PathVariable Long id) {
        
        try {
            evidenceService.deleteEvidence(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== BÚSQUEDAS Y FILTROS =====

    /**
     * Obtener toda la evidencia con paginación
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<EvidenceResponseDto>> getAllEvidence(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String evidenceType,
            @RequestParam(required = false) Long challengeId,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Evidence> evidencePage;
        
        if (search != null && !search.trim().isEmpty()) {
            evidencePage = evidenceService.searchEvidence(search, pageable);
        } else if (challengeId != null) {
            evidencePage = evidenceService.getEvidenceByChallenge(challengeId, pageable);
        } else if (status != null) {
            evidencePage = evidenceService.getEvidenceByStatus(EvidenceStatus.valueOf(status), pageable);
        } else {
            evidencePage = evidenceService.getAllPublicEvidence(pageable);
        }
        
        PageResponseDto<EvidenceResponseDto> response = convertToPageResponse(evidencePage, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener evidencia por challenge
     */
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<PageResponseDto<EvidenceResponseDto>> getEvidenceByChallenge(
            @PathVariable Long challengeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Evidence> evidencePage = evidenceService.getEvidenceByChallenge(challengeId, pageable);
        PageResponseDto<EvidenceResponseDto> response = convertToPageResponse(evidencePage, page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener evidencia por usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponseDto<EvidenceResponseDto>> getEvidenceByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Evidence> evidencePage = evidenceService.getEvidenceByUser(userId, pageable);
        PageResponseDto<EvidenceResponseDto> response = convertToPageResponse(evidencePage, page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener mi evidencia
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponseDto<EvidenceResponseDto>> getMyEvidence(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Evidence> evidencePage = evidenceService.getEvidenceByUsername(currentUser, pageable);
        PageResponseDto<EvidenceResponseDto> response = convertToPageResponse(evidencePage, page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener evidencia trending/destacada
     */
    @GetMapping("/trending")
    public ResponseEntity<List<EvidenceResponseDto>> getTrendingEvidence(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Evidence> trendingEvidence = evidenceService.getTrendingEvidence(limit);
        
        List<EvidenceResponseDto> response = trendingEvidence.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener evidencia de alta calidad
     */
    @GetMapping("/high-quality")
    public ResponseEntity<PageResponseDto<EvidenceResponseDto>> getHighQualityEvidence(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "80") int minQualityScore) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("qualityScore").descending());
        
        Page<Evidence> evidencePage = evidenceService.getHighQualityEvidence(minQualityScore, pageable);
        PageResponseDto<EvidenceResponseDto> response = convertToPageResponse(evidencePage, page, size);
        
        return ResponseEntity.ok(response);
    }

    // ===== VALIDACIÓN Y MODERACIÓN =====

    /**
     * Obtener evidencia pendiente de validación
     */
    @GetMapping("/pending-validation")
    @PreAuthorize("hasRole('VALIDATOR') or hasRole('ADMIN')")
    public ResponseEntity<PageResponseDto<EvidenceResponseDto>> getPendingValidationEvidence(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").ascending());
        
        Page<Evidence> evidencePage = evidenceService.getPendingValidationEvidence(pageable);
        PageResponseDto<EvidenceResponseDto> response = convertToPageResponse(evidencePage, page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validar evidencia
     */
    @PostMapping("/{id}/validate")
    @PreAuthorize("hasRole('VALIDATOR') or hasRole('ADMIN')")
    public ResponseEntity<EvidenceResponseDto> validateEvidence(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String feedback,
            @RequestParam(required = false) Integer qualityScore,
            Authentication authentication) {
        
        String validator = authentication.getName();
        
        try {
            Evidence validatedEvidence = evidenceService.validateEvidence(
                id, validator, approved, feedback, qualityScore);
            
            EvidenceResponseDto responseDto = convertToResponseDto(validatedEvidence);
            return ResponseEntity.ok(responseDto);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Rechazar evidencia
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('VALIDATOR') or hasRole('ADMIN')")
    public ResponseEntity<EvidenceResponseDto> rejectEvidence(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        
        String validator = authentication.getName();
        
        try {
            Evidence rejectedEvidence = evidenceService.rejectEvidence(id, validator, reason);
            EvidenceResponseDto responseDto = convertToResponseDto(rejectedEvidence);
            return ResponseEntity.ok(responseDto);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== INTERACCIONES =====

    /**
     * Dar like a evidencia
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> likeEvidence(@PathVariable Long id, Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            evidenceService.likeEvidence(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Quitar like a evidencia
     */
    @DeleteMapping("/{id}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unlikeEvidence(@PathVariable Long id, Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            evidenceService.unlikeEvidence(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== ESTADÍSTICAS =====

    /**
     * Obtener estadísticas de evidencia
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Object> getEvidenceStats(@PathVariable Long id) {
        
        try {
            Object stats = evidenceService.getEvidenceStatistics(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener estadísticas globales de evidencia
     */
    @GetMapping("/stats/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getGlobalEvidenceStats() {
        
        Object stats = evidenceService.getGlobalEvidenceStatistics();
        return ResponseEntity.ok(stats);
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Convertir Evidence a EvidenceResponseDto
     */
    private EvidenceResponseDto convertToResponseDto(Evidence evidence) {
        EvidenceResponseDto dto = new EvidenceResponseDto();
        dto.setId(evidence.getId());
        dto.setChallengeId(evidence.getChallenge().getId());
        dto.setChallengeTitle(evidence.getChallenge().getTitle());
        dto.setUserId(evidence.getUser().getId());
        dto.setUsername(evidence.getUser().getUsername());
        dto.setDescription(evidence.getDescription());
        dto.setEvidenceType(evidence.getEvidenceType().toString());
        dto.setStatus(evidence.getStatus().toString());
        dto.setSubmissionUrl(evidence.getSubmissionUrl());
        dto.setRepositoryUrl(evidence.getRepositoryUrl());
        dto.setDeploymentUrl(evidence.getDeploymentUrl());
        dto.setAdditionalNotes(evidence.getAdditionalNotes());
        dto.setIsPublic(evidence.getIsPublic());
        dto.setAllowComments(evidence.getAllowComments());
        dto.setSubmittedAt(evidence.getSubmittedAt());
        dto.setReviewedAt(evidence.getReviewedAt());
        dto.setReviewFeedback(evidence.getReviewFeedback());
        dto.setQualityScore(evidence.getQualityScore());
        dto.setValidationCount(evidence.getValidationCount());
        
        // Aquí se mapearían otros campos como attachmentUrls, likesCount, etc.
        
        return dto;
    }

    /**
     * Convertir Page<Evidence> a PageResponseDto<EvidenceResponseDto>
     */
    private PageResponseDto<EvidenceResponseDto> convertToPageResponse(Page<Evidence> evidencePage, int page, int size) {
        List<EvidenceResponseDto> content = evidencePage.getContent().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
        
        return new PageResponseDto<>(
            content,
            page,
            size,
            evidencePage.getTotalElements(),
            evidencePage.getTotalPages()
        );
    }
}

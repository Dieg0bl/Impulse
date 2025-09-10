package com.impulse.lean.application.controller;

import com.impulse.lean.application.dto.challenge.ChallengeResponseDto;
import com.impulse.lean.application.dto.common.ApiResponse;
import com.impulse.lean.application.dto.common.PaginationRequest;
import com.impulse.lean.application.dto.common.PaginationResponse;
import com.impulse.lean.domain.model.Challenge;
import com.impulse.lean.domain.model.ChallengeCategory;
import com.impulse.lean.domain.model.ChallengeDifficulty;
import com.impulse.lean.domain.repository.ChallengeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IMPULSE LEAN v1 - Challenge REST Controller
 * 
 * Handles HTTP requests for challenge operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/challenges")
@Tag(name = "Challenges", description = "Challenge management operations")
public class ChallengeController {

    private final ChallengeRepository challengeRepository;

    @Autowired
    public ChallengeController(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @GetMapping
    @Operation(summary = "Get all challenges", description = "Retrieve a paginated list of challenges")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Challenges retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<ApiResponse<PaginationResponse<ChallengeResponseDto>>> getAllChallenges(
            @Valid @ModelAttribute PaginationRequest request,
            @RequestParam(required = false) ChallengeCategory category,
            @RequestParam(required = false) ChallengeDifficulty difficulty,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

        try {
            Sort sort = Sort.by(
                request.getSafeSortDirection().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                request.getSafeSortBy() != null ? request.getSafeSortBy() : "createdAt"
            );

            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            Page<Challenge> challengePage;

            if (request.hasSearch()) {
                challengePage = challengeRepository.searchChallenges(request.getSafeSearch(), pageable);
            } else if (activeOnly) {
                challengePage = challengeRepository.findByStatusOrderByCreatedAtDesc(
                    com.impulse.lean.domain.model.ChallengeStatus.PUBLISHED, pageable);
            } else {
                challengePage = challengeRepository.findAll(pageable);
            }

            // Convert to DTOs
            List<ChallengeResponseDto> challengeDtos = challengePage.getContent().stream()
                .map(ChallengeResponseDto::from)
                .collect(Collectors.toList());

            PaginationResponse<ChallengeResponseDto> response = new PaginationResponse<>(
                challengeDtos,
                challengePage.getNumber(),
                challengePage.getSize(),
                challengePage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve challenges: " + e.getMessage()));
        }
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get challenge by UUID", description = "Retrieve a specific challenge by its UUID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Challenge found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    public ResponseEntity<ApiResponse<ChallengeResponseDto>> getChallengeByUuid(
            @Parameter(description = "Challenge UUID") @PathVariable String uuid) {

        Optional<Challenge> challengeOpt = challengeRepository.findByUuid(uuid);
        
        if (challengeOpt.isPresent()) {
            ChallengeResponseDto dto = ChallengeResponseDto.from(challengeOpt.get());
            return ResponseEntity.ok(ApiResponse.success(dto));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Get active challenges", description = "Retrieve all currently active challenges")
    public ResponseEntity<ApiResponse<List<ChallengeResponseDto>>> getActiveChallenges() {
        try {
            List<Challenge> activeChallenges = challengeRepository.findActiveAndNotExpiredChallenges(
                LocalDateTime.now());
            
            List<ChallengeResponseDto> challengeDtos = activeChallenges.stream()
                .map(ChallengeResponseDto::summary)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(challengeDtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve active challenges: " + e.getMessage()));
        }
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured challenges", description = "Retrieve featured challenges")
    public ResponseEntity<ApiResponse<List<ChallengeResponseDto>>> getFeaturedChallenges() {
        try {
            // For now, just return active challenges
            List<Challenge> activeChallenges = challengeRepository.findActiveChallenges();
            
            List<ChallengeResponseDto> challengeDtos = activeChallenges.stream()
                .limit(5) // Limit to 5 featured challenges
                .map(ChallengeResponseDto::summary)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(challengeDtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve featured challenges: " + e.getMessage()));
        }
    }

    @GetMapping("/categories/{category}")
    @Operation(summary = "Get challenges by category", description = "Retrieve challenges in a specific category")
    public ResponseEntity<ApiResponse<List<ChallengeResponseDto>>> getChallengesByCategory(
            @Parameter(description = "Challenge category") @PathVariable ChallengeCategory category) {
        
        try {
            List<Challenge> challenges = challengeRepository.findActiveChallengesByCategory(category);
            
            List<ChallengeResponseDto> challengeDtos = challenges.stream()
                .map(ChallengeResponseDto::summary)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(challengeDtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve challenges by category: " + e.getMessage()));
        }
    }

    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "Get challenges by difficulty", description = "Retrieve challenges by difficulty level")
    public ResponseEntity<ApiResponse<List<ChallengeResponseDto>>> getChallengesByDifficulty(
            @Parameter(description = "Challenge difficulty") @PathVariable ChallengeDifficulty difficulty) {
        
        try {
            List<Challenge> challenges = challengeRepository.findActiveChallengesByDifficulty(difficulty);
            
            List<ChallengeResponseDto> challengeDtos = challenges.stream()
                .map(ChallengeResponseDto::summary)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(challengeDtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve challenges by difficulty: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search challenges", description = "Search challenges by title or description")
    public ResponseEntity<ApiResponse<PaginationResponse<ChallengeResponseDto>>> searchChallenges(
            @RequestParam String query,
            @Valid @ModelAttribute PaginationRequest request) {
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            
            Page<Challenge> challengePage = challengeRepository.searchActiveChallenges(query, pageable);
            
            List<ChallengeResponseDto> challengeDtos = challengePage.getContent().stream()
                .map(ChallengeResponseDto::summary)
                .collect(Collectors.toList());

            PaginationResponse<ChallengeResponseDto> response = new PaginationResponse<>(
                challengeDtos,
                challengePage.getNumber(),
                challengePage.getSize(),
                challengePage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to search challenges: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get challenge statistics", description = "Retrieve challenge statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Object>> getChallengeStats() {
        try {
            // Simple statistics
            long totalChallenges = challengeRepository.count();
            long activeChallenges = challengeRepository.countByStatus(
                com.impulse.lean.domain.model.ChallengeStatus.PUBLISHED);
            
            var stats = new Object() {
                public final long total = totalChallenges;
                public final long active = activeChallenges;
                public final long draft = totalChallenges - activeChallenges;
            };

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve challenge statistics: " + e.getMessage()));
        }
    }
}

package com.impulse.lean.application.controller;

import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.application.dto.challenge.ChallengeCreateRequestDto;
import com.impulse.lean.application.dto.challenge.ChallengeJoinRequestDto;
import com.impulse.lean.application.dto.challenge.ChallengeResponseDto;
import com.impulse.lean.application.dto.challenge.ChallengeUpdateRequestDto;
import com.impulse.lean.application.dto.common.ApiResponse;
import com.impulse.lean.application.dto.common.PaginationRequest;
import com.impulse.lean.application.dto.common.PaginationResponse;
import com.impulse.lean.application.service.interfaces.ChallengeService;
import com.impulse.lean.application.service.interfaces.UserService;
import com.impulse.lean.domain.model.Challenge;
import com.impulse.lean.domain.model.ChallengeCategory;
import com.impulse.lean.domain.model.ChallengeDifficulty;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.repository.ChallengeRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * IMPULSE LEAN v1 - Challenge REST Controller
 * 
 * Complete CRUD operations for challenge management
 * Supports filtering, pagination, participation management
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/challenges")
@Tag(name = "Challenges", description = "Challenge management operations")
public class ChallengeController {

    private final ChallengeRepository challengeRepository;
    private final ChallengeService challengeService;
    private final UserService userService;

    @Autowired
    public ChallengeController(ChallengeRepository challengeRepository, ChallengeService challengeService, UserService userService) {
        this.challengeRepository = challengeRepository;
        this.challengeService = challengeService;
        this.userService = userService;
    }

    // ========== CHALLENGE CRUD OPERATIONS ==========

    @PostMapping
    @Operation(summary = "Create new challenge", description = "Create a new challenge with validation")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ChallengeResponseDto>> createChallenge(
            @Valid @RequestBody ChallengeCreateRequestDto request,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            User creator = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            Challenge challenge = challengeService.createChallenge(creator, request);
            ChallengeResponseDto challengeDto = ChallengeResponseDto.from(challenge);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Challenge created successfully", challengeDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create challenge: " + e.getMessage()));
        }
    }

    @PatchMapping("/{uuid}")
    @Operation(summary = "Update challenge", description = "Update a challenge by UUID")
    @PreAuthorize("hasRole('USER')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Challenge updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Challenge not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Not authorized to update this challenge")
    })
    public ResponseEntity<ApiResponse<ChallengeResponseDto>> updateChallenge(
            @Parameter(description = "Challenge UUID") @PathVariable String uuid,
            @Valid @RequestBody ChallengeUpdateRequestDto request,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Check if user can modify this challenge
            if (!challengeService.canChallengeBeModified(uuid, user.getUuid())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You are not authorized to modify this challenge"));
            }
            
            Challenge updatedChallenge = challengeService.updateChallenge(uuid, request);
            ChallengeResponseDto challengeDto = ChallengeResponseDto.from(updatedChallenge);
            
            return ResponseEntity.ok(ApiResponse.success("Challenge updated successfully", challengeDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Challenge not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update challenge: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete challenge", description = "Soft delete a challenge by UUID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Challenge deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Challenge not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Not authorized to delete this challenge")
    })
    public ResponseEntity<ApiResponse<Void>> deleteChallenge(
            @Parameter(description = "Challenge UUID") @PathVariable String uuid,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Check if user can modify this challenge (or is admin)
            boolean isAdmin = user.getRole().toString().equals("ADMIN");
            if (!isAdmin && !challengeService.canChallengeBeModified(uuid, user.getUuid())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You are not authorized to delete this challenge"));
            }
            
            challengeService.deleteChallenge(uuid);
            
            return ResponseEntity.ok(ApiResponse.success("Challenge deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Challenge not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete challenge: " + e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/publish")
    @Operation(summary = "Publish challenge", description = "Publish a draft challenge to make it available for participation")
    @PreAuthorize("hasRole('USER')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Challenge published successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Challenge not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Not authorized to publish this challenge")
    })
    public ResponseEntity<ApiResponse<ChallengeResponseDto>> publishChallenge(
            @Parameter(description = "Challenge UUID") @PathVariable String uuid,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Check if user can modify this challenge
            if (!challengeService.canChallengeBeModified(uuid, user.getUuid())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You are not authorized to publish this challenge"));
            }
            
            Challenge publishedChallenge = challengeService.publishChallenge(uuid);
            ChallengeResponseDto challengeDto = ChallengeResponseDto.from(publishedChallenge);
            
            return ResponseEntity.ok(ApiResponse.success("Challenge published successfully", challengeDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Challenge not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to publish challenge: " + e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/join")
    @Operation(summary = "Join challenge", description = "Join a challenge as a participant")
    @PreAuthorize("hasRole('USER')")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully joined challenge"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Challenge not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot join this challenge")
    })
    public ResponseEntity<ApiResponse<String>> joinChallenge(
            @Parameter(description = "Challenge UUID") @PathVariable String uuid,
            @Valid @RequestBody ChallengeJoinRequestDto request,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Check if user can join this challenge
            if (!challengeService.canUserJoinChallenge(uuid, user.getUuid())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("You cannot join this challenge"));
            }
            
            // Check if user is already participating
            if (challengeService.isUserParticipating(uuid, user.getUuid())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("You are already participating in this challenge"));
            }
            
            challengeService.joinChallenge(uuid, user.getUuid(), request);
            
            return ResponseEntity.ok(ApiResponse.success("Successfully joined challenge", "Challenge joined successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Challenge not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to join challenge: " + e.getMessage()));
        }
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

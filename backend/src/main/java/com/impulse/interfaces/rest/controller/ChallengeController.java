package com.impulse.interfaces.rest.controller;

import com.impulse.application.service.ChallengeService;
import com.impulse.domain.model.Challenge;
import com.impulse.domain.model.enums.ChallengeStatus;
import com.impulse.domain.model.enums.ChallengeDifficulty;
import com.impulse.domain.model.enums.ChallengeCategory;
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

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de Challenges
 */
@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = "*")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    // ===== OPERACIONES CRUD =====

    /**
     * Crear nuevo challenge
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<ChallengeResponseDto> createChallenge(
            @Valid @RequestBody ChallengeCreateDto createDto,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        Challenge challenge = challengeService.createChallenge(
            createDto.getTitle(),
            createDto.getDescription(),
            ChallengeCategory.valueOf(createDto.getCategory()),
            ChallengeDifficulty.valueOf(createDto.getDifficulty()),
            createDto.getPoints(),
            createDto.getEstimatedDurationMinutes(),
            createDto.getStartDate(),
            createDto.getEndDate(),
            createDto.getInstructions(),
            createDto.getSuccessCriteria(),
            createDto.getTags(),
            createDto.getRequiredSkills(),
            createDto.getIsPublic(),
            createDto.getAllowTeams(),
            createDto.getMaxParticipants(),
            currentUser
        );
        
        ChallengeResponseDto responseDto = convertToResponseDto(challenge);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Obtener challenge por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto> getChallengeById(@PathVariable Long id) {
        
        Optional<Challenge> challenge = challengeService.getChallengeById(id);
        
        if (challenge.isPresent()) {
            ChallengeResponseDto responseDto = convertToResponseDto(challenge.get());
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualizar challenge
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @challengeService.isOwner(#id, authentication.name)")
    public ResponseEntity<ChallengeResponseDto> updateChallenge(
            @PathVariable Long id,
            @Valid @RequestBody ChallengeCreateDto updateDto,
            Authentication authentication) {
        
        try {
            Challenge updatedChallenge = challengeService.updateChallenge(
                id,
                updateDto.getTitle(),
                updateDto.getDescription(),
                updateDto.getInstructions(),
                updateDto.getSuccessCriteria(),
                updateDto.getTags(),
                updateDto.getRequiredSkills(),
                updateDto.getIsPublic(),
                updateDto.getAllowTeams(),
                updateDto.getMaxParticipants()
            );
            
            ChallengeResponseDto responseDto = convertToResponseDto(updatedChallenge);
            return ResponseEntity.ok(responseDto);
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar challenge
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @challengeService.isOwner(#id, authentication.name)")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        
        try {
            challengeService.deleteChallenge(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== BÚSQUEDAS Y FILTROS =====

    /**
     * Obtener todos los challenges con paginación
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<ChallengeResponseDto>> getAllChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Challenge> challengePage;
        
        if (search != null && !search.trim().isEmpty()) {
            challengePage = challengeService.searchChallenges(search, pageable);
        } else {
            challengePage = challengeService.getAllChallenges(pageable);
        }
        
        PageResponseDto<ChallengeResponseDto> response = convertToPageResponse(challengePage, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener challenges activos
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseDto<ChallengeResponseDto>> getActiveChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Challenge> challengePage = challengeService.getActiveChallenges(pageable);
        PageResponseDto<ChallengeResponseDto> response = convertToPageResponse(challengePage, page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener challenges por categoría
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<PageResponseDto<ChallengeResponseDto>> getChallengesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            ChallengeCategory categoryEnum = ChallengeCategory.valueOf(category.toUpperCase());
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Challenge> challengePage = challengeService.getChallengesByCategory(categoryEnum, pageable);
            PageResponseDto<ChallengeResponseDto> response = convertToPageResponse(challengePage, page, size);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener challenges por dificultad
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<PageResponseDto<ChallengeResponseDto>> getChallengesByDifficulty(
            @PathVariable String difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            ChallengeDifficulty difficultyEnum = ChallengeDifficulty.valueOf(difficulty.toUpperCase());
            Pageable pageable = PageRequest.of(page, size);
            
            Page<Challenge> challengePage = challengeService.getChallengesByDifficulty(difficultyEnum, pageable);
            PageResponseDto<ChallengeResponseDto> response = convertToPageResponse(challengePage, page, size);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener challenges trending
     */
    @GetMapping("/trending")
    public ResponseEntity<List<ChallengeResponseDto>> getTrendingChallenges(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Challenge> trendingChallenges = challengeService.getTrendingChallenges(limit);
        
        List<ChallengeResponseDto> response = trendingChallenges.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener challenges recomendados para un usuario
     */
    @GetMapping("/recommended")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ChallengeResponseDto>> getRecommendedChallenges(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit) {
        
        String currentUser = authentication.getName();
        List<Challenge> recommendedChallenges = challengeService.getRecommendedChallenges(currentUser, limit);
        
        List<ChallengeResponseDto> response = recommendedChallenges.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    // ===== PARTICIPACIÓN =====

    /**
     * Unirse a un challenge
     */
    @PostMapping("/{id}/join")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> joinChallenge(@PathVariable Long id, Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            challengeService.joinChallenge(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Abandonar un challenge
     */
    @PostMapping("/{id}/leave")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> leaveChallenge(@PathVariable Long id, Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            challengeService.leaveChallenge(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Completar un challenge
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> completeChallenge(@PathVariable Long id, Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            challengeService.completeChallenge(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== GESTIÓN DE ESTADO =====

    /**
     * Publicar challenge
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN') or @challengeService.isOwner(#id, authentication.name)")
    public ResponseEntity<ChallengeResponseDto> publishChallenge(@PathVariable Long id) {
        
        try {
            Challenge publishedChallenge = challengeService.publishChallenge(id);
            ChallengeResponseDto responseDto = convertToResponseDto(publishedChallenge);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Archivar challenge
     */
    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN') or @challengeService.isOwner(#id, authentication.name)")
    public ResponseEntity<ChallengeResponseDto> archiveChallenge(@PathVariable Long id) {
        
        try {
            Challenge archivedChallenge = challengeService.archiveChallenge(id);
            ChallengeResponseDto responseDto = convertToResponseDto(archivedChallenge);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== ESTADÍSTICAS =====

    /**
     * Obtener estadísticas del challenge
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Object> getChallengeStats(@PathVariable Long id) {
        
        try {
            Object stats = challengeService.getChallengeStatistics(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener participantes del challenge
     */
    @GetMapping("/{id}/participants")
    public ResponseEntity<PageResponseDto<UserResponseDto>> getChallengeParticipants(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            // Esta funcionalidad se implementaría en el servicio
            // Page<User> participants = challengeService.getChallengeParticipants(id, PageRequest.of(page, size));
            // Por ahora retornamos una respuesta vacía
            PageResponseDto<UserResponseDto> response = new PageResponseDto<>();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Convertir Challenge a ChallengeResponseDto
     */
    private ChallengeResponseDto convertToResponseDto(Challenge challenge) {
        ChallengeResponseDto dto = new ChallengeResponseDto();
        dto.setId(challenge.getId());
        dto.setTitle(challenge.getTitle());
        dto.setDescription(challenge.getDescription());
        dto.setCategory(challenge.getCategory().toString());
        dto.setDifficulty(challenge.getDifficulty().toString());
        dto.setStatus(challenge.getStatus().toString());
        dto.setPoints(challenge.getPoints());
        dto.setEstimatedDurationMinutes(challenge.getEstimatedDurationMinutes());
        dto.setStartDate(challenge.getStartDate());
        dto.setEndDate(challenge.getEndDate());
        dto.setInstructions(challenge.getInstructions());
        dto.setSuccessCriteria(challenge.getSuccessCriteria());
        dto.setTags(challenge.getTags());
        dto.setRequiredSkills(challenge.getRequiredSkills());
        dto.setIsPublic(challenge.getIsPublic());
        dto.setAllowTeams(challenge.getAllowTeams());
        dto.setMaxParticipants(challenge.getMaxParticipants());
        dto.setCurrentParticipants(challenge.getCurrentParticipants());
        dto.setCreatedAt(challenge.getCreatedAt());
        dto.setUpdatedAt(challenge.getUpdatedAt());
        dto.setAverageRating(challenge.getAverageRating());
        dto.setTotalRatings(challenge.getTotalRatings());
        dto.setCompletionCount(challenge.getCompletionCount());
        dto.setImageUrl(challenge.getImageUrl());
        dto.setIsFeatured(challenge.getIsFeatured());
        dto.setViewCount(challenge.getViewCount());
        
        return dto;
    }

    /**
     * Convertir Page<Challenge> a PageResponseDto<ChallengeResponseDto>
     */
    private PageResponseDto<ChallengeResponseDto> convertToPageResponse(Page<Challenge> challengePage, int page, int size) {
        List<ChallengeResponseDto> content = challengePage.getContent().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
        
        return new PageResponseDto<>(
            content,
            page,
            size,
            challengePage.getTotalElements(),
            challengePage.getTotalPages()
        );
    }
}

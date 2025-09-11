package com.impulse.lean.application.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.application.dto.common.ApiResponse;
import com.impulse.lean.application.dto.common.PaginationResponse;
import com.impulse.lean.application.dto.validator.ValidatorAssignmentRequestDto;
import com.impulse.lean.application.dto.validator.ValidatorAssignmentResponseDto;
import com.impulse.lean.application.dto.validator.ValidatorRequestDto;
import com.impulse.lean.application.dto.validator.ValidatorResponseDto;
import com.impulse.user.service.UserService;
import com.impulse.lean.application.service.interfaces.ValidatorService;
import com.impulse.lean.domain.model.AssignmentStatus;
import com.impulse.user.model.User;
import com.impulse.lean.domain.model.Validator;
import com.impulse.lean.domain.model.ValidatorAssignment;
import com.impulse.lean.domain.model.ValidatorSpecialty;
import com.impulse.lean.domain.model.ValidatorStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * IMPULSE LEAN v1 - Validator REST Controller
 * 
 * Complete CRUD operations for validator management
 * Supports assignment workflow, specialties, and validator analytics
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/validators")
@Tag(name = "Validators", description = "Validator management operations")
public class ValidatorController {

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private UserService userService;

    // Validator CRUD operations
    @PostMapping("/register")
    @Operation(summary = "Register as validator", description = "Register current user as a validator")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> registerValidator(
            @Valid @RequestBody ValidatorRequestDto request,
            Authentication authentication) {

        try {
            User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Validator validator = validatorService.registerValidator(user, request);
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validator);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Validator registration submitted successfully", responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to register validator: " + e.getMessage()));
        }
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get validator by UUID", description = "Retrieve a specific validator by UUID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Validator found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Validator not found")
    })
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> getValidatorByUuid(
            @Parameter(description = "Validator UUID") @PathVariable String uuid) {

        Optional<Validator> validatorOpt = validatorService.findByUuid(uuid);
        if (validatorOpt.isPresent()) {
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validatorOpt.get());
            return ResponseEntity.ok(ApiResponse.success("Validator found", responseDto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Validator not found"));
        }
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update validator", description = "Update validator information")
    @PreAuthorize("hasRole('ADMIN') or @validatorService.findByUuid(#uuid).map(v -> v.user.uuid).orElse('') == authentication.principal.uuid")
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> updateValidator(
            @Parameter(description = "Validator UUID") @PathVariable String uuid,
            @Valid @RequestBody ValidatorRequestDto request) {

        try {
            Validator validator = validatorService.updateValidator(uuid, request);
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validator);

            return ResponseEntity.ok(ApiResponse.success("Validator updated successfully", responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update validator: " + e.getMessage()));
        }
    }

    @PatchMapping("/{uuid}/activate")
    @Operation(summary = "Activate validator", description = "Activate a validator (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> activateValidator(
            @Parameter(description = "Validator UUID") @PathVariable String uuid) {

        try {
            Validator validator = validatorService.activateValidator(uuid);
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validator);

            return ResponseEntity.ok(ApiResponse.success("Validator activated successfully", responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to activate validator: " + e.getMessage()));
        }
    }

    @PatchMapping("/{uuid}/deactivate")
    @Operation(summary = "Deactivate validator", description = "Deactivate a validator (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> deactivateValidator(
            @Parameter(description = "Validator UUID") @PathVariable String uuid) {

        try {
            Validator validator = validatorService.deactivateValidator(uuid);
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validator);

            return ResponseEntity.ok(ApiResponse.success("Validator deactivated successfully", responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to deactivate validator: " + e.getMessage()));
        }
    }

    // Validator listing and search
    @GetMapping
    @Operation(summary = "List validators", description = "Get paginated list of validators with filtering")
    public ResponseEntity<ApiResponse<PaginationResponse<ValidatorResponseDto>>> listValidators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ValidatorStatus status,
            @RequestParam(required = false) ValidatorSpecialty specialty,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Validator> validatorPage;

            if (search != null && !search.trim().isEmpty()) {
                validatorPage = validatorService.searchValidators(search, pageable);
            } else {
                validatorPage = validatorService.findValidators(pageable);
            }

            List<ValidatorResponseDto> validatorList = validatorPage.getContent().stream()
                .map(ValidatorResponseDto::fromEntity)
                .collect(Collectors.toList());

            PaginationResponse<ValidatorResponseDto> response = new PaginationResponse<>(
                validatorList,
                validatorPage.getNumber(),
                validatorPage.getSize(),
                validatorPage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success("Validators retrieved successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validators: " + e.getMessage()));
        }
    }

    @GetMapping("/available")
    @Operation(summary = "Get available validators", description = "Get list of available validators")
    public ResponseEntity<ApiResponse<List<ValidatorResponseDto>>> getAvailableValidators(
            @RequestParam(required = false) ValidatorSpecialty specialty) {

        try {
            List<Validator> validators;
            if (specialty != null) {
                validators = validatorService.findValidatorsBySpecialty(specialty)
                    .stream()
                    .filter(v -> v.getStatus() == ValidatorStatus.ACTIVE && v.getAvailable())
                    .collect(Collectors.toList());
            } else {
                validators = validatorService.findAvailableValidators();
            }

            List<ValidatorResponseDto> responseList = validators.stream()
                .map(ValidatorResponseDto::fromEntity)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Available validators retrieved successfully", responseList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve available validators: " + e.getMessage()));
        }
    }

    // Validator assignment operations
    @PostMapping("/assignments")
    @Operation(summary = "Assign validator", description = "Assign a validator to evidence")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<ValidatorAssignmentResponseDto>> assignValidator(
            @RequestParam String evidenceUuid,
            @RequestParam String validatorUuid,
            @Valid @RequestBody ValidatorAssignmentRequestDto request) {

        try {
            ValidatorAssignment assignment = validatorService.assignValidator(evidenceUuid, validatorUuid, request);
            ValidatorAssignmentResponseDto responseDto = ValidatorAssignmentResponseDto.fromEntity(assignment);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Validator assigned successfully", responseDto));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to assign validator: " + e.getMessage()));
        }
    }

    @PostMapping("/assignments/auto")
    @Operation(summary = "Auto-assign validator", description = "Automatically assign best validator to evidence")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<ValidatorAssignmentResponseDto>> autoAssignValidator(
            @RequestParam String evidenceUuid) {

        try {
            ValidatorAssignment assignment = validatorService.autoAssignValidator(evidenceUuid);
            ValidatorAssignmentResponseDto responseDto = ValidatorAssignmentResponseDto.fromEntity(assignment);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Validator auto-assigned successfully", responseDto));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to auto-assign validator: " + e.getMessage()));
        }
    }

    @PutMapping("/assignments/{assignmentUuid}/reassign")
    @Operation(summary = "Reassign validator", description = "Reassign evidence to different validator")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<ValidatorAssignmentResponseDto>> reassignValidator(
            @Parameter(description = "Assignment UUID") @PathVariable String assignmentUuid,
            @RequestParam String newValidatorUuid,
            @RequestParam String reason) {

        try {
            ValidatorAssignment assignment = validatorService.reassignValidator(assignmentUuid, newValidatorUuid, reason);
            ValidatorAssignmentResponseDto responseDto = ValidatorAssignmentResponseDto.fromEntity(assignment);

            return ResponseEntity.ok(ApiResponse.success("Validator reassigned successfully", responseDto));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to reassign validator: " + e.getMessage()));
        }
    }

    @DeleteMapping("/assignments/{assignmentUuid}")
    @Operation(summary = "Unassign validator", description = "Remove validator assignment")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<ValidatorAssignmentResponseDto>> unassignValidator(
            @Parameter(description = "Assignment UUID") @PathVariable String assignmentUuid,
            @RequestParam String reason) {

        try {
            ValidatorAssignment assignment = validatorService.unassignValidator(assignmentUuid, reason);
            ValidatorAssignmentResponseDto responseDto = ValidatorAssignmentResponseDto.fromEntity(assignment);

            return ResponseEntity.ok(ApiResponse.success("Validator unassigned successfully", responseDto));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to unassign validator: " + e.getMessage()));
        }
    }

    // Validator specialties management
    @PostMapping("/{uuid}/specialties")
    @Operation(summary = "Add specialty", description = "Add specialty to validator")
    @PreAuthorize("hasRole('ADMIN') or @validatorService.findByUuid(#uuid).map(v -> v.user.uuid).orElse('') == authentication.principal.uuid")
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> addSpecialty(
            @Parameter(description = "Validator UUID") @PathVariable String uuid,
            @RequestParam ValidatorSpecialty specialty) {

        try {
            Validator validator = validatorService.addSpecialty(uuid, specialty);
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validator);

            return ResponseEntity.ok(ApiResponse.success("Specialty added successfully", responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to add specialty: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{uuid}/specialties")
    @Operation(summary = "Remove specialty", description = "Remove specialty from validator")
    @PreAuthorize("hasRole('ADMIN') or @validatorService.findByUuid(#uuid).map(v -> v.user.uuid).orElse('') == authentication.principal.uuid")
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> removeSpecialty(
            @Parameter(description = "Validator UUID") @PathVariable String uuid,
            @RequestParam ValidatorSpecialty specialty) {

        try {
            Validator validator = validatorService.removeSpecialty(uuid, specialty);
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validator);

            return ResponseEntity.ok(ApiResponse.success("Specialty removed successfully", responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to remove specialty: " + e.getMessage()));
        }
    }

    // Validator availability management
    @PatchMapping("/{uuid}/availability")
    @Operation(summary = "Update availability", description = "Update validator availability")
    @PreAuthorize("hasRole('ADMIN') or @validatorService.findByUuid(#uuid).map(v -> v.user.uuid).orElse('') == authentication.principal.uuid")
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> updateAvailability(
            @Parameter(description = "Validator UUID") @PathVariable String uuid,
            @RequestParam boolean available) {

        try {
            validatorService.setValidatorAvailability(uuid, available);
            Validator validator = validatorService.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
            
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validator);

            return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update availability: " + e.getMessage()));
        }
    }

    @PatchMapping("/{uuid}/capacity")
    @Operation(summary = "Update capacity", description = "Update validator max assignments capacity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ValidatorResponseDto>> updateCapacity(
            @Parameter(description = "Validator UUID") @PathVariable String uuid,
            @RequestParam int maxAssignments) {

        try {
            validatorService.updateValidatorCapacity(uuid, maxAssignments);
            Validator validator = validatorService.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
            
            ValidatorResponseDto responseDto = ValidatorResponseDto.fromEntity(validator);

            return ResponseEntity.ok(ApiResponse.success("Capacity updated successfully", responseDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update capacity: " + e.getMessage()));
        }
    }

    // Validator assignment queries
    @GetMapping("/{uuid}/assignments")
    @Operation(summary = "Get validator assignments", description = "Get assignments for a validator")
    @PreAuthorize("hasRole('MODERATOR') or @validatorService.findByUuid(#uuid).map(v -> v.user.uuid).orElse('') == authentication.principal.uuid")
    public ResponseEntity<ApiResponse<List<ValidatorAssignmentResponseDto>>> getValidatorAssignments(
            @Parameter(description = "Validator UUID") @PathVariable String uuid,
            @RequestParam(required = false) AssignmentStatus status) {

        try {
            List<ValidatorAssignment> assignments;
            if (status != null) {
                assignments = validatorService.findAssignmentsByValidator(uuid)
                    .stream()
                    .filter(a -> a.getStatus() == status)
                    .collect(Collectors.toList());
            } else {
                assignments = validatorService.findAssignmentsByValidator(uuid);
            }

            List<ValidatorAssignmentResponseDto> responseList = assignments.stream()
                .map(ValidatorAssignmentResponseDto::fromEntity)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Assignments retrieved successfully", responseList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve assignments: " + e.getMessage()));
        }
    }

    @GetMapping("/{uuid}/pending-assignments")
    @Operation(summary = "Get pending assignments", description = "Get pending assignments for validator")
    @PreAuthorize("hasRole('MODERATOR') or @validatorService.findByUuid(#uuid).map(v -> v.user.uuid).orElse('') == authentication.principal.uuid")
    public ResponseEntity<ApiResponse<List<ValidatorAssignmentResponseDto>>> getPendingAssignments(
            @Parameter(description = "Validator UUID") @PathVariable String uuid) {

        try {
            List<ValidatorAssignment> assignments = validatorService.findPendingAssignments(uuid);

            List<ValidatorAssignmentResponseDto> responseList = assignments.stream()
                .map(ValidatorAssignmentResponseDto::fromEntity)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Pending assignments retrieved successfully", responseList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve pending assignments: " + e.getMessage()));
        }
    }

    // Validator analytics and statistics
    @GetMapping("/analytics/stats")
    @Operation(summary = "Get validator statistics", description = "Get overall validator statistics")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Object>> getValidatorStats() {
        
        try {
            long totalActive = validatorService.countActiveValidators();
            long totalAvailable = validatorService.countAvailableValidators();
            BigDecimal averageRating = validatorService.getAverageValidatorRating();
            double averageAssignmentTime = validatorService.getAverageAssignmentTime();
            
            Object stats = new Object() {
                public final long activeValidators = totalActive;
                public final long availableValidators = totalAvailable;
                public final BigDecimal averageValidatorRating = averageRating;
                public final double averageAssignmentTimeHours = averageAssignmentTime;
            };
            
            return ResponseEntity.ok(ApiResponse.success("Validator statistics retrieved successfully", stats));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validator statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/{uuid}/stats")
    @Operation(summary = "Get validator performance stats", description = "Get performance statistics for specific validator")
    @PreAuthorize("hasRole('MODERATOR') or @validatorService.findByUuid(#uuid).map(v -> v.user.uuid).orElse('') == authentication.principal.uuid")
    public ResponseEntity<ApiResponse<Object>> getValidatorPerformanceStats(
            @Parameter(description = "Validator UUID") @PathVariable String uuid) {

        try {
            BigDecimal rating = validatorService.getValidatorRating(uuid);
            int totalValidations = validatorService.getValidatorTotalValidations(uuid);
            int pendingValidations = validatorService.getValidatorPendingValidations(uuid);
            double averageResponseTime = validatorService.getValidatorAverageResponseTime(uuid);
            double accuracyScore = validatorService.getValidatorAccuracyScore(uuid);

            Object stats = new Object() {
                public final BigDecimal validatorRating = rating;
                public final int totalValidationsCompleted = totalValidations;
                public final int pendingValidationsCount = pendingValidations;
                public final double averageResponseTimeHours = averageResponseTime;
                public final double validationAccuracy = accuracyScore;
            };

            return ResponseEntity.ok(ApiResponse.success("Validator performance stats retrieved successfully", stats));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validator performance stats: " + e.getMessage()));
        }
    }

    @GetMapping("/recommendations/{evidenceUuid}")
    @Operation(summary = "Get validator recommendations", description = "Get recommended validators for evidence")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<ValidatorResponseDto>>> getValidatorRecommendations(
            @Parameter(description = "Evidence UUID") @PathVariable String evidenceUuid,
            @RequestParam(defaultValue = "5") int limit) {

        try {
            List<Validator> recommendations = validatorService.findBestValidatorsForEvidence(evidenceUuid, limit);

            List<ValidatorResponseDto> responseList = recommendations.stream()
                .map(ValidatorResponseDto::fromEntity)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Validator recommendations retrieved successfully", responseList));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validator recommendations: " + e.getMessage()));
        }
    }
}

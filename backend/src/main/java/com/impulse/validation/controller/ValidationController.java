package com.impulse.validation.controller;

import com.impulse.lean.application.dto.common.ApiResponse;
import com.impulse.lean.application.dto.common.PaginationResponse;
import com.impulse.lean.application.dto.validation.EvidenceValidationRequestDto;
import com.impulse.lean.application.service.interfaces.ValidationService;
import com.impulse.lean.domain.model.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Validation REST Controller
 * 
 * Complete validation operations for evidence review and approval
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/validations")
@Tag(name = "Validations", description = "Evidence validation operations")
public class ValidationController {

    @Autowired
    private ValidationService validationService;

    // Core validation operations
    @PostMapping
    @Operation(summary = "Create validation", description = "Create a new validation for evidence")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<EvidenceValidation>> createValidation(
            @Valid @RequestBody EvidenceValidationRequestDto request) {

        try {
            EvidenceValidation validation = validationService.createValidation(
                request.getEvidenceUuid(), 
                request.getValidatorUuid(), 
                request);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Validation created successfully", validation));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create validation: " + e.getMessage()));
        }
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get validation by UUID", description = "Retrieve a specific validation")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Validation found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Validation not found")
    })
    public ResponseEntity<ApiResponse<EvidenceValidation>> getValidation(
            @Parameter(description = "Validation UUID") @PathVariable String uuid) {

        Optional<EvidenceValidation> validationOpt = validationService.findByUuid(uuid);
        if (validationOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Validation found", validationOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Validation not found"));
        }
    }

    @PutMapping("/{uuid}/complete")
    @Operation(summary = "Complete validation", description = "Complete validation with score and feedback")
    @PreAuthorize("hasRole('VALIDATOR')")
    public ResponseEntity<ApiResponse<EvidenceValidation>> completeValidation(
            @Parameter(description = "Validation UUID") @PathVariable String uuid,
            @RequestParam BigDecimal score,
            @RequestParam(required = false) String feedback) {

        try {
            EvidenceValidation validation = validationService.completeValidation(uuid, score, feedback);

            return ResponseEntity.ok(ApiResponse.success("Validation completed successfully", validation));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to complete validation: " + e.getMessage()));
        }
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update validation", description = "Update validation details")
    @PreAuthorize("hasRole('VALIDATOR')")
    public ResponseEntity<ApiResponse<EvidenceValidation>> updateValidation(
            @Parameter(description = "Validation UUID") @PathVariable String uuid,
            @Valid @RequestBody EvidenceValidationRequestDto request) {

        try {
            EvidenceValidation validation = validationService.updateValidation(uuid, request);

            return ResponseEntity.ok(ApiResponse.success("Validation updated successfully", validation));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update validation: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete validation", description = "Delete a validation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteValidation(
            @Parameter(description = "Validation UUID") @PathVariable String uuid) {

        try {
            validationService.deleteValidation(uuid);

            return ResponseEntity.ok(ApiResponse.success("Validation deleted successfully", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete validation: " + e.getMessage()));
        }
    }

    // Validation queries
    @GetMapping
    @Operation(summary = "List validations", description = "Get paginated list of validations")
    public ResponseEntity<ApiResponse<PaginationResponse<EvidenceValidation>>> listValidations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "validatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<EvidenceValidation> validationPage;

            if (search != null && !search.trim().isEmpty()) {
                validationPage = validationService.searchValidations(search, pageable);
            } else {
                validationPage = validationService.findValidations(pageable);
            }

            PaginationResponse<EvidenceValidation> response = new PaginationResponse<>(
                validationPage.getContent(),
                validationPage.getNumber(),
                validationPage.getSize(),
                validationPage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success("Validations retrieved successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validations: " + e.getMessage()));
        }
    }

    @GetMapping("/evidence/{evidenceUuid}")
    @Operation(summary = "Get validations by evidence", description = "Get all validations for specific evidence")
    public ResponseEntity<ApiResponse<List<EvidenceValidation>>> getValidationsByEvidence(
            @Parameter(description = "Evidence UUID") @PathVariable String evidenceUuid) {

        try {
            List<EvidenceValidation> validations = validationService.findByEvidence(evidenceUuid);

            return ResponseEntity.ok(ApiResponse.success("Validations retrieved successfully", validations));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validations: " + e.getMessage()));
        }
    }

    @GetMapping("/validator/{validatorUuid}")
    @Operation(summary = "Get validations by validator", description = "Get all validations for specific validator")
    public ResponseEntity<ApiResponse<List<EvidenceValidation>>> getValidationsByValidator(
            @Parameter(description = "Validator UUID") @PathVariable String validatorUuid) {

        try {
            List<EvidenceValidation> validations = validationService.findByValidator(validatorUuid);

            return ResponseEntity.ok(ApiResponse.success("Validations retrieved successfully", validations));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validations: " + e.getMessage()));
        }
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending validations", description = "Get all pending validations")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<EvidenceValidation>>> getPendingValidations() {

        try {
            List<EvidenceValidation> validations = validationService.findPendingValidations();

            return ResponseEntity.ok(ApiResponse.success("Pending validations retrieved successfully", validations));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve pending validations: " + e.getMessage()));
        }
    }

    @GetMapping("/completed")
    @Operation(summary = "Get completed validations", description = "Get all completed validations")
    public ResponseEntity<ApiResponse<List<EvidenceValidation>>> getCompletedValidations() {

        try {
            List<EvidenceValidation> validations = validationService.findCompletedValidations();

            return ResponseEntity.ok(ApiResponse.success("Completed validations retrieved successfully", validations));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve completed validations: " + e.getMessage()));
        }
    }

    // Validation workflow
    @PostMapping("/assign")
    @Operation(summary = "Assign validation", description = "Assign a validator to evidence")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<EvidenceValidation>> assignValidation(
            @RequestParam String evidenceUuid,
            @RequestParam String validatorUuid,
            @RequestParam ValidationType type) {

        try {
            EvidenceValidation validation = validationService.assignValidation(evidenceUuid, validatorUuid, type);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Validation assigned successfully", validation));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to assign validation: " + e.getMessage()));
        }
    }

    @PutMapping("/{uuid}/reassign")
    @Operation(summary = "Reassign validation", description = "Reassign validation to different validator")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> reassignValidation(
            @Parameter(description = "Validation UUID") @PathVariable String uuid,
            @RequestParam String newValidatorUuid) {

        try {
            validationService.reassignValidation(uuid, newValidatorUuid);

            return ResponseEntity.ok(ApiResponse.success("Validation reassigned successfully", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to reassign validation: " + e.getMessage()));
        }
    }

    @PutMapping("/{uuid}/escalate")
    @Operation(summary = "Escalate validation", description = "Escalate validation for review")
    @PreAuthorize("hasRole('VALIDATOR')")
    public ResponseEntity<ApiResponse<Void>> escalateValidation(
            @Parameter(description = "Validation UUID") @PathVariable String uuid,
            @RequestParam String reason) {

        try {
            validationService.escalateValidation(uuid, reason);

            return ResponseEntity.ok(ApiResponse.success("Validation escalated successfully", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to escalate validation: " + e.getMessage()));
        }
    }

    // Automatic validation
    @PostMapping("/auto/{evidenceUuid}")
    @Operation(summary = "Perform automatic validation", description = "Run automatic validation on evidence")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<EvidenceValidation>> performAutomaticValidation(
            @Parameter(description = "Evidence UUID") @PathVariable String evidenceUuid) {

        try {
            EvidenceValidation validation = validationService.performAutomaticValidation(evidenceUuid);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Automatic validation completed", validation));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to perform automatic validation: " + e.getMessage()));
        }
    }

    @PostMapping("/auto/process")
    @Operation(summary = "Process automatic validations", description = "Process all pending automatic validations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EvidenceValidation>>> processAutomaticValidations() {

        try {
            List<EvidenceValidation> validations = validationService.processAutomaticValidations();

            return ResponseEntity.ok(ApiResponse.success("Automatic validations processed", validations));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to process automatic validations: " + e.getMessage()));
        }
    }

    // Validation analytics
    @GetMapping("/analytics/stats")
    @Operation(summary = "Get validation statistics", description = "Get overall validation statistics")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Object>> getValidationStats() {
        
        try {
            BigDecimal averageScore = validationService.getAverageValidationScore();
            long totalValidations = validationService.getValidationCount();
            
            Object stats = new Object() {
                public final BigDecimal averageValidationScore = averageScore;
                public final long totalValidations = totalValidations;
            };
            
            return ResponseEntity.ok(ApiResponse.success("Validation statistics retrieved successfully", stats));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validation statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics/validator/{validatorUuid}")
    @Operation(summary = "Get validator statistics", description = "Get validation statistics for specific validator")
    public ResponseEntity<ApiResponse<Object>> getValidatorStats(
            @Parameter(description = "Validator UUID") @PathVariable String validatorUuid) {

        try {
            BigDecimal averageScore = validationService.getAverageValidationScoreByValidator(validatorUuid);
            long totalValidations = validationService.getValidationCountByValidator(validatorUuid);
            double accuracy = validationService.getValidationAccuracy(validatorUuid);

            Object stats = new Object() {
                public final BigDecimal averageScore = averageScore;
                public final long totalValidations = totalValidations;
                public final double validationAccuracy = accuracy;
            };

            return ResponseEntity.ok(ApiResponse.success("Validator statistics retrieved successfully", stats));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve validator statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/recommendations/validators/{evidenceUuid}")
    @Operation(summary = "Get recommended validators", description = "Get recommended validators for evidence")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<User>>> getRecommendedValidators(
            @Parameter(description = "Evidence UUID") @PathVariable String evidenceUuid) {

        try {
            List<User> validators = validationService.getRecommendedValidators(evidenceUuid);

            return ResponseEntity.ok(ApiResponse.success("Recommended validators retrieved successfully", validators));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve recommended validators: " + e.getMessage()));
        }
    }

    // Quality control
    @GetMapping("/inconsistent")
    @Operation(summary = "Get inconsistent validations", description = "Get validations flagged as inconsistent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EvidenceValidation>>> getInconsistentValidations() {

        try {
            List<EvidenceValidation> validations = validationService.findInconsistentValidations();

            return ResponseEntity.ok(ApiResponse.success("Inconsistent validations retrieved successfully", validations));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve inconsistent validations: " + e.getMessage()));
        }
    }

    @PutMapping("/{uuid}/flag")
    @Operation(summary = "Flag validation for review", description = "Flag validation for quality review")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> flagValidationForReview(
            @Parameter(description = "Validation UUID") @PathVariable String uuid,
            @RequestParam String reason) {

        try {
            validationService.flagValidationForReview(uuid, reason);

            return ResponseEntity.ok(ApiResponse.success("Validation flagged for review", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to flag validation: " + e.getMessage()));
        }
    }
}

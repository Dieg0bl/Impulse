package com.impulse.evidence.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.domain.model.EvidenceValidation;
import com.impulse.lean.domain.model.ValidationType;
import com.impulse.lean.service.EvidenceValidationService;

/**
 * IMPULSE LEAN v1 - Evidence Validation REST Controller
 * 
 * Provides RESTful endpoints for evidence validation operations
 * Compatible with existing EvidenceValidation model
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/validations")
@CrossOrigin(origins = "*")
public class EvidenceValidationController {

    @Autowired
    private EvidenceValidationService validationService;

    /**
     * Create a new evidence validation
     */
    @PostMapping
    public ResponseEntity<EvidenceValidation> createValidation(
            @RequestParam Long evidenceId,
            @RequestParam(required = false) Long validatorId,
            @RequestParam ValidationType type,
            @RequestParam BigDecimal score,
            @RequestParam(required = false) String feedback) {
        
        try {
            EvidenceValidation validation = validationService.createValidation(
                evidenceId, validatorId, type, score, feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(validation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing validation
     */
    @PutMapping("/{id}")
    public ResponseEntity<EvidenceValidation> updateValidation(
            @PathVariable Long id,
            @RequestParam BigDecimal score,
            @RequestParam(required = false) String feedback,
            @RequestParam(required = false) BigDecimal confidenceLevel) {
        
        try {
            EvidenceValidation validation = validationService.updateValidation(
                id, score, feedback, confidenceLevel);
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get validation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvidenceValidation> getValidation(@PathVariable Long id) {
        Optional<EvidenceValidation> validation = validationService.getValidationById(id);
        return validation.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all validations with pagination
     */
    @GetMapping
    public ResponseEntity<Page<EvidenceValidation>> getValidations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EvidenceValidation> validations = validationService.getValidations(pageable);
        return ResponseEntity.ok(validations);
    }

    /**
     * Get validations by evidence ID
     */
    @GetMapping("/evidence/{evidenceId}")
    public ResponseEntity<List<EvidenceValidation>> getValidationsByEvidence(
            @PathVariable Long evidenceId) {
        
        List<EvidenceValidation> validations = validationService.getValidationsByEvidence(evidenceId);
        return ResponseEntity.ok(validations);
    }

    /**
     * Get validations by validator ID
     */
    @GetMapping("/validator/{validatorId}")
    public ResponseEntity<List<EvidenceValidation>> getValidationsByValidator(
            @PathVariable Long validatorId) {
        
        List<EvidenceValidation> validations = validationService.getValidationsByValidator(validatorId);
        return ResponseEntity.ok(validations);
    }

    /**
     * Get validations by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<EvidenceValidation>> getValidationsByType(
            @PathVariable ValidationType type) {
        
        List<EvidenceValidation> validations = validationService.getValidationsByType(type);
        return ResponseEntity.ok(validations);
    }

    /**
     * Delete a validation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteValidation(@PathVariable Long id) {
        boolean deleted = validationService.deleteValidation(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Assign validation to a validator
     */
    @PostMapping("/assign")
    public ResponseEntity<EvidenceValidation> assignValidation(
            @RequestParam Long evidenceId,
            @RequestParam Long validatorId) {
        
        try {
            EvidenceValidation validation = validationService.assignValidation(evidenceId, validatorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(validation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Perform automatic validation
     */
    @PostMapping("/{evidenceId}/auto-validate")
    public ResponseEntity<EvidenceValidation> performAutomaticValidation(
            @PathVariable Long evidenceId) {
        
        try {
            EvidenceValidation validation = validationService.performAutomaticValidation(evidenceId);
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get validation statistics for evidence
     */
    @GetMapping("/evidence/{evidenceId}/stats")
    public ResponseEntity<Map<String, Object>> getValidationStats(
            @PathVariable Long evidenceId) {
        
        Map<String, Object> stats = validationService.getValidationStats(evidenceId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get validator performance metrics
     */
    @GetMapping("/validator/{validatorId}/metrics")
    public ResponseEntity<Map<String, Object>> getValidatorMetrics(
            @PathVariable Long validatorId) {
        
        Map<String, Object> metrics = validationService.getValidatorMetrics(validatorId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get validations by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<EvidenceValidation>> getValidationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<EvidenceValidation> validations = validationService.getValidationsByDateRange(start, end);
        return ResponseEntity.ok(validations);
    }

    /**
     * Check if evidence has conflicting validations
     */
    @GetMapping("/evidence/{evidenceId}/conflicts")
    public ResponseEntity<Map<String, Boolean>> checkConflictingValidations(
            @PathVariable Long evidenceId) {
        
        boolean hasConflicts = validationService.hasConflictingValidations(evidenceId);
        return ResponseEntity.ok(Map.of("hasConflicts", hasConflicts));
    }

    /**
     * Get average score for evidence
     */
    @GetMapping("/evidence/{evidenceId}/average-score")
    public ResponseEntity<Map<String, BigDecimal>> getAverageScore(
            @PathVariable Long evidenceId) {
        
        BigDecimal averageScore = validationService.getAverageScore(evidenceId);
        return ResponseEntity.ok(Map.of("averageScore", averageScore));
    }

    /**
     * Get validation analytics
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getValidationAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        Map<String, Object> analytics = validationService.getValidationAnalytics(start, end);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Validation summary endpoint for dashboard
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getValidationSummary() {
        // Get recent validations summary
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime now = LocalDateTime.now();
        
        Map<String, Object> analytics = validationService.getValidationAnalytics(weekAgo, now);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "EvidenceValidationService",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}

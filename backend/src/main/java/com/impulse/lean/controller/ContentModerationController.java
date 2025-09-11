package com.impulse.lean.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.dto.ContentModerationRequestDto;
import com.impulse.lean.service.ContentModerationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * IMPULSE LEAN v1 - Content Moderation Controller
 * 
 * REST controller for content moderation operations
 * Provides endpoints for content review and safety management
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/moderation")
@Tag(name = "Content Moderation", description = "Content moderation and safety operations")
public class ContentModerationController {

    @Autowired
    private ContentModerationService contentModerationService;

    @PostMapping("/submit")
    @Operation(summary = "Submit content for moderation")
    public ResponseEntity<Map<String, Object>> submitForModeration(
            @Valid @RequestBody ContentModerationRequestDto request) {
        Map<String, Object> result = contentModerationService.submitForModeration(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{contentId}/status")
    @Operation(summary = "Get moderation status")
    public ResponseEntity<Map<String, Object>> getModerationStatus(
            @Parameter(description = "Content ID") @PathVariable String contentId) {
        Map<String, Object> status = contentModerationService.getModerationStatus(contentId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{contentId}/approve")
    @Operation(summary = "Approve content")
    public ResponseEntity<Map<String, Object>> approveContent(
            @Parameter(description = "Content ID") @PathVariable String contentId,
            @Parameter(description = "Moderator ID") @RequestParam String moderatorId,
            @Parameter(description = "Approval reason") @RequestParam(required = false) String reason) {
        Map<String, Object> result = contentModerationService.approveContent(contentId, moderatorId, reason);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{contentId}/reject")
    @Operation(summary = "Reject content")
    public ResponseEntity<Map<String, Object>> rejectContent(
            @Parameter(description = "Content ID") @PathVariable String contentId,
            @Parameter(description = "Moderator ID") @RequestParam String moderatorId,
            @Parameter(description = "Rejection reason") @RequestParam String reason) {
        Map<String, Object> result = contentModerationService.rejectContent(contentId, moderatorId, reason);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{contentId}/flag")
    @Operation(summary = "Flag content for review")
    public ResponseEntity<Map<String, Object>> flagContent(
            @Parameter(description = "Content ID") @PathVariable String contentId,
            @Parameter(description = "Flag type") @RequestParam String flagType,
            @Parameter(description = "Flag reason") @RequestParam String reason,
            @Parameter(description = "Reporter ID") @RequestParam String reporterId) {
        Map<String, Object> result = contentModerationService.flagContent(contentId, flagType, reason, reporterId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/queue")
    @Operation(summary = "Get pending moderation queue")
    public ResponseEntity<List<Map<String, Object>>> getPendingModerationQueue(
            @Parameter(description = "Moderator ID") @RequestParam(required = false) String moderatorId,
            @Parameter(description = "Queue limit") @RequestParam(defaultValue = "50") int limit) {
        List<Map<String, Object>> queue = contentModerationService.getPendingModerationQueue(moderatorId, limit);
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/{contentId}/history")
    @Operation(summary = "Get moderation history")
    public ResponseEntity<List<Map<String, Object>>> getModerationHistory(
            @Parameter(description = "Content ID") @PathVariable String contentId) {
        List<Map<String, Object>> history = contentModerationService.getModerationHistory(contentId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{contentId}/auto-moderate")
    @Operation(summary = "Auto-moderate content")
    public ResponseEntity<Map<String, Object>> autoModerateContent(
            @Parameter(description = "Content ID") @PathVariable String contentId,
            @Parameter(description = "Content text") @RequestParam String content,
            @Parameter(description = "Content type") @RequestParam String contentType) {
        Map<String, Object> result = contentModerationService.autoModerateContent(contentId, content, contentType);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get moderation statistics")
    public ResponseEntity<Map<String, Object>> getModerationStats(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Object> stats = contentModerationService.getModerationStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/rules")
    @Operation(summary = "Update moderation rules")
    public ResponseEntity<Map<String, Object>> updateModerationRules(
            @RequestBody Map<String, Object> rules) {
        Map<String, Object> result = contentModerationService.updateModerationRules(rules);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/rules")
    @Operation(summary = "Get active moderation rules")
    public ResponseEntity<Map<String, Object>> getModerationRules() {
        Map<String, Object> rules = contentModerationService.getModerationRules();
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/{contentId}/escalate")
    @Operation(summary = "Escalate content to senior moderator")
    public ResponseEntity<Map<String, Object>> escalateContent(
            @Parameter(description = "Content ID") @PathVariable String contentId,
            @Parameter(description = "Moderator ID") @RequestParam String moderatorId,
            @Parameter(description = "Escalation reason") @RequestParam String reason) {
        Map<String, Object> result = contentModerationService.escalateContent(contentId, moderatorId, reason);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/moderator/{moderatorId}/metrics")
    @Operation(summary = "Get moderator performance metrics")
    public ResponseEntity<Map<String, Object>> getModeratorMetrics(
            @Parameter(description = "Moderator ID") @PathVariable String moderatorId,
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Object> metrics = contentModerationService.getModeratorMetrics(moderatorId, startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/bulk-moderate")
    @Operation(summary = "Bulk moderate content")
    public ResponseEntity<Map<String, Object>> bulkModerateContent(
            @Parameter(description = "Content IDs") @RequestParam List<String> contentIds,
            @Parameter(description = "Moderation action") @RequestParam String action,
            @Parameter(description = "Moderator ID") @RequestParam String moderatorId) {
        Map<String, Object> result = contentModerationService.bulkModerateContent(contentIds, action, moderatorId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/safety-score")
    @Operation(summary = "Get content safety score")
    public ResponseEntity<Map<String, Object>> getContentSafetyScore(
            @Parameter(description = "Content text") @RequestParam String content,
            @Parameter(description = "Content type") @RequestParam String contentType) {
        Map<String, Object> result = contentModerationService.getContentSafetyScore(content, contentType);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/report-error")
    @Operation(summary = "Report moderation error")
    public ResponseEntity<Map<String, Object>> reportModerationError(
            @Parameter(description = "Content ID") @RequestParam String contentId,
            @Parameter(description = "Error type") @RequestParam String errorType,
            @Parameter(description = "Error description") @RequestParam String description) {
        Map<String, Object> result = contentModerationService.reportModerationError(contentId, errorType, description);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trending-issues")
    @Operation(summary = "Get trending moderation issues")
    public ResponseEntity<List<Map<String, Object>>> getTrendingModerationIssues(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Map<String, Object>> issues = contentModerationService.getTrendingModerationIssues(startDate, endDate);
        return ResponseEntity.ok(issues);
    }

    @PostMapping("/reports")
    @Operation(summary = "Create moderation report")
    public ResponseEntity<Map<String, Object>> createModerationReport(
            @Parameter(description = "Report type") @RequestParam String reportType,
            @RequestBody Map<String, Object> parameters) {
        Map<String, Object> result = contentModerationService.createModerationReport(reportType, parameters);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/assess-risk")
    @Operation(summary = "Assess content risk")
    public ResponseEntity<Map<String, Object>> assessContentRisk(
            @Parameter(description = "Content text") @RequestParam String content,
            @Parameter(description = "Content type") @RequestParam String contentType,
            @RequestBody(required = false) Map<String, Object> context) {
        Map<String, Object> result = contentModerationService.assessContentRisk(content, contentType, context);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/toggle-auto-moderation")
    @Operation(summary = "Toggle auto-moderation")
    public ResponseEntity<Map<String, Object>> toggleAutoModeration(
            @Parameter(description = "Content type") @RequestParam String contentType,
            @Parameter(description = "Enable auto-moderation") @RequestParam boolean enabled) {
        Map<String, Object> result = contentModerationService.toggleAutoModeration(contentType, enabled);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/queue/priority/{priority}")
    @Operation(summary = "Get moderation queue by priority")
    public ResponseEntity<List<Map<String, Object>>> getModerationQueueByPriority(
            @Parameter(description = "Priority level") @PathVariable String priority,
            @Parameter(description = "Queue limit") @RequestParam(defaultValue = "50") int limit) {
        List<Map<String, Object>> queue = contentModerationService.getModerationQueueByPriority(priority, limit);
        return ResponseEntity.ok(queue);
    }

    @PutMapping("/{contentId}/sensitivity")
    @Operation(summary = "Update content sensitivity level")
    public ResponseEntity<Map<String, Object>> updateContentSensitivity(
            @Parameter(description = "Content ID") @PathVariable String contentId,
            @Parameter(description = "Sensitivity level") @RequestParam String sensitivityLevel) {
        Map<String, Object> result = contentModerationService.updateContentSensitivity(contentId, sensitivityLevel);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/appeals")
    @Operation(summary = "Get moderation appeals")
    public ResponseEntity<List<Map<String, Object>>> getModerationAppeals(
            @Parameter(description = "Appeal status") @RequestParam(defaultValue = "PENDING") String status,
            @Parameter(description = "Appeals limit") @RequestParam(defaultValue = "50") int limit) {
        List<Map<String, Object>> appeals = contentModerationService.getModerationAppeals(status, limit);
        return ResponseEntity.ok(appeals);
    }

    @PostMapping("/appeals/{appealId}/process")
    @Operation(summary = "Process moderation appeal")
    public ResponseEntity<Map<String, Object>> processModerationAppeal(
            @Parameter(description = "Appeal ID") @PathVariable String appealId,
            @Parameter(description = "Appeal decision") @RequestParam String decision,
            @Parameter(description = "Moderator ID") @RequestParam String moderatorId,
            @Parameter(description = "Decision reason") @RequestParam String reason) {
        Map<String, Object> result = contentModerationService.processModerationAppeal(appealId, decision, moderatorId, reason);
        return ResponseEntity.ok(result);
    }
}

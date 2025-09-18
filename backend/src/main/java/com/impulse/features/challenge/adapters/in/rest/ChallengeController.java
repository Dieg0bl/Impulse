package com.impulse.features.challenge.adapters.in.rest;

import com.impulse.features.challenge.adapters.in.rest.dto.CreateChallengeRequest;
import com.impulse.features.challenge.adapters.in.rest.dto.ChallengeApiResponse;
import com.impulse.features.challenge.adapters.in.rest.dto.OpenChallengeRequest;
import com.impulse.features.challenge.adapters.in.rest.mapper.ChallengeApiMapper;
import com.impulse.features.challenge.application.port.in.CreateChallengePort;
import com.impulse.features.challenge.application.port.in.OpenChallengePort;
import com.impulse.shared.utils.IdempotencyKey;
import com.impulse.shared.utils.CorrelationId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;

/**
 * REST Controller: ChallengeController
 * Handles HTTP requests for challenge operations
 * IMPULSE v1.0 specification compliant
 */
@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    private final CreateChallengePort createChallengePort;
    private final OpenChallengePort openChallengePort;
    private final ChallengeApiMapper mapper;

    public ChallengeController(CreateChallengePort createChallengePort,
                              OpenChallengePort openChallengePort,
                              ChallengeApiMapper mapper) {
        this.createChallengePort = createChallengePort;
        this.openChallengePort = openChallengePort;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChallengeApiResponse> createChallenge(
            @Valid @RequestBody CreateChallengeRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            Authentication authentication) {

        // Set correlation ID for tracing
        CorrelationId.set(correlationId);

        try {
            // Map request to command
            var command = mapper.toCreateCommand(request, getUserId(authentication));

            // Execute use case
            var response = createChallengePort.execute(
                command,
                idempotencyKey != null ? IdempotencyKey.of(idempotencyKey) : null
            );

            // Map to API response
            var apiResponse = mapper.toApiResponse(response);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

        } finally {
            CorrelationId.clear();
        }
    }

    @PutMapping("/{challengeId}/open")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChallengeApiResponse> openChallenge(
            @PathVariable String challengeId,
            @Valid @RequestBody OpenChallengeRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            Authentication authentication) {

        // Set correlation ID for tracing
        CorrelationId.set(correlationId);

        try {
            // Map request to command
            var command = mapper.toOpenCommand(challengeId, request, getUserId(authentication));

            // Execute use case
            var response = openChallengePort.execute(
                command,
                idempotencyKey != null ? IdempotencyKey.of(idempotencyKey) : null
            );

            // Map to API response
            var apiResponse = mapper.toApiResponse(response);

            return ResponseEntity.ok(apiResponse);

        } finally {
            CorrelationId.clear();
        }
    }

    private Long getUserId(Authentication authentication) {
        // Extract user ID from authentication - implementation depends on security setup
        // For now, assume principal contains user ID
        Principal principal = authentication;
        try {
            return Long.parseLong(principal.getName());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid user ID in authentication: " + principal.getName());
        }
    }
}

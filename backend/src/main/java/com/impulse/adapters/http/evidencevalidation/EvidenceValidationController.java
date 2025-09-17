package com.impulse.adapters.http.evidencevalidation;

import com.impulse.application.evidencevalidation.dto.CreateEvidenceValidationCommand;
import com.impulse.application.evidencevalidation.dto.EvidenceValidationResponse;
import com.impulse.application.evidencevalidation.usecase.CreateEvidenceValidationUseCase;
import com.impulse.application.evidencevalidation.usecase.GetEvidenceValidationByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evidence-validations")
@RequiredArgsConstructor
public class EvidenceValidationController {
    private final CreateEvidenceValidationUseCase createEvidenceValidationUseCase;
    private final GetEvidenceValidationByIdUseCase getEvidenceValidationByIdUseCase;
    @PostMapping
    public ResponseEntity<EvidenceValidationResponse> createEvidenceValidation(@RequestBody CreateEvidenceValidationCommand command) {
        EvidenceValidationResponse response = createEvidenceValidationUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<EvidenceValidationResponse> getEvidenceValidationById(@PathVariable String id) {
        EvidenceValidationResponse response = getEvidenceValidationByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

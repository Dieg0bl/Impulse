package com.impulse.adapters.http.validatorassignment;

import com.impulse.application.validatorassignment.dto.CreateValidatorAssignmentCommand;
import com.impulse.application.validatorassignment.dto.ValidatorAssignmentResponse;
import com.impulse.application.validatorassignment.usecase.CreateValidatorAssignmentUseCase;
import com.impulse.application.validatorassignment.usecase.GetValidatorAssignmentByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validator-assignments")
@RequiredArgsConstructor
public class ValidatorAssignmentController {
    private final CreateValidatorAssignmentUseCase createValidatorAssignmentUseCase;
    private final GetValidatorAssignmentByIdUseCase getValidatorAssignmentByIdUseCase;
    @PostMapping
    public ResponseEntity<ValidatorAssignmentResponse> createValidatorAssignment(@RequestBody CreateValidatorAssignmentCommand command) {
        ValidatorAssignmentResponse response = createValidatorAssignmentUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ValidatorAssignmentResponse> getValidatorAssignmentById(@PathVariable String id) {
        ValidatorAssignmentResponse response = getValidatorAssignmentByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

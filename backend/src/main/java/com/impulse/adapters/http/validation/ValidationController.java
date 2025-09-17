package com.impulse.adapters.http.validation;

import com.impulse.application.validation.dto.ValidationResponse;
import com.impulse.application.validation.dto.CreateValidationCommand;
import com.impulse.application.validation.usecase.CreateValidationUseCase;
import com.impulse.application.validation.usecase.GetValidationByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validations")
public class ValidationController {
    private final CreateValidationUseCase createValidationUseCase;
    private final GetValidationByIdUseCase getValidationByIdUseCase;

    public ValidationController(CreateValidationUseCase createValidationUseCase, GetValidationByIdUseCase getValidationByIdUseCase) {
        this.createValidationUseCase = createValidationUseCase;
        this.getValidationByIdUseCase = getValidationByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<ValidationResponse> createValidation(@RequestBody CreateValidationCommand command) {
        ValidationResponse response = createValidationUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValidationResponse> getValidationById(@PathVariable Long id) {
        ValidationResponse response = getValidationByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

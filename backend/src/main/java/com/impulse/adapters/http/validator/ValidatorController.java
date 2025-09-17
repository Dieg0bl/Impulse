package com.impulse.adapters.http.validator;

import com.impulse.application.validator.dto.CreateValidatorCommand;
import com.impulse.application.validator.dto.ValidatorResponse;
import com.impulse.application.validator.usecase.CreateValidatorUseCase;
import com.impulse.application.validator.usecase.GetValidatorByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validators")
@RequiredArgsConstructor
public class ValidatorController {
    private final CreateValidatorUseCase createValidatorUseCase;
    private final GetValidatorByIdUseCase getValidatorByIdUseCase;
    @PostMapping
    public ResponseEntity<ValidatorResponse> createValidator(@RequestBody CreateValidatorCommand command) {
        ValidatorResponse response = createValidatorUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ValidatorResponse> getValidatorById(@PathVariable String id) {
        ValidatorResponse response = getValidatorByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

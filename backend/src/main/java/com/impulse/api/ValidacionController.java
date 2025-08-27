
package com.impulse.api;

import com.impulse.domain.tutor.Validation;
import com.impulse.application.ports.ValidationPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validaciones")
public class ValidacionController {

    private static final String NOT_FOUND_MSG = "Validación no encontrada";

    private final ValidationPort validationRepository;

    public ValidacionController(ValidationPort validationRepository) {
        this.validationRepository = validationRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<Validation>> getValidaciones() {
        List<Validation> validaciones = validationRepository.findAll();
        return ResponseEntity.ok(validaciones);
    }

    @PostMapping("")
    public ResponseEntity<Object> crearValidacion(@RequestBody Validation request) {
        try {
            Validation creada = validationRepository.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getValidacion(@PathVariable Long id) {
        return validationRepository.findById(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_MSG));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarValidacion(@PathVariable Long id, @RequestBody Validation request) {
        return validationRepository.findById(id)
                .<ResponseEntity<Object>>map(v -> {
                    Validation actualizado = updateValidationFields(v, request);
                    validationRepository.save(actualizado);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_MSG));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarValidacion(@PathVariable Long id) {
        if (!validationRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_MSG);
        }
        validationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Validation updateValidationFields(Validation original, Validation update) {
        // Como los campos son privados y no hay setters, creamos una nueva instancia si hay cambios
        String status = update.getStatus() != null ? update.getStatus() : original.getStatus();
        String feedback = update.getFeedback() != null ? update.getFeedback() : original.getFeedback();
        return new Validation(original.getRetoId(), original.getValidatorId(), status, feedback);
    }
}

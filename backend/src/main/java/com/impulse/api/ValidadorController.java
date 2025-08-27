package com.impulse.api;

import com.impulse.application.usuario.ValidadorService;
import com.impulse.common.exceptions.ConflictException;
import com.impulse.common.exceptions.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validadores")
@Validated
public class ValidadorController {

    private final ValidadorService validadorService;

    @Autowired
    public ValidadorController(ValidadorService validadorService) {
        this.validadorService = validadorService;
    }

    @PostMapping(
        value = "/invitar",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> invitarValidador(@Valid @RequestBody ValidadorInvitacionRequest request) {
        try {
            validadorService.invitarValidador(request.getEmail());
            return ResponseEntity.ok("Invitación enviada a " + request.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping(
        value = "/{id}",
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> eliminarValidador(@PathVariable Long id) {
        try {
            validadorService.eliminarValidador(id);
            return ResponseEntity.ok("Validador eliminado: " + id);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public static class ValidadorInvitacionRequest {
        @NotBlank(message = "El email es obligatorio.")
        @Email(message = "El email no tiene un formato válido.")
        private String email;

        public ValidadorInvitacionRequest() {
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }
}

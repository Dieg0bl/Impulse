package com.impulse.api;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.application.ValidationService;
import com.impulse.application.reto.RetoService;
import com.impulse.domain.reto.PermisoDTO;
import com.impulse.domain.reto.RetoDTO;
import com.impulse.domain.tutor.Validation;

@RestController("retoControllerApi")
@RequestMapping("/api/retos")
public class RetoController {
    private final RetoService retoService;
    private final ValidationService validationService;

    public RetoController(RetoService retoService, ValidationService validationService) {
        this.retoService = retoService;
        this.validationService = validationService;
    }

    @GetMapping
    public ResponseEntity<List<RetoDTO>> getRetos() {
        List<RetoDTO> retos = retoService.listarRetos();
        return ResponseEntity.ok(retos);
    }

    @PostMapping
    public ResponseEntity<?> crearReto(@RequestBody RetoDTO request) {
        try {
            RetoDTO creado = retoService.crearReto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReto(@PathVariable Long id) {
        try {
            RetoDTO reto = retoService.obtenerReto(id);
            return ResponseEntity.ok(reto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReto(@PathVariable Long id, @RequestBody RetoDTO request) {
        try {
            RetoDTO actualizado = retoService.actualizarReto(id, request);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReto(@PathVariable Long id) {
        try {
            retoService.eliminarReto(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/permisos")
    public ResponseEntity<PermisoDTO> getPermisos(@PathVariable Long id, Principal principal) {
        RetoDTO reto = retoService.obtenerReto(id);
        Long idCreador = reto.idCreador();
        boolean puedeEditar = idCreador != null && principal != null && principal.getName() != null
                && idCreador.toString().equals(principal.getName());
        return ResponseEntity.ok(new PermisoDTO("editar", puedeEditar));
    }

    // No se implementa updatePermisos porque los permisos se calculan dinámicamente según el usuario autenticado y el creador del reto.
    // Si en el futuro se requiere lógica de delegación o roles, se puede ampliar aquí.

    @PostMapping("/{id}/validar")
    public ResponseEntity<?> validateReto(@PathVariable Long id, @RequestBody Validation request, Principal principal) {
        try {
            // El validador es el usuario autenticado
            Long validatorId = principal != null ? Long.valueOf(principal.getName()) : null;
            if (validatorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            Validation validacion = validationService.submit(id, validatorId, request.getStatus(), request.getFeedback());
            return ResponseEntity.status(HttpStatus.CREATED).body(validacion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

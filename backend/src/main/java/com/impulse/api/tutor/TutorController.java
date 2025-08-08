package com.impulse.api.tutor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.application.tutor.TutorService;
import com.impulse.domain.tutor.TutorDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de tutores.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController
@RequestMapping("/api/tutor")
@Validated
public class TutorController {
    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @Operation(summary = "Obtener tutor por ID", description = "Devuelve el tutor correspondiente al ID proporcionado.")
    @ApiResponse(responseCode = "200", description = "Tutor encontrado")
    @ApiResponse(responseCode = "404", description = "Tutor no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<TutorDTO> obtenerTutor(@PathVariable Long id) {
        TutorDTO tutor = tutorService.obtenerTutor(id);
        return ResponseEntity.ok(tutor);
    }

    @Operation(summary = "Crear un nuevo tutor", description = "Crea un tutor y devuelve el DTO resultante")
    @ApiResponse(responseCode = "201", description = "Tutor creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<TutorDTO> crearTutor(@Valid @RequestBody TutorDTO dto) {
        TutorDTO creado = tutorService.crearTutor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar tutor", description = "Actualiza los datos de un tutor existente")
    @ApiResponse(responseCode = "200", description = "Tutor actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Tutor no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<TutorDTO> actualizarTutor(@PathVariable Long id, @Valid @RequestBody TutorDTO dto) {
        TutorDTO actualizado = tutorService.actualizarTutor(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar tutor", description = "Elimina un tutor por ID (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Tutor eliminado")
    @ApiResponse(responseCode = "404", description = "Tutor no encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTutor(@PathVariable Long id) {
        tutorService.eliminarTutor(id);
        return ResponseEntity.noContent().build();
    }
}

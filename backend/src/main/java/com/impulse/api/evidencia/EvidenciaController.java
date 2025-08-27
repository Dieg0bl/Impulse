package com.impulse.api.evidencia;

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

import com.impulse.application.evidencia.EvidenciaService;
import com.impulse.domain.evidencia.EvidenciaDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de evidencias.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController("evidenciaControllerSub")
@RequestMapping("/api/evidencia")
@Validated
public class EvidenciaController {
    private final EvidenciaService evidenciaService;

    public EvidenciaController(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @Operation(summary = "Obtener evidencia por ID", description = "Devuelve la evidencia correspondiente al ID proporcionado.")
    @ApiResponse(responseCode = "200", description = "Evidencia encontrada")
    @ApiResponse(responseCode = "404", description = "Evidencia no encontrada")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<EvidenciaDTO> obtenerEvidencia(@PathVariable Long id) {
        EvidenciaDTO evidencia = evidenciaService.obtenerEvidencia(id);
        return ResponseEntity.ok(evidencia);
    }

    @Operation(summary = "Crear una nueva evidencia", description = "Crea una evidencia y devuelve el DTO resultante")
    @ApiResponse(responseCode = "201", description = "Evidencia creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<EvidenciaDTO> crearEvidencia(@Valid @RequestBody EvidenciaDTO dto) {
        EvidenciaDTO creada = evidenciaService.crearEvidencia(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Actualizar evidencia", description = "Actualiza los datos de una evidencia existente")
    @ApiResponse(responseCode = "200", description = "Evidencia actualizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Evidencia no encontrada")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<EvidenciaDTO> actualizarEvidencia(@PathVariable Long id, @Valid @RequestBody EvidenciaDTO dto) {
        EvidenciaDTO actualizada = evidenciaService.actualizarEvidencia(id, dto);
        return ResponseEntity.ok(actualizada);
    }

    @Operation(summary = "Eliminar evidencia", description = "Elimina una evidencia por ID (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Evidencia eliminada")
    @ApiResponse(responseCode = "404", description = "Evidencia no encontrada")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvidencia(@PathVariable Long id) {
        evidenciaService.eliminarEvidencia(id);
        return ResponseEntity.noContent().build();
    }
}

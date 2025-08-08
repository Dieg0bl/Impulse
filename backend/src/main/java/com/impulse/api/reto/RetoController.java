package com.impulse.api.reto;

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

import com.impulse.application.reto.RetoService;
import com.impulse.domain.reto.RetoDTO;
import com.impulse.retention.RetentionService;
import com.impulse.analytics.EventTracker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de retos.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController
@RequestMapping("/api/reto")
@Validated
public class RetoController {
    private final RetoService retoService; private final RetentionService retention; private final EventTracker tracker;

    public RetoController(RetoService retoService, RetentionService retention, EventTracker tracker) {
        this.retoService = retoService; this.retention = retention; this.tracker = tracker;
    }

    @Operation(summary = "Obtener reto por ID", description = "Devuelve el reto correspondiente al ID proporcionado.")
    @ApiResponse(responseCode = "200", description = "Reto encontrado")
    @ApiResponse(responseCode = "404", description = "Reto no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<RetoDTO> obtenerReto(@PathVariable Long id) {
        RetoDTO reto = retoService.obtenerReto(id);
        return ResponseEntity.ok(reto);
    }

    @Operation(summary = "Crear un nuevo reto", description = "Crea un reto y devuelve el DTO resultante")
    @ApiResponse(responseCode = "201", description = "Reto creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<RetoDTO> crearReto(@Valid @RequestBody RetoDTO dto) {
    RetoDTO creado = retoService.crearReto(dto);
    try { if(creado.getUsuarioId()!=null) { retention.recordActivity(creado.getUsuarioId()); tracker.track(creado.getUsuarioId(), "reto_created", java.util.Map.of("reto_id", creado.getId()), null, "api"); } } catch (Exception ignore) {}
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar reto", description = "Actualiza los datos de un reto existente")
    @ApiResponse(responseCode = "200", description = "Reto actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Reto no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<RetoDTO> actualizarReto(@PathVariable Long id, @Valid @RequestBody RetoDTO dto) {
    RetoDTO actualizado = retoService.actualizarReto(id, dto);
    try { if(actualizado.getUsuarioId()!=null){ retention.recordActivity(actualizado.getUsuarioId()); if("COMPLETADO".equalsIgnoreCase(actualizado.getEstado())) tracker.track(actualizado.getUsuarioId(), "reto_completed", java.util.Map.of("reto_id", actualizado.getId()), null, "api"); } } catch (Exception ignore) {}
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar reto", description = "Elimina un reto por ID (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Reto eliminado")
    @ApiResponse(responseCode = "404", description = "Reto no encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReto(@PathVariable Long id) {
        retoService.eliminarReto(id);
        return ResponseEntity.noContent().build();
    }
}

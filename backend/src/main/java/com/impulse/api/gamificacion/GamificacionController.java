package com.impulse.api.gamificacion;

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

import com.impulse.application.gamificacion.GamificacionService;
import com.impulse.domain.gamificacion.GamificacionDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de gamificación.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController
@RequestMapping("/api/gamificacion")
@Validated
public class GamificacionController {
    private final GamificacionService gamificacionService;

    public GamificacionController(GamificacionService gamificacionService) {
        this.gamificacionService = gamificacionService;
    }

    @Operation(summary = "Obtener gamificación por ID", description = "Devuelve la gamificación correspondiente al ID proporcionado.")
    @ApiResponse(responseCode = "200", description = "Gamificación encontrada")
    @ApiResponse(responseCode = "404", description = "Gamificación no encontrada")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<GamificacionDTO> obtenerGamificacion(@PathVariable Long id) {
        GamificacionDTO gamificacion = gamificacionService.obtenerGamificacion(id);
        return ResponseEntity.ok(gamificacion);
    }

    @Operation(summary = "Crear una nueva gamificación", description = "Crea una gamificación y devuelve el DTO resultante")
    @ApiResponse(responseCode = "201", description = "Gamificación creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<GamificacionDTO> crearGamificacion(@Valid @RequestBody GamificacionDTO dto) {
        GamificacionDTO creada = gamificacionService.crearGamificacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Actualizar gamificación", description = "Actualiza los datos de una gamificación existente")
    @ApiResponse(responseCode = "200", description = "Gamificación actualizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Gamificación no encontrada")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<GamificacionDTO> actualizarGamificacion(@PathVariable Long id, @Valid @RequestBody GamificacionDTO dto) {
        GamificacionDTO actualizada = gamificacionService.actualizarGamificacion(id, dto);
        return ResponseEntity.ok(actualizada);
    }

    @Operation(summary = "Eliminar gamificación", description = "Elimina una gamificación por ID (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Gamificación eliminada")
    @ApiResponse(responseCode = "404", description = "Gamificación no encontrada")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGamificacion(@PathVariable Long id) {
        gamificacionService.eliminarGamificacion(id);
        return ResponseEntity.noContent().build();
    }
}

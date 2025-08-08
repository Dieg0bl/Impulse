package com.impulse.api.notificacion;

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

import com.impulse.application.notificacion.NotificacionService;
import com.impulse.domain.notificacion.NotificacionDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de notificaciones.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController
@RequestMapping("/api/notificacion")
@Validated
public class NotificacionController {
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @Operation(summary = "Obtener notificación por ID", description = "Devuelve la notificación correspondiente al ID proporcionado.")
    @ApiResponse(responseCode = "200", description = "Notificación encontrada")
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtenerNotificacion(@PathVariable Long id) {
        NotificacionDTO notificacion = notificacionService.obtenerNotificacion(id);
        return ResponseEntity.ok(notificacion);
    }

    @Operation(summary = "Crear una nueva notificación", description = "Crea una notificación y devuelve el DTO resultante")
    @ApiResponse(responseCode = "201", description = "Notificación creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<NotificacionDTO> crearNotificacion(@Valid @RequestBody NotificacionDTO dto) {
        NotificacionDTO creada = notificacionService.crearNotificacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Actualizar notificación", description = "Actualiza los datos de una notificación existente")
    @ApiResponse(responseCode = "200", description = "Notificación actualizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<NotificacionDTO> actualizarNotificacion(@PathVariable Long id, @Valid @RequestBody NotificacionDTO dto) {
        NotificacionDTO actualizada = notificacionService.actualizarNotificacion(id, dto);
        return ResponseEntity.ok(actualizada);
    }

    @Operation(summary = "Eliminar notificación", description = "Elimina una notificación por ID (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Notificación eliminada")
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}

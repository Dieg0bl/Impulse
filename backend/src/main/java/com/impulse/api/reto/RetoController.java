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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de retos.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController("retoControllerModule")
@RequestMapping("/api/reto")
@Validated
public class RetoController {
    private final RetoService retoService;

    public RetoController(RetoService retoService) {
        this.retoService = retoService;
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

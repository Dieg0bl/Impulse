package com.impulse.api.auditoria;


import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.domain.auditoria.AuditoriaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de auditoría.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController
@RequestMapping("/api/auditoria")
@Validated
public class AuditoriaController {
    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Operation(summary = "Obtener auditoría por ID", description = "Devuelve la auditoría correspondiente al ID proporcionado.")
    @ApiResponse(responseCode = "200", description = "Auditoría encontrada")
    @ApiResponse(responseCode = "404", description = "Auditoría no encontrada")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaDTO> obtenerAuditoria(@PathVariable Long id) {
        AuditoriaDTO auditoria = auditoriaService.obtenerAuditoria(id);
        return ResponseEntity.ok(auditoria);
    }

    @Operation(summary = "Crear una nueva auditoría", description = "Crea una auditoría y devuelve el DTO resultante")
    @ApiResponse(responseCode = "201", description = "Auditoría creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AuditoriaDTO> crearAuditoria(@Valid @RequestBody AuditoriaDTO dto) {
        AuditoriaDTO creada = auditoriaService.crearAuditoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Eliminar auditoría", description = "Elimina una auditoría por ID (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Auditoría eliminada")
    @ApiResponse(responseCode = "404", description = "Auditoría no encontrada")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAuditoria(@PathVariable Long id) {
        auditoriaService.eliminarAuditoria(id);
        return ResponseEntity.noContent().build();
    }
}

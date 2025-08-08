package com.impulse.api.monetizacion;

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

import com.impulse.application.monetizacion.PagoService;
import com.impulse.domain.monetizacion.PagoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de pagos (monetización).
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController
@RequestMapping("/api/monetizacion")
@Validated
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(summary = "Obtener pago por ID", description = "Devuelve el pago correspondiente al ID proporcionado.")
    @ApiResponse(responseCode = "200", description = "Pago encontrado")
    @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPago(@PathVariable Long id) {
        PagoDTO pago = pagoService.obtenerPago(id);
        return ResponseEntity.ok(pago);
    }

    @Operation(summary = "Crear un nuevo pago", description = "Crea un pago y devuelve el DTO resultante")
    @ApiResponse(responseCode = "201", description = "Pago creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<PagoDTO> crearPago(@Valid @RequestBody PagoDTO dto) {
        PagoDTO creado = pagoService.crearPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar pago", description = "Actualiza los datos de un pago existente")
    @ApiResponse(responseCode = "200", description = "Pago actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<PagoDTO> actualizarPago(@PathVariable Long id, @Valid @RequestBody PagoDTO dto) {
        PagoDTO actualizado = pagoService.actualizarPago(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar pago", description = "Elimina un pago por ID (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Pago eliminado")
    @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }
}

package com.impulse.api.usuario;

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

import com.impulse.application.usuario.UsuarioService;
import com.impulse.domain.usuario.UsuarioDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de usuarios.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@RestController
@RequestMapping("/api/usuario")
@Validated
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Obtener usuario por ID", description = "Devuelve el usuario correspondiente al ID proporcionado.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un usuario y devuelve el DTO resultante")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad")
    @PreAuthorize("permitAll()")
    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO creado = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO actualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por ID (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}

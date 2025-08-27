
package com.impulse.api;

import com.impulse.application.usuario.UsuarioService;
import com.impulse.domain.usuario.UsuarioDTO;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioPerfilController {

    private final UsuarioService usuarioService;

    public UsuarioPerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // En un entorno real, el id del usuario se obtiene del principal autenticado
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> getPerfil(@RequestHeader("X-User-Id") Long userId) {
        UsuarioDTO usuario = usuarioService.buscarPorId(userId);
        if (usuario == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioDTO> updatePerfil(@RequestHeader("X-User-Id") Long userId, @RequestBody UsuarioDTO request) {
        UsuarioDTO actualizado = usuarioService.actualizarUsuario(userId, request);
        return ResponseEntity.ok(actualizado);
    }
}


package com.impulse.api;

import com.impulse.application.usuario.UsuarioService;
import com.impulse.domain.usuario.UsuarioDTO;
import com.impulse.domain.usuario.UsuarioMapper;
import com.impulse.infrastructure.auth.AuthTokenRepository;
import com.impulse.domain.auth.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AuthTokenRepository authTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
        // Generar token JWT (simplificado, deberías usar una librería JWT real)
        String token = java.util.UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken(usuario.getId(), token, "SESSION", LocalDateTime.now().plusHours(12));
        authTokenRepository.save(authToken);
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("usuario", usuario);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioDTO request) {
        try {
            UsuarioDTO creado = usuarioService.crearUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Optional<AuthToken> authToken = authTokenRepository.findByToken(token);
        authToken.ifPresent(t -> { t.markUsed(); authTokenRepository.save(t); });
        return ResponseEntity.ok("Sesión cerrada");
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<AuthToken>> getSessions(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Optional<AuthToken> authToken = authTokenRepository.findByToken(token);
        if (authToken.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Long userId = authToken.get().getUserId();
        List<AuthToken> sessions = authTokenRepository.findAll().stream()
                .filter(t -> t.getUserId().equals(userId) && !t.isUsed() && !t.isExpired())
                .toList();
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<String> revokeSession(@PathVariable Long sessionId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Optional<AuthToken> current = authTokenRepository.findByToken(token);
        if (current.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Optional<AuthToken> toRevoke = authTokenRepository.findById(sessionId);
        if (toRevoke.isPresent() && toRevoke.get().getUserId().equals(current.get().getUserId())) {
            toRevoke.get().markUsed();
            authTokenRepository.save(toRevoke.get());
            return ResponseEntity.ok("Sesión revocada: " + sessionId);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sesión no encontrada");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        // Generar token de recuperación y simular envío de email
        String token = java.util.UUID.randomUUID().toString();
        AuthToken recoveryToken = new AuthToken(usuario.getId(), token, "RECOVERY", LocalDateTime.now().plusMinutes(30));
        authTokenRepository.save(recoveryToken);
        // Aquí deberías enviar el email real
        return ResponseEntity.ok("Email de recuperación enviado");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, Object> request) {
        String token = (String) request.get("token");
        String newPassword = (String) request.get("newPassword");
        Optional<AuthToken> recoveryToken = authTokenRepository.findByToken(token);
        if (recoveryToken.isEmpty() || recoveryToken.get().isUsed() || recoveryToken.get().isExpired() || !"RECOVERY".equals(recoveryToken.get().getType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido o expirado");
        }
        UsuarioDTO usuario = usuarioService.buscarPorId(recoveryToken.get().getUserId());
        if (usuario == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioService.actualizarUsuario(usuario.getId(), usuario);
        recoveryToken.get().markUsed();
        authTokenRepository.save(recoveryToken.get());
        return ResponseEntity.ok("Contraseña restablecida");
    }
}

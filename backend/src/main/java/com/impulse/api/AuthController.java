package com.impulse.api;

import com.impulse.infrastructure.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

record LoginRequest(String email, String password) {}

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final JwtUtil jwtUtil;
  public AuthController(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    // DEMO: credenciales desde variables de entorno (o .env en Compose)
    String demoEmail = System.getenv().getOrDefault("APP_DEMO_EMAIL", "admin@impulse.app");
    String demoPass  = System.getenv().getOrDefault("APP_DEMO_PASSWORD", "password");

    if (demoEmail.equals(req.email()) && demoPass.equals(req.password())) {
      String token = jwtUtil.generateToken(req.email());
      return ResponseEntity.ok(Map.of("token", token));
    }
    return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
  }
}

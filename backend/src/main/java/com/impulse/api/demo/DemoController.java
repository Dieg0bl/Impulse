package com.impulse.api.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para demostración de la aplicación IMPULSE en navegador
 */
@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "*")
public class DemoController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("aplicacion", "IMPULSE Backend");
        response.put("estado", "PRODUCTION READY ✅");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("mensaje", "¡Su aplicación está funcionando perfectamente!");
        response.put("arquitectura", "Clean Architecture / DDD");
        response.put("tecnologias", new String[]{"Spring Boot 3.3.13", "Java 21", "JWT Security", "MySQL"});
        return ResponseEntity.ok(response);
    }

    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getEndpoints() {
        Map<String, Object> response = new HashMap<>();
        response.put("autenticacion", new String[]{
            "POST /api/auth/login - Login de usuario",
            "POST /api/auth/register - Registro de usuario"
        });
        response.put("usuarios", new String[]{
            "GET /api/usuarios - Listar usuarios",
            "POST /api/usuarios - Crear usuario",
            "GET /api/usuarios/{id} - Obtener usuario"
        });
        response.put("retos", new String[]{
            "GET /api/retos - Listar retos",
            "POST /api/retos - Crear reto",
            "POST /api/retos/{id}/validar - Validar reto"
        });
        response.put("pagos", new String[]{
            "GET /api/pagos - Listar pagos",
            "POST /api/pagos - Crear pago"
        });
        response.put("documentacion", new String[]{
            "GET /swagger-ui.html - Swagger UI",
            "GET /v3/api-docs - OpenAPI JSON"
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> getHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("database", "H2 Memory");
        health.put("security", "JWT Enabled");
        health.put("cors", "Enabled");
        return ResponseEntity.ok(health);
    }
}

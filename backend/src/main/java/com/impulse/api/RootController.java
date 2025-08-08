package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para la ruta raíz de IMPULSE
 * Sirve el frontend HTML para usuarios y API JSON para llamadas programáticas
 */
@Controller
@CrossOrigin(origins = "*")
public class RootController {

    @GetMapping("/")
    public String welcome(@RequestHeader(value = "Accept", defaultValue = "") String acceptHeader) {
        // Si es una llamada de API (acepta JSON), redirige al endpoint JSON
        if (acceptHeader.contains("application/json")) {
            return "redirect:/api/status";
        }
        // Para navegadores normales, sirve el frontend HTML
        return "index.html";
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots(){
        return "User-agent: *\nAllow: /\nSitemap: /sitemap.xml\n";
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap(){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" +
                url("/") + url("/login") + url("/register") + url("/onboarding") +
                "</urlset>";
    }

    private String url(String path){
        return "<url><loc>https://app.impulse.local"+path+"</loc></url>";
    }

    @GetMapping("/api/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("aplicacion", "IMPULSE - Plataforma de Gestión de Retos");
        response.put("version", "1.0.0");
        response.put("estado", "ONLINE");
        response.put("timestamp", LocalDateTime.now());
        response.put("mensaje", "¡Bienvenido a IMPULSE! La plataforma está funcionando correctamente.");
        response.put("endpoints_publicos", new String[]{
            "GET / - Frontend web interactivo",
            "GET /api/status - Estado en formato JSON",
            "GET /actuator/health - Estado del servidor",
            "GET /h2-console - Consola de base de datos (solo demo)"
        });
        response.put("endpoints_api", new String[]{
            "POST /api/auth/login - Autenticación",
            "POST /api/auth/register - Registro de usuario",
            "GET /api/demo/status - Estado detallado (requiere auth)",
            "GET /api/demo/endpoints - Lista de endpoints (requiere auth)"
        });
        response.put("documentacion", "Los endpoints de API requieren autenticación JWT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("aplicacion", "IMPULSE Backend");
        response.put("descripcion", "Plataforma digital de gestión de retos con presión social real y gamificación");
        response.put("arquitectura", "Clean Architecture / DDD");
        response.put("tecnologias", new String[]{
            "Java 21", 
            "Spring Boot 3.3.13", 
            "Spring Security", 
            "JWT Authentication",
            "Hibernate JPA",
            "H2 Database (demo)",
            "Maven"
        });
        response.put("compliance", new String[]{
            "RGPD - Reglamento General de Protección de Datos",
            "ISO 27001 - Gestión de Seguridad de la Información", 
            "ENS - Esquema Nacional de Seguridad"
        });
        response.put("caracteristicas", new String[]{
            "Validación humana real",
            "Presión social auténtica", 
            "Gamificación controlada",
            "Auditoría completa",
            "Seguridad multicapa"
        });
        return ResponseEntity.ok(response);
    }
}

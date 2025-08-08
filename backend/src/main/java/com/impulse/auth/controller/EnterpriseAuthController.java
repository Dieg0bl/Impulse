package com.impulse.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.auth.dto.LoginRequest;
import com.impulse.auth.dto.LoginResponse;
import com.impulse.auth.dto.RegisterRequest;
import com.impulse.auth.service.AuthService;
import com.impulse.common.security.CookieAuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Controlador empresarial de autenticación con cookies HttpOnly y CSRF.
 * Implementa estándares de seguridad OWASP para autenticación web moderna.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {"http://localhost:3000", "https://impulse.empresa.com"}, 
    allowCredentials = "true",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
public class EnterpriseAuthController {
    
    // Constantes para mensajes de respuesta
    private static final String SUCCESS_KEY = "success";
    private static final String MESSAGE_KEY = "message";
    private static final String ERROR_KEY = "error";
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";
    private static final String AUTHENTICATED_KEY = "authenticated";
    
    private final AuthService authService;
    private final CookieAuthenticationService cookieService;
    
    public EnterpriseAuthController(AuthService authService, CookieAuthenticationService cookieService) {
        this.authService = authService;
        this.cookieService = cookieService;
    }

    /**
     * Solicita un magic link (fase 3). Devuelve token para pruebas; en producción sólo se enviaría por email.
     */
    @PostMapping("/magic/request")
    public ResponseEntity<Map<String,Object>> requestMagic(@RequestBody Map<String,String> body){
        try {
            String email = body.getOrDefault("email", "");
            String token = authService.generateMagicLink(email);
            Map<String,Object> resp = new HashMap<>();
            resp.put(SUCCESS_KEY, TRUE_VALUE);
            resp.put(MESSAGE_KEY, "magic_link_generado");
            resp.put("magic_token_dev", token); // Expuesto sólo para entorno dev/testing
            return ResponseEntity.ok(resp);
        } catch (Exception e){
            Map<String,Object> err = new HashMap<>();
            err.put(SUCCESS_KEY, FALSE_VALUE);
            err.put(ERROR_KEY, e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * Consume un magic link y retorna login (JWT legacy) para compatibilidad actual. Cookies gestionables en futuro si se desea.
     */
    @PostMapping("/magic/consume")
    public ResponseEntity<LoginResponse> consumeMagic(@RequestBody Map<String,String> body){
        String token = body.getOrDefault("token", "");
        LoginResponse lr = authService.consumeMagicLink(token);
        if(lr.isSuccess()){
            return ResponseEntity.ok(lr);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(lr);
    }
    
    /**
     * Login empresarial con establecimiento automático de cookies HttpOnly.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        
        try {
            // Autenticar usuario con servicio existente
            LoginResponse loginResponse = authService.login(loginRequest);
            
            if (loginResponse.isSuccess()) {
                // Crear autenticación Spring Security temporal para cookies
                Authentication authentication = authService.createAuthentication(
                    loginRequest.getEmail(), 
                    loginResponse.getRole()
                );
                
                // Establecer cookies empresariales
                cookieService.setAuthenticationCookies(response, authentication);
                
                // Respuesta sin token JWT (ahora está en cookies)
                return ResponseEntity.ok(new LoginResponse(
                    true,
                    "Login exitoso - autenticación por cookies establecida",
                    null, // Sin token en respuesta
                    loginResponse.getRole(),
                    loginResponse.getUserId(),
                    loginResponse.getUsername()
                ));
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LoginResponse(
                false,
                "Error en autenticación: " + e.getMessage(),
                null,
                null,
                null,
                null
            ));
        }
    }
    
    /**
     * Registro de usuario manteniendo compatibilidad con servicio existente.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            return authService.register(registerRequest);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put(MESSAGE_KEY, "Error en registro: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Logout empresarial con limpieza completa de cookies.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        try {
            // Limpiar cookies de autenticación
            cookieService.clearAuthenticationCookies(request, response);
            
            // Limpiar contexto de seguridad
            SecurityContextHolder.clearContext();
            
            Map<String, String> result = new HashMap<>();
            result.put(MESSAGE_KEY, "Logout exitoso - cookies limpiadas");
            result.put(SUCCESS_KEY, TRUE_VALUE);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put(MESSAGE_KEY, "Error en logout: " + e.getMessage());
            error.put(SUCCESS_KEY, FALSE_VALUE);
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Renovación manual de tokens (endpoint opcional para SPA).
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshTokens(
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        try {
            boolean renewed = cookieService.renewTokens(request, response);
            
            Map<String, Object> result = new HashMap<>();
            result.put(SUCCESS_KEY, renewed);
            result.put(MESSAGE_KEY, renewed ? "Tokens renovados exitosamente" : "No se pudieron renovar los tokens");
            
            if (renewed) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(401).body(result);
            }
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put(SUCCESS_KEY, false);
            error.put(MESSAGE_KEY, "Error renovando tokens: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Verificación de estado de autenticación actual.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            Map<String, Object> status = new HashMap<>();
            
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                status.put(AUTHENTICATED_KEY, true);
                status.put("userId", auth.getName());
                status.put("role", auth.getAuthorities().iterator().next().getAuthority());
                status.put("principal", auth.getPrincipal());
            } else {
                status.put(AUTHENTICATED_KEY, false);
                status.put(MESSAGE_KEY, "No hay sesión activa");
            }
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put(AUTHENTICATED_KEY, false);
            error.put(ERROR_KEY, "Error verificando estado: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Endpoint para obtener token CSRF para formularios.
     */
    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpServletRequest request) {
        try {
            String csrfToken = cookieService.extractCsrfToken(request).orElse("");
            
            Map<String, String> result = new HashMap<>();
            result.put("csrfToken", csrfToken);
            result.put(SUCCESS_KEY, !csrfToken.isEmpty() ? TRUE_VALUE : FALSE_VALUE);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put(ERROR_KEY, "Error obteniendo CSRF token: " + e.getMessage());
            error.put(SUCCESS_KEY, FALSE_VALUE);
            return ResponseEntity.badRequest().body(error);
        }
    }
}

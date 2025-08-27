package com.impulse.auth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import com.impulse.retention.RetentionService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.impulse.application.usuario.UsuarioService;
import com.impulse.auth.dto.LoginRequest;
import com.impulse.auth.dto.LoginResponse;
import com.impulse.auth.dto.RegisterRequest;
import com.impulse.common.security.JwtProvider;
import com.impulse.domain.auth.AuthToken;
import com.impulse.application.ports.AuthTokenPort;
import com.impulse.domain.usuario.UsuarioDTO;

/**
 * Servicio empresarial de autenticación con soporte para cookies HttpOnly.
 * Integra con el sistema existente de usuarios manteniendo compatibilidad.
 */
@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String MESSAGE_KEY = "message";
    
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    // Persistencia de magic links (tabla auth_tokens)
    private final AuthTokenPort authTokenRepository;
    private final com.impulse.security.LoginAttemptService loginAttempts;
    private final com.impulse.security.SecurityAuditService audit;
    private final JdbcTemplate jdbc;
    private final RetentionService retention;
    
    public AuthService(UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, AuthTokenPort authTokenRepository, com.impulse.security.LoginAttemptService loginAttempts, com.impulse.security.SecurityAuditService audit, JdbcTemplate jdbc, RetentionService retention) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authTokenRepository = authTokenRepository;
        this.loginAttempts = loginAttempts;
        this.audit = audit;
        this.jdbc = jdbc;
        this.retention = retention;
    }
    
    /**
     * Autenticación de usuario con validación de credenciales.
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Lockout check
            if(loginAttempts.isLocked(loginRequest.getEmail())){
                audit.audit(null, null, "LOGIN_LOCKED", "user", loginRequest.getEmail(), java.util.Map.of("minutes_until_unlock", loginAttempts.minutesUntilUnlock(loginRequest.getEmail())), "medium");
                return new LoginResponse(false, "Cuenta temporalmente bloqueada por múltiples intentos fallidos");
            }

            // Buscar usuario por email
            UsuarioDTO usuario = usuarioService.buscarPorEmail(loginRequest.getEmail());

            if (usuario == null) {
                loginAttempts.record(loginRequest.getEmail(), false);
                audit.audit(null, null, "LOGIN_USER_NOT_FOUND", "user", loginRequest.getEmail(), null, "low");
                return new LoginResponse(false, "Usuario no encontrado");
            }

            // Verificar estado del usuario
            if (!ESTADO_ACTIVO.equals(usuario.getEstado())) {
                loginAttempts.record(loginRequest.getEmail(), false);
                audit.audit(usuario.getId(), null, "LOGIN_INACTIVE", "user", String.valueOf(usuario.getId()), java.util.Map.of("estado", usuario.getEstado()), "medium");
                return new LoginResponse(false, "Usuario inactivo o suspendido");
            }

            // Validar contraseña
            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                loginAttempts.record(loginRequest.getEmail(), false);
                audit.audit(usuario.getId(), null, "LOGIN_BAD_PASSWORD", "user", String.valueOf(usuario.getId()), null, "low");
                return new LoginResponse(false, "Credenciales inválidas");
            }

            // Generar token JWT legacy (compatible con código existente)
            String token = jwtProvider.generateToken(usuario.getEmail(), usuario.getRoles());

            loginAttempts.record(loginRequest.getEmail(), true);
            audit.audit(usuario.getId(), null, "LOGIN_SUCCESS", "user", String.valueOf(usuario.getId()), null, "low");
            recordRetentionActivity(usuario.getId());
            return new LoginResponse(
                true,
                "Login exitoso",
                token,
                usuario.getRoles(),
                usuario.getId(),
                usuario.getNombre()
            );

        } catch (Exception e) {
            logger.error("Error en proceso de login", e);
            return new LoginResponse(false, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    /**
     * Registro de nuevo usuario con validaciones de negocio.
     */
    public ResponseEntity<Map<String, Object>> register(RegisterRequest registerRequest) {
        try {
            // Verificar si el usuario ya existe
            if (checkUserExists(registerRequest.getEmail())) {
                Map<String, Object> error = new HashMap<>();
                error.put(MESSAGE_KEY, "El email ya está registrado");
                return ResponseEntity.badRequest().body(error);
            }

            // Password policy
            String pwd = registerRequest.getPassword();
            if(!isPasswordStrong(pwd)){
                Map<String,Object> error = new HashMap<>();
                error.put(MESSAGE_KEY, "La contraseña no cumple la política (min 12, mayus, minus, dígito, símbolo)");
                return ResponseEntity.badRequest().body(error);
            }

            // Crear nuevo usuario usando DTO existente
            UsuarioDTO nuevoUsuario = createUserFromRequest(registerRequest);

            // Guardar usuario
            UsuarioDTO usuarioCreado = usuarioService.crearUsuario(nuevoUsuario);

            // Marcar conversión de invitación si aplica
            if(registerRequest.getInviteCode()!=null && !registerRequest.getInviteCode().isBlank()){
                markInviteConverted(registerRequest, usuarioCreado);
            }

            // Respuesta exitosa sin datos sensibles
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put(MESSAGE_KEY, "Usuario registrado exitosamente");
            response.put("userId", usuarioCreado.getId());
            response.put("email", usuarioCreado.getEmail());
            response.put("nombre", usuarioCreado.getNombre());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error en proceso de registro", e);
            Map<String, Object> error = new HashMap<>();
            error.put(MESSAGE_KEY, "Error en registro: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Crea un objeto Authentication para Spring Security.
     * Usado por el servicio de cookies para establecer el contexto de seguridad.
     */
    public Authentication createAuthentication(String email, String role) {
        return new UsernamePasswordAuthenticationToken(
            email,
            null,
            List.of(new SimpleGrantedAuthority(role))
        );
    }
    
    /**
     * Busca usuario por email para operaciones internas.
     */
    public UsuarioDTO findUserByEmail(String email) {
        try {
            return usuarioService.buscarPorEmail(email);
        } catch (Exception e) {
            logger.warn("Error buscando usuario por email: {}", email, e);
            return null;
        }
    }
    
    /**
     * Valida si un usuario está activo y puede autenticarse.
     */
    public boolean isUserActive(String email) {
        try {
            UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
            return usuario != null && ESTADO_ACTIVO.equals(usuario.getEstado());
        } catch (Exception e) {
            logger.warn("Error validando estado de usuario: {}", email, e);
            return false;
        }
    }
    
    /**
     * Actualiza la última fecha de acceso del usuario.
     */
    public void updateLastAccess(String email) {
        try {
            UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
            if (usuario != null) {
                // Actualizar timestamp de último acceso
                usuario.setUpdatedAt(java.time.LocalDateTime.now());
                usuarioService.actualizarUsuario(usuario.getId(), usuario);
            }
        } catch (Exception e) {
            // Log error pero no fallar el proceso de login
            logger.warn("Error actualizando último acceso para usuario: {}", email, e);
        }
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    /**
     * Registra actividad de retención para el usuario (no crítico para login).
     */
    private void recordRetentionActivity(Long userId) {
        try {
            retention.recordActivity(userId);
        } catch (Exception ignore) {
            // intentionally ignored: retention is not critical for login
        }
    }

    /**
     * Marca la conversión de una invitación si aplica.
     */
    private void markInviteConverted(RegisterRequest registerRequest, UsuarioDTO usuarioCreado) {
        try {
            var rows = jdbc.queryForList("SELECT * FROM invites WHERE code=?", registerRequest.getInviteCode());
            if(!rows.isEmpty()){
                var row = rows.get(0);
                if(!Boolean.TRUE.equals(row.get("accepted"))){
                    jdbc.update("UPDATE invites SET accepted=TRUE, accepted_at=NOW() WHERE id=?", row.get("id"));
                    audit.audit(usuarioCreado.getId(), null, "INVITE_CONVERTED_REGISTER", "invite", String.valueOf(row.get("id")), java.util.Map.of("referrer_id", row.get("referrer_id")), "low");
                }
            }
        } catch (Exception ex){
            logger.warn("Fallo marcando conversión de invitación: {}", ex.getMessage());
        }
    }
    
    /**
     * Verifica si un usuario ya existe por email.
     */
    private boolean checkUserExists(String email) {
        try {
            UsuarioDTO existente = usuarioService.buscarPorEmail(email);
            return existente != null;
        } catch (Exception e) {
            logger.debug("Usuario no existe: {}", email);
            return false;
        }
    }
    
    /**
     * Crea un DTO de usuario a partir del request de registro.
     */
    private UsuarioDTO createUserFromRequest(RegisterRequest registerRequest) {
        UsuarioDTO nuevoUsuario = new UsuarioDTO();
        nuevoUsuario.setEmail(registerRequest.getEmail());
        nuevoUsuario.setNombre(registerRequest.getNombre());
        nuevoUsuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        nuevoUsuario.setRoles("ROLE_USER"); // Rol por defecto
        nuevoUsuario.setEstado(ESTADO_ACTIVO);
        nuevoUsuario.setConsentimientoAceptado(registerRequest.isAceptaDatos());
        return nuevoUsuario;
    }

    private boolean isPasswordStrong(String pwd){
        if(pwd==null || pwd.length()<12) return false;
        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        boolean special = false;
        for(char c: pwd.toCharArray()){
            if(Character.isUpperCase(c)) {
                upper = true;
            } else if(Character.isLowerCase(c)) {
                lower = true;
            } else if(Character.isDigit(c)) {
                digit = true;
            } else {
                special = true;
            }
        }
        return upper && lower && digit && special;
    }

    // ==================== MAGIC LINK (fase 3) ====================
    /**
     * Genera un magic link temporal (válido 10 minutos) y devuelve el token para envío por email.
     * No persiste en base de datos para minimizar cambios; si se requiere durabilidad, migrar a tabla auth_tokens.
     */
    public String generateMagicLink(String email){
        UsuarioDTO user = usuarioService.buscarPorEmail(email);
        if(user==null || !ESTADO_ACTIVO.equals(user.getEstado())){
            throw new IllegalArgumentException("usuario_invalido");
        }
        String token = java.util.UUID.randomUUID().toString().replace("-", "");
        AuthToken at = new AuthToken(user.getId(), token, "MAGIC", java.time.LocalDateTime.now().plusMinutes(10));
        authTokenRepository.save(at);
        return token;
    }

    /**
     * Consume un magic link: valida expiración y retorna LoginResponse (emitiendo token JWT legacy para compatibilidad).
     */
    public LoginResponse consumeMagicLink(String token){
        return authTokenRepository.findByToken(token)
            .map(at -> {
                if(at.isUsed() || at.isExpired() || !"MAGIC".equals(at.getType())){
                    return new LoginResponse(false, "magic_link_invalido_o_expirado");
                }
                at.markUsed();
                authTokenRepository.save(at); // persistimos estado usado
                UsuarioDTO usuario = usuarioService.buscarPorId(at.getUserId());
                if(usuario==null) return new LoginResponse(false, "usuario_no_encontrado");
                String jwt = jwtProvider.generateToken(usuario.getEmail(), usuario.getRoles());
                updateLastAccess(usuario.getEmail());
                recordRetentionActivity(usuario.getId());
                return new LoginResponse(true, "login_magic_ok", jwt, usuario.getRoles(), usuario.getId(), usuario.getNombre());
            })
            .orElseGet(() -> new LoginResponse(false, "magic_link_invalido_o_expirado"));
    }
}

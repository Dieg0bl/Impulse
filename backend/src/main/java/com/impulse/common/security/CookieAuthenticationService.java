package com.impulse.common.security;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servicio empresarial para gestión de autenticación por cookies HttpOnly.
 * Implementa estándares de seguridad OWASP con tokens de acceso y refresh rotativos.
 * Utiliza cache empresarial distribuido para escalabilidad en producción.
 */
@Service
public class CookieAuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CookieAuthenticationService.class);
    
    // Nombres de cookies estándar empresarial
    private static final String ACCESS_TOKEN_COOKIE = "IMPULSE_TOKEN";
    private static final String REFRESH_TOKEN_COOKIE = "IMPULSE_REFRESH";
    private static final String CSRF_TOKEN_COOKIE = "XSRF-TOKEN";
    
    // Duración de tokens según mejores prácticas
    private final Duration accessTokenDuration = Duration.ofMinutes(15);  // Corto para seguridad
    private final Duration refreshTokenDuration = Duration.ofDays(7);     // Una semana máximo
    
    @Autowired
    private EnterpriseTokenCache tokenCache;
    
    @Value("${app.security.cookie.secure:true}")
    private boolean secureCookies;
    
    @Value("${app.security.cookie.domain:}")
    private String cookieDomain;
    
    private final JwtProvider jwtProvider;
    
    public CookieAuthenticationService(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }
    
    /**
     * Establece cookies de autenticación tras login exitoso.
     * Implementa patrón dual-token con rotación automática.
     */
    public void setAuthenticationCookies(HttpServletResponse response, Authentication authentication) {
        String userId = authentication.getName();
        String userRole = authentication.getAuthorities().iterator().next().getAuthority();
        
        // Generar tokens
        String accessToken = jwtProvider.generateAccessToken(userId, userRole);
        String refreshToken = generateRefreshToken(userId, userRole);
        String csrfToken = generateCsrfToken();
        
        // Almacenar refresh token con metadata en cache empresarial
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenDuration.toSeconds());
        RefreshTokenData tokenData = new RefreshTokenData(
            userId, 
            userRole, 
            expiresAt,
            csrfToken,
            getClientIpAddress(null),
            getUserAgent(null)
        );
        
        tokenCache.put(refreshToken, tokenData);
        
        // Establecer cookies con máxima seguridad
        setSecureCookie(response, ACCESS_TOKEN_COOKIE, accessToken, (int) accessTokenDuration.toSeconds(), true);
        setSecureCookie(response, REFRESH_TOKEN_COOKIE, refreshToken, (int) refreshTokenDuration.toSeconds(), true);
        setSecureCookie(response, CSRF_TOKEN_COOKIE, csrfToken, (int) refreshTokenDuration.toSeconds(), false); // No HttpOnly para JS
        
        logger.info("Cookies de autenticación establecidas para usuario: {} con role: {}", userId, userRole);
    }
    
    /**
     * Extrae token de acceso de cookies de forma segura.
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return getCookieValue(request, ACCESS_TOKEN_COOKIE);
    }
    
    /**
     * Extrae token CSRF de cookies para validación.
     */
    public Optional<String> extractCsrfToken(HttpServletRequest request) {
        return getCookieValue(request, CSRF_TOKEN_COOKIE);
    }
    
    /**
     * Renueva tokens usando refresh token. Implementa rotación de un solo uso.
     */
    public boolean renewTokens(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshTokenOpt = getCookieValue(request, REFRESH_TOKEN_COOKIE);
        Optional<String> csrfTokenOpt = extractCsrfToken(request);
        
        if (refreshTokenOpt.isEmpty()) {
            logger.warn("Intento de renovación sin refresh token desde IP: {}", getClientIpAddress(request));
            return false;
        }
        
        String refreshToken = refreshTokenOpt.get();
        RefreshTokenData tokenData = tokenCache.remove(refreshToken); // Un solo uso - cache empresarial
        
        if (tokenData == null || tokenData.isExpired(LocalDateTime.now())) {
            logger.warn("Refresh token inválido o expirado desde IP: {}", getClientIpAddress(request));
            return false;
        }
        
        // Validar CSRF token para mayor seguridad
        if (csrfTokenOpt.isPresent() && !tokenData.getCsrfToken().equals(csrfTokenOpt.get())) {
            logger.warn("CSRF token mismatch durante renovación de tokens para usuario: {}", tokenData.getUserId());
            return false;
        }
        
        // Validar integridad de sesión
        String currentIp = getClientIpAddress(request);
        String currentUserAgent = getUserAgent(request);
        if (!tokenData.isValidForSession(tokenData.getUserId(), currentUserAgent, currentIp)) {
            logger.error("Posible session hijacking detectado para usuario: {} desde IP: {}", 
                        tokenData.getUserId(), currentIp);
            clearAuthenticationCookies(request, response);
            return false;
        }
        
        // Generar nuevos tokens
        String newAccessToken = jwtProvider.generateAccessToken(tokenData.getUserId(), tokenData.getUserRole());
        String newRefreshToken = generateRefreshToken(tokenData.getUserId(), tokenData.getUserRole());
        String newCsrfToken = generateCsrfToken();
        
        // Almacenar nuevo refresh token en cache empresarial
        LocalDateTime newExpiresAt = LocalDateTime.now().plusSeconds(refreshTokenDuration.toSeconds());
        RefreshTokenData newTokenData = new RefreshTokenData(
            tokenData.getUserId(),
            tokenData.getUserRole(),
            newExpiresAt,
            newCsrfToken,
            currentIp,
            currentUserAgent
        );
        
        tokenCache.put(newRefreshToken, newTokenData);
        
        // Establecer nuevas cookies
        setSecureCookie(response, ACCESS_TOKEN_COOKIE, newAccessToken, (int) accessTokenDuration.toSeconds(), true);
        setSecureCookie(response, REFRESH_TOKEN_COOKIE, newRefreshToken, (int) refreshTokenDuration.toSeconds(), true);
        setSecureCookie(response, CSRF_TOKEN_COOKIE, newCsrfToken, (int) refreshTokenDuration.toSeconds(), false);
        
        logger.info("Tokens renovados exitosamente para usuario: {}", tokenData.getUserId());
        return true;
    }
    
    /**
     * Revoca todas las cookies de autenticación de forma segura.
     */
    public void clearAuthenticationCookies(HttpServletRequest request, HttpServletResponse response) {
        // Revocar refresh token si existe en cache empresarial
        getCookieValue(request, REFRESH_TOKEN_COOKIE)
            .ifPresent(refreshToken -> {
                RefreshTokenData tokenData = tokenCache.remove(refreshToken);
                if (tokenData != null) {
                    logger.info("Logout exitoso para usuario: {}", tokenData.getUserId());
                }
            });
        
        // Limpiar todas las cookies
        clearCookie(response, ACCESS_TOKEN_COOKIE);
        clearCookie(response, REFRESH_TOKEN_COOKIE);
        clearCookie(response, CSRF_TOKEN_COOKIE);
    }
    
    /**
     * Valida token CSRF contra header de request.
     */
    public boolean validateCsrfToken(HttpServletRequest request) {
        String cookieCsrf = getCookieValue(request, CSRF_TOKEN_COOKIE).orElse("");
        String headerCsrf = request.getHeader("X-XSRF-TOKEN");
        
        boolean isValid = StringUtils.hasText(cookieCsrf) && 
                         StringUtils.hasText(headerCsrf) && 
                         cookieCsrf.equals(headerCsrf);
        
        if (!isValid) {
            logger.warn("CSRF token validation failed from IP: {}", getClientIpAddress(request));
        }
        
        return isValid;
    }
    
    /**
     * Obtiene métricas del cache empresarial de tokens
     */
    public EnterpriseTokenCache.CacheMetrics getCacheMetrics() {
        return tokenCache.getMetrics();
    }
    
    /**
     * Invalida todas las sesiones de un usuario específico
     */
    public void invalidateAllUserSessions(String userId) {
        try {
            // En un cache distribuido real, iteraríamos por tokens del usuario
            // Por seguridad, limpiamos todo el cache
            tokenCache.clear();
            logger.warn("Cache de tokens limpiado para invalidar sesiones de usuario: {}", userId);
        } catch (Exception e) {
            logger.error("Error invalidando sesiones para usuario: {}", userId, e);
        }
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    private void setSecureCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secureCookies);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict");
        
        if (StringUtils.hasText(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        
        response.addCookie(cookie);
    }
    
    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookies);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Eliminar inmediatamente
        cookie.setAttribute("SameSite", "Strict");
        
        if (StringUtils.hasText(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        
        response.addCookie(cookie);
    }
    
    private Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst();
    }
    
    private String generateRefreshToken(String userId, String userRole) {
        // Token seguro con información de contexto
        return UUID.randomUUID().toString() + "." + userId + "." + userRole + "." + System.currentTimeMillis();
    }
    
    private String generateCsrfToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return "unknown";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private String getUserAgent(HttpServletRequest request) {
        if (request == null) return "unknown";
        return request.getHeader("User-Agent");
    }
    
    /**
     * Limpieza automática de tokens expirados (ejecutar en scheduler)
     */
    public void cleanupExpiredTokens() {
        tokenCache.cleanupExpiredTokens();
        logger.debug("Limpieza automática de tokens completada");
    }
}

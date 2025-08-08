package com.impulse.common.security.filters;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.impulse.common.security.CookieAuthenticationService;
import com.impulse.common.security.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro empresarial de autenticación por cookies HttpOnly.
 * Implementa validación de tokens JWT con renovación automática y protección CSRF.
 */
@Component
public class CookieAuthenticationFilter extends OncePerRequestFilter {
    
    private final CookieAuthenticationService cookieService;
    private final JwtProvider jwtProvider;
    
    public CookieAuthenticationFilter(CookieAuthenticationService cookieService, JwtProvider jwtProvider) {
        this.cookieService = cookieService;
        this.jwtProvider = jwtProvider;
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Verificar si ya está autenticado
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Intentar autenticación por cookie
            if (authenticateFromCookie(request, response)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Intentar renovación de tokens
            if (attemptTokenRenewal(request, response)) {
                filterChain.doFilter(request, response);
                return;
            }
            
        } catch (RuntimeException | IOException ex) {
            logger.error("Error en autenticación por cookie", ex);
            // Limpiar cookies corruptas
            cookieService.clearAuthenticationCookies(request, response);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Intenta autenticar usando el token de acceso en cookies.
     */
    private boolean authenticateFromCookie(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> accessTokenOpt = cookieService.extractAccessToken(request);
        
        if (accessTokenOpt.isEmpty()) {
            return false;
        }
        
        String accessToken = accessTokenOpt.get();
        
        if (!jwtProvider.validateToken(accessToken)) {
            return false;
        }
        
        // Validar CSRF en operaciones de modificación
        if (requiresCsrfValidation(request) && !cookieService.validateCsrfToken(request)) {
            logger.warn("CSRF token inválido para request: " + request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        
        // Extraer información del token
        String userId = jwtProvider.getUserIdFromToken(accessToken);
        String userRole = jwtProvider.getUserRoleFromToken(accessToken);
        
        // Crear autenticación
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            List.of(new SimpleGrantedAuthority(userRole))
        );
        
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        return true;
    }
    
    /**
     * Intenta renovar tokens usando refresh token.
     */
    private boolean attemptTokenRenewal(HttpServletRequest request, HttpServletResponse response) {
        if (!cookieService.renewTokens(request, response)) {
            return false;
        }
        
        // Intentar autenticación con nuevos tokens
        return authenticateFromCookie(request, response);
    }
    
    /**
     * Determina si el request requiere validación CSRF.
     * Solo operaciones de modificación (POST, PUT, DELETE, PATCH).
     */
    private boolean requiresCsrfValidation(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase();
        return "POST".equals(method) || 
               "PUT".equals(method) || 
               "DELETE".equals(method) || 
               "PATCH".equals(method);
    }
    
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Excluir endpoints públicos de autenticación
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/register") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/actuator/health") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/static/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/");
    }
}

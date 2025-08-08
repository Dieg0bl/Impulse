package com.impulse.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Simple API key auth filter for administrative/automation endpoints.
 * Applies to paths beginning with /api/admin or containing /secure/.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeys;
    private final SecurityAuditService audit;

    public ApiKeyAuthFilter(ApiKeyService apiKeys, SecurityAuditService audit){ this.apiKeys = apiKeys; this.audit = audit; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        boolean protectedPath = path.startsWith("/api/admin") || path.contains("/secure/");
        if(!protectedPath){
            filterChain.doFilter(request, response); return;
        }
        String key = request.getHeader("X-API-Key");
        if(key==null || key.isBlank()){
            response.setStatus(401); response.setContentType("application/json"); response.getWriter().write("{\"error\":\"api_key_required\"}"); return;
        }
        boolean ok = apiKeys.validateAndTouch(key, request.getRemoteAddr());
        if(!ok){
            audit.audit(null, request.getRemoteAddr(), "API_KEY_INVALID", "api_key", null, null, "medium");
            response.setStatus(403); response.setContentType("application/json"); response.getWriter().write("{\"error\":\"invalid_api_key\"}"); return;
        }
        filterChain.doFilter(request, response);
    }
}

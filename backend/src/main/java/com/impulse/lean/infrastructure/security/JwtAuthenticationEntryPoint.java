package com.impulse.lean.infrastructure.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * IMPULSE LEAN v1 - JWT Authentication Entry Point
 * 
 * Handles authentication failures for JWT-protected endpoints
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String jsonResponse = String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"Authentication required to access this resource\",\"path\":\"%s\",\"timestamp\":\"%s\"}",
            request.getRequestURI(), 
            java.time.Instant.now()
        );
        
        response.getOutputStream().println(jsonResponse);
    }
}

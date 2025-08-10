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

/** Global privacy kill switch: when flag privacy.kill_switch=true most write endpoints are blocked. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PrivacyKillSwitchFilter extends OncePerRequestFilter {
    private final KillSwitchService killSwitch;
    public PrivacyKillSwitchFilter(KillSwitchService killSwitch){ this.killSwitch = killSwitch; }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if(killSwitch.isActive()){
            String path = request.getRequestURI();
            boolean allowed = path.startsWith("/api/privacy") || path.startsWith("/actuator") || "GET".equals(request.getMethod());
            if(!allowed){
                response.setStatus(503);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"privacy_kill_switch_active\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

package com.impulse.common.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

@Component
public class RBACFilter extends OncePerRequestFilter {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RBACFilter.class);
    private static final String LOG_INIT = "RBACFilter inicializado para control de acceso";
    private static final String LOG_ACCESS = "RBACFilter ejecutando control de acceso para request: {}";

    public RBACFilter() {
        LOGGER.info(LOG_INIT);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        LOGGER.debug(LOG_ACCESS, request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}

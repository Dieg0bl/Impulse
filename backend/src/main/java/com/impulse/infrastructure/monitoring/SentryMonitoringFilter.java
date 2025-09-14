package com.impulse.monitoring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * Filtro para monitoreo de errores y eventos
 * Captura excepciones y eventos de monitoreo (preparado para Sentry)
 */
@Configuration
@Order(1)
public class SentryMonitoringFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SentryMonitoringFilter.class);

    @Value("${monitoring.enabled:true}")
    private boolean monitoringEnabled;

    @Value("${monitoring.environment:development}")
    private String environment;

    @Value("${monitoring.capture-errors:true}")
    private boolean captureErrors;

    /**
     * Inicialización del monitoreo
     */
    @PostConstruct
    public void initMonitoring() {
        if (monitoringEnabled) {
            logger.info("Monitoring filter initialized for environment: {}", environment);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!monitoringEnabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        try {
            // Ejecutar la cadena de filtros
            chain.doFilter(request, response);

            // Loggear información de respuesta si hay errores
            if (httpResponse.getStatus() >= 400 && captureErrors) {
                captureHttpError(httpRequest, httpResponse, startTime);
            }

        } catch (Exception e) {
            // Capturar excepciones no manejadas
            if (captureErrors) {
                captureException(e, httpRequest, startTime);
            }
            throw e; // Re-lanzar la excepción
        }
    }

    /**
     * Captura errores HTTP (4xx, 5xx)
     */
    private void captureHttpError(HttpServletRequest request, HttpServletResponse response, long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        String message = String.format("HTTP %d error on %s %s (duration: %dms)",
            response.getStatus(),
            request.getMethod(),
            request.getRequestURI(),
            duration);

        if (response.getStatus() >= 500) {
            logger.error("Server error: {} - Headers: {}", message, getRequestHeaders(request));
        } else {
            logger.warn("Client error: {}", message);
        }
    }

    /**
     * Captura excepciones no manejadas
     */
    private void captureException(Exception e, HttpServletRequest request, long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        logger.error("Unhandled exception on {} {} (duration: {}ms): {}",
            request.getMethod(),
            request.getRequestURI(),
            duration,
            e.getMessage(),
            e);
    }

    /**
     * Obtiene headers de la petición
     */
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // Filtrar headers sensibles
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }

        return headers;
    }

    /**
     * Obtiene parámetros de la petición
     */
    private Map<String, String[]> getRequestParameters(HttpServletRequest request) {
        Map<String, String[]> parameters = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String paramName = entry.getKey();
            // Filtrar parámetros sensibles
            if (!isSensitiveParameter(paramName)) {
                parameters.put(paramName, entry.getValue());
            }
        }

        return parameters;
    }

    /**
     * Verifica si un header es sensible y no debe ser capturado
     */
    private boolean isSensitiveHeader(String headerName) {
        String lowerCaseName = headerName.toLowerCase();
        return lowerCaseName.contains("authorization") ||
               lowerCaseName.contains("cookie") ||
               lowerCaseName.contains("token") ||
               lowerCaseName.contains("password");
    }

    /**
     * Verifica si un parámetro es sensible y no debe ser capturado
     */
    private boolean isSensitiveParameter(String paramName) {
        String lowerCaseName = paramName.toLowerCase();
        return lowerCaseName.contains("password") ||
               lowerCaseName.contains("token") ||
               lowerCaseName.contains("secret") ||
               lowerCaseName.contains("key");
    }
}

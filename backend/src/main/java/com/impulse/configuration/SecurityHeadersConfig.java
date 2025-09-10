package com.impulse.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Configuración de headers de seguridad
 * - CORS policy configurada
 * - Security headers (CSP, HSTS, etc.)
 * - Content Security Policy con nonces
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    /**
     * Configuración CORS para desarrollo y producción
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                    "http://localhost:*",           // Desarrollo local
                    "https://*.impulse.app",        // Producción
                    "https://impulse.app",          // Dominio principal
                    "https://*.vercel.app",         // Deploy previews
                    "https://*.netlify.app"         // Deploy previews
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight por 1 hora
    }

    /**
     * Filtro para headers de seguridad
     */
    @Bean
    public Filter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }

    /**
     * Implementación del filtro de headers de seguridad
     */
    public static class SecurityHeadersFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            // Generar nonce para CSP
            String nonce = generateNonce();
            req.setAttribute("cspNonce", nonce);

            // Headers de seguridad fundamentales
            addSecurityHeaders(res, nonce, req);

            chain.doFilter(request, response);
        }

        /**
         * Agrega todos los headers de seguridad
         */
        private void addSecurityHeaders(HttpServletResponse response, String nonce, HttpServletRequest request) {
            
            // Content Security Policy (CSP)
            String cspPolicy = buildContentSecurityPolicy(nonce, request);
            response.setHeader("Content-Security-Policy", cspPolicy);
            
            // HTTP Strict Transport Security (HSTS)
            if (isHttpsRequest(request)) {
                response.setHeader("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains; preload");
            }
            
            // Prevenir clickjacking
            response.setHeader("X-Frame-Options", "DENY");
            
            // Prevenir MIME type sniffing
            response.setHeader("X-Content-Type-Options", "nosniff");
            
            // XSS Protection (para navegadores legacy)
            response.setHeader("X-XSS-Protection", "1; mode=block");
            
            // Referrer Policy
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            // Permissions Policy (Feature Policy)
            response.setHeader("Permissions-Policy", buildPermissionsPolicy());
            
            // Cross-Origin-Embedder-Policy
            response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
            
            // Cross-Origin-Opener-Policy
            response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
            
            // Cross-Origin-Resource-Policy
            response.setHeader("Cross-Origin-Resource-Policy", "same-site");
            
            // Server header removal (no revelar tecnología)
            response.setHeader("Server", "IMPULSE");
            
            // Cache control para endpoints sensibles
            if (isSensitiveEndpoint(request.getRequestURI())) {
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
            }
        }

        /**
         * Construye la política CSP dinámica
         */
        private String buildContentSecurityPolicy(String nonce, HttpServletRequest request) {
            StringBuilder csp = new StringBuilder();
            
            // Directivas base
            csp.append("default-src 'self'; ");
            
            // Scripts - solo permitir con nonce o self
            csp.append("script-src 'self' 'nonce-").append(nonce).append("' ");
            csp.append("https://js.stripe.com https://www.paypal.com https://www.google.com; ");
            
            // Estilos
            csp.append("style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; ");
            
            // Imágenes - permitir data URIs para avatares y previews
            csp.append("img-src 'self' data: blob: https:; ");
            
            // Conectividad
            csp.append("connect-src 'self' ");
            csp.append("https://api.stripe.com https://api.paypal.com ");
            csp.append("wss://ws.impulse.app; ");
            
            // Fonts
            csp.append("font-src 'self' https://fonts.gstatic.com; ");
            
            // Media para evidencias
            csp.append("media-src 'self' blob:; ");
            
            // Objects y embeds bloqueados
            csp.append("object-src 'none'; ");
            csp.append("embed-src 'none'; ");
            
            // Base URI
            csp.append("base-uri 'self'; ");
            
            // Form actions
            csp.append("form-action 'self'; ");
            
            // Frame ancestors (prevenir clickjacking)
            csp.append("frame-ancestors 'none'; ");
            
            // Upgrade insecure requests en producción
            if (isProductionEnvironment(request)) {
                csp.append("upgrade-insecure-requests; ");
            }
            
            return csp.toString().trim();
        }

        /**
         * Construye la política de permisos
         */
        private String buildPermissionsPolicy() {
            return "accelerometer=(), " +
                   "ambient-light-sensor=(), " +
                   "autoplay=(self), " +
                   "battery=(), " +
                   "camera=(self), " +
                   "display-capture=(), " +
                   "document-domain=(), " +
                   "encrypted-media=(self), " +
                   "execution-while-not-rendered=(), " +
                   "execution-while-out-of-viewport=(), " +
                   "fullscreen=(self), " +
                   "geolocation=(), " +
                   "gyroscope=(), " +
                   "magnetometer=(), " +
                   "microphone=(self), " +
                   "midi=(), " +
                   "navigation-override=(), " +
                   "payment=(self), " +
                   "picture-in-picture=(), " +
                   "publickey-credentials-get=(self), " +
                   "screen-wake-lock=(), " +
                   "sync-xhr=(), " +
                   "usb=(), " +
                   "web-share=(self), " +
                   "xr-spatial-tracking=()";
        }

        /**
         * Genera nonce aleatorio para CSP
         */
        private String generateNonce() {
            return UUID.randomUUID().toString().replace("-", "");
        }

        /**
         * Verifica si es request HTTPS
         */
        private boolean isHttpsRequest(HttpServletRequest request) {
            return "https".equals(request.getScheme()) || 
                   "https".equals(request.getHeader("X-Forwarded-Proto"));
        }

        /**
         * Verifica si es endpoint sensible que requiere headers de cache estrictos
         */
        private boolean isSensitiveEndpoint(String uri) {
            return uri.startsWith("/api/auth/") ||
                   uri.startsWith("/api/admin/") ||
                   uri.startsWith("/api/payments/") ||
                   uri.contains("/profile") ||
                   uri.contains("/settings");
        }

        /**
         * Verifica si es entorno de producción
         */
        private boolean isProductionEnvironment(HttpServletRequest request) {
            String host = request.getHeader("Host");
            return host != null && (host.contains("impulse.app") && !host.contains("dev."));
        }
    }
}

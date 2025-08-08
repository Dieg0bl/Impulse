package com.impulse.common.navigation;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * URL State Manager para gestión avanzada de estado en URLs.
 * Proporciona sincronización bidireccional entre estado de aplicación y URL.
 * 
 * Funcionalidades:
 * - Encoding/decoding seguro de estado complejo en URLs
 * - Bookmarking inteligente con restauración de contexto
 * - Compresión automática para URLs largas
 * - Validación y sanitización de estado
 */
@Component
public class URLStateManager {

    private static final Logger logger = LoggerFactory.getLogger(URLStateManager.class);
    
    private final ObjectMapper objectMapper;
    private static final String STATE_PARAM = "state";
    private static final String TIMESTAMP_PARAM = "ts";
    
    public URLStateManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Representa el estado completo de una página/vista
     */
    public static class PageState {
        private String pageId;
        private Map<String, Object> viewState;
        private Map<String, Object> filterState;
        private Map<String, Object> formState;
        private LocalDateTime timestamp;
        private String userId;
        private Map<String, Object> metadata;
        
        public PageState() {
            this.viewState = new HashMap<>();
            this.filterState = new HashMap<>();
            this.formState = new HashMap<>();
            this.metadata = new HashMap<>();
            this.timestamp = LocalDateTime.now();
        }
        
        // Getters y setters
        public String getPageId() { return pageId; }
        public void setPageId(String pageId) { this.pageId = pageId; }
        
        public Map<String, Object> getViewState() { return viewState; }
        public void setViewState(Map<String, Object> viewState) { this.viewState = viewState; }
        
        public Map<String, Object> getFilterState() { return filterState; }
        public void setFilterState(Map<String, Object> filterState) { this.filterState = filterState; }
        
        public Map<String, Object> getFormState() { return formState; }
        public void setFormState(Map<String, Object> formState) { this.formState = formState; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    /**
     * Codifica el estado de página en parámetros URL seguros
     */
    public Map<String, String> encodePageState(PageState pageState) {
        Map<String, String> urlParams = new HashMap<>();
        
        try {
            // Serializar estado a JSON compacto
            String stateJson = objectMapper.writeValueAsString(pageState);
            
            // Comprimir si es necesario
            String encodedState = compressIfNeeded(stateJson);
            
            // URL encode para seguridad
            urlParams.put(STATE_PARAM, URLEncoder.encode(encodedState, StandardCharsets.UTF_8));
            urlParams.put(TIMESTAMP_PARAM, pageState.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            logger.debug("Estado codificado para página {}: {} caracteres", 
                pageState.getPageId(), encodedState.length());
            
        } catch (JsonProcessingException e) {
            logger.error("Error codificando estado de página {}: {}", pageState.getPageId(), e.getMessage());
            // Fallback: solo timestamp
            urlParams.put(TIMESTAMP_PARAM, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        return urlParams;
    }
    
    /**
     * Decodifica el estado de página desde parámetros URL
     */
    public Optional<PageState> decodePageState(Map<String, String> urlParams) {
        try {
            String encodedState = urlParams.get(STATE_PARAM);
            if (encodedState == null || encodedState.isEmpty()) {
                logger.debug("No se encontró estado en URL");
                return Optional.empty();
            }
            
            // URL decode
            String decodedState = URLDecoder.decode(encodedState, StandardCharsets.UTF_8);
            
            // Descomprimir si es necesario
            String stateJson = decompressIfNeeded(decodedState);
            
            // Deserializar desde JSON
            PageState pageState = objectMapper.readValue(stateJson, PageState.class);
            
            // Validar timestamp (no más de 24 horas)
            if (isStateValid(pageState)) {
                logger.debug("Estado decodificado exitosamente para página {}", pageState.getPageId());
                return Optional.of(pageState);
            } else {
                logger.warn("Estado inválido o expirado para página {}", pageState.getPageId());
                return Optional.empty();
            }
            
        } catch (JsonProcessingException | IllegalArgumentException e) {
            logger.error("Error decodificando estado desde URL: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Crea un estado de página desde parámetros específicos
     */
    public PageState createPageState(String pageId, String userId) {
        PageState state = new PageState();
        state.setPageId(pageId);
        state.setUserId(userId);
        
        // Metadata de contexto
        state.getMetadata().put("created", LocalDateTime.now());
        state.getMetadata().put("version", "1.0");
        
        return state;
    }
    
    /**
     * Actualiza estado de vista (filtros, paginación, ordenamiento)
     */
    public PageState updateViewState(PageState pageState, String key, Object value) {
        pageState.getViewState().put(key, value);
        pageState.setTimestamp(LocalDateTime.now());
        return pageState;
    }
    
    /**
     * Actualiza estado de filtros
     */
    public PageState updateFilterState(PageState pageState, Map<String, Object> filters) {
        pageState.getFilterState().putAll(filters);
        pageState.setTimestamp(LocalDateTime.now());
        return pageState;
    }
    
    /**
     * Actualiza estado de formulario (para recuperación)
     */
    public PageState updateFormState(PageState pageState, Map<String, Object> formData) {
        pageState.getFormState().putAll(formData);
        pageState.setTimestamp(LocalDateTime.now());
        return pageState;
    }
    
    /**
     * Convierte estado a query string completo
     */
    public String buildQueryString(PageState pageState) {
        Map<String, String> params = encodePageState(pageState);
        
        return params.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));
    }
    
    /**
     * Extrae estado desde query string
     */
    public Optional<PageState> parseQueryString(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return Optional.empty();
        }
        
        Map<String, String> params = Arrays.stream(queryString.split("&"))
            .filter(param -> param.contains("="))
            .map(param -> param.split("=", 2))
            .collect(Collectors.toMap(
                arr -> arr[0],
                arr -> arr.length > 1 ? arr[1] : ""
            ));
        
        return decodePageState(params);
    }
    
    // Métodos de utilidad privados
    
    private String compressIfNeeded(String input) {
        // Para simplificar, usamos Base64. En producción se podría usar GZIP
        if (input.length() > 1000) {
            return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
        }
        return input;
    }
    
    private String decompressIfNeeded(String input) {
        try {
            // Intentar decodificar Base64
            byte[] decoded = Base64.getDecoder().decode(input);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // No está en Base64, devolver tal como está
            return input;
        }
    }
    
    private boolean isStateValid(PageState pageState) {
        if (pageState.getTimestamp() == null) {
            return false;
        }
        
        // Estado válido por 24 horas
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        return pageState.getTimestamp().isAfter(cutoff);
    }
}

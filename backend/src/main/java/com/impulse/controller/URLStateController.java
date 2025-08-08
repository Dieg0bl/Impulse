package com.impulse.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.common.navigation.URLStateManager;
import com.impulse.common.navigation.URLStateManager.PageState;
import com.impulse.domain.usuario.Usuario;

/**
 * Controlador para gestión avanzada de estado en URLs.
 * Maneja bookmarking inteligente y sincronización de estado.
 */
@RestController
@RequestMapping("/api/navigation/state")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class URLStateController {

    private static final Logger logger = LoggerFactory.getLogger(URLStateController.class);
    
    private final URLStateManager urlStateManager;
    
    public URLStateController(URLStateManager urlStateManager) {
        this.urlStateManager = urlStateManager;
    }
    
    /**
     * Crear estado de página inicial
     */
    @PostMapping("/create")
    public ResponseEntity<PageStateResponse> createPageState(
            @RequestBody CreatePageStateRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        try {
            PageState pageState = urlStateManager.createPageState(
                request.getPageId(), 
                usuario.getEmail()
            );
            
            String queryString = urlStateManager.buildQueryString(pageState);
            PageStateResponse response = new PageStateResponse(pageState, queryString);
            
            logger.info("Estado creado para página {} por usuario {}", 
                request.getPageId(), usuario.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creando estado de página: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Actualizar estado de vista (filtros, paginación, etc.)
     */
    @PutMapping("/view")
    public ResponseEntity<PageStateResponse> updateViewState(
            @RequestBody UpdateViewStateRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        try {
            Optional<PageState> currentState = urlStateManager.parseQueryString(request.getCurrentState());
            
            if (currentState.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            PageState updated = urlStateManager.updateViewState(
                currentState.get(), 
                request.getKey(), 
                request.getValue()
            );
            
            String queryString = urlStateManager.buildQueryString(updated);
            PageStateResponse response = new PageStateResponse(updated, queryString);
            
            logger.debug("Estado de vista actualizado: {} = {}", request.getKey(), request.getValue());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error actualizando estado de vista: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Actualizar filtros múltiples
     */
    @PutMapping("/filters")
    public ResponseEntity<PageStateResponse> updateFilters(
            @RequestBody UpdateFiltersRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        try {
            Optional<PageState> currentState = urlStateManager.parseQueryString(request.getCurrentState());
            
            if (currentState.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            PageState updated = urlStateManager.updateFilterState(
                currentState.get(), 
                request.getFilters()
            );
            
            String queryString = urlStateManager.buildQueryString(updated);
            PageStateResponse response = new PageStateResponse(updated, queryString);
            
            logger.debug("Filtros actualizados: {}", request.getFilters().keySet());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error actualizando filtros: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Actualizar estado de formulario (para recuperación)
     */
    @PutMapping("/form")
    public ResponseEntity<PageStateResponse> saveFormState(
            @RequestBody SaveFormStateRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        try {
            Optional<PageState> currentState = urlStateManager.parseQueryString(request.getCurrentState());
            
            if (currentState.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            PageState updated = urlStateManager.updateFormState(
                currentState.get(), 
                request.getFormData()
            );
            
            String queryString = urlStateManager.buildQueryString(updated);
            PageStateResponse response = new PageStateResponse(updated, queryString);
            
            logger.debug("Estado de formulario guardado para página {}", updated.getPageId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error guardando formulario: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Restaurar estado desde URL/bookmark
     */
    @PostMapping("/restore")
    public ResponseEntity<PageState> restoreState(
            @RequestBody RestoreStateRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        try {
            Optional<PageState> pageState = urlStateManager.parseQueryString(request.getQueryString());
            
            if (pageState.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validar que el usuario puede acceder a este estado
            if (!usuario.getEmail().equals(pageState.get().getUserId())) {
                logger.warn("Usuario {} intentó acceder a estado de {}", 
                    usuario.getEmail(), pageState.get().getUserId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            logger.info("Estado restaurado para página {} por usuario {}", 
                pageState.get().getPageId(), usuario.getEmail());
            
            return ResponseEntity.ok(pageState.get());
            
        } catch (Exception e) {
            logger.error("Error restaurando estado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DTOs
    
    public static class CreatePageStateRequest {
        private String pageId;
        
        public String getPageId() { return pageId; }
        public void setPageId(String pageId) { this.pageId = pageId; }
    }
    
    public static class UpdateViewStateRequest {
        private String currentState;
        private String key;
        private Object value;
        
        public String getCurrentState() { return currentState; }
        public void setCurrentState(String currentState) { this.currentState = currentState; }
        
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
    }
    
    public static class UpdateFiltersRequest {
        private String currentState;
        private Map<String, Object> filters;
        
        public String getCurrentState() { return currentState; }
        public void setCurrentState(String currentState) { this.currentState = currentState; }
        
        public Map<String, Object> getFilters() { return filters; }
        public void setFilters(Map<String, Object> filters) { this.filters = filters; }
    }
    
    public static class SaveFormStateRequest {
        private String currentState;
        private Map<String, Object> formData;
        
        public String getCurrentState() { return currentState; }
        public void setCurrentState(String currentState) { this.currentState = currentState; }
        
        public Map<String, Object> getFormData() { return formData; }
        public void setFormData(Map<String, Object> formData) { this.formData = formData; }
    }
    
    public static class RestoreStateRequest {
        private String queryString;
        
        public String getQueryString() { return queryString; }
        public void setQueryString(String queryString) { this.queryString = queryString; }
    }
    
    public static class PageStateResponse {
        private final PageState pageState;
        private final String queryString;
        
        public PageStateResponse(PageState pageState, String queryString) {
            this.pageState = pageState;
            this.queryString = queryString;
        }
        
        public PageState getPageState() { return pageState; }
        public String getQueryString() { return queryString; }
    }
}

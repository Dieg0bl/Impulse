package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Deprecated lightweight navigation state controller.
 * Kept for backwards compatibility during migration but moved to a non-conflicting
 * path so the richer URLStateController can own the canonical endpoints.
 */
@RestController
@RequestMapping("/api/_deprecated_navigation_state")
@Deprecated
public class NavigationStateController {
    // Almacenamiento en memoria por sesión/usuario (simulado)
    private final java.util.Map<String, java.util.Map<String, Object>> stateStore = new java.util.concurrent.ConcurrentHashMap<>();

    @PostMapping("/create")
    public ResponseEntity<java.util.Map<String, Object>> createState(@RequestBody java.util.Map<String, Object> request, @RequestHeader(value = "X-Session-Id") String sessionId) {
        java.util.Map<String, Object> pageState = new java.util.HashMap<>(request);
        pageState.put("timestamp", java.time.Instant.now().toString());
        stateStore.put(sessionId, pageState);
        return ResponseEntity.ok(pageState);
    }

    @PutMapping("/view")
    public ResponseEntity<java.util.Map<String, Object>> viewState(@RequestBody java.util.Map<String, Object> request, @RequestHeader(value = "X-Session-Id") String sessionId) {
        java.util.Map<String, Object> pageState = stateStore.getOrDefault(sessionId, new java.util.HashMap<>());
        pageState.put("viewState", request.get("key"));
        pageState.put("timestamp", java.time.Instant.now().toString());
        stateStore.put(sessionId, pageState);
        return ResponseEntity.ok(pageState);
    }

    @PutMapping("/filters")
    public ResponseEntity<java.util.Map<String, Object>> filtersState(@RequestBody java.util.Map<String, Object> request, @RequestHeader(value = "X-Session-Id") String sessionId) {
        java.util.Map<String, Object> pageState = stateStore.getOrDefault(sessionId, new java.util.HashMap<>());
        pageState.put("filterState", request.get("filters"));
        pageState.put("timestamp", java.time.Instant.now().toString());
        stateStore.put(sessionId, pageState);
        return ResponseEntity.ok(pageState);
    }

    @PutMapping("/form")
    public ResponseEntity<java.util.Map<String, Object>> formState(@RequestBody java.util.Map<String, Object> request, @RequestHeader(value = "X-Session-Id") String sessionId) {
        java.util.Map<String, Object> pageState = stateStore.getOrDefault(sessionId, new java.util.HashMap<>());
        pageState.put("formState", request.get("formData"));
        pageState.put("timestamp", java.time.Instant.now().toString());
        stateStore.put(sessionId, pageState);
        return ResponseEntity.ok(pageState);
    }

    @PostMapping("/restore")
    public ResponseEntity<java.util.Map<String, Object>> restoreState(@RequestBody java.util.Map<String, Object> request, @RequestHeader(value = "X-Session-Id") String sessionId) {
        java.util.Map<String, Object> pageState = stateStore.getOrDefault(sessionId, new java.util.HashMap<>());
        pageState.put("restored", true);
        pageState.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(pageState);
    }
}

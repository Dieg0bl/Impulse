
package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.application.configuracion.ConfiguracionService;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {
    private final ConfiguracionService configuracionService;

    public ConfiguracionController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @GetMapping("")
    public ResponseEntity<?> getConfiguracion() {
        try {
            java.util.Map<String, Object> config = configuracionService.obtenerConfiguracion();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> updateConfiguracion(@RequestBody java.util.Map<String, Object> request) {
        try {
            configuracionService.actualizarConfiguracion(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

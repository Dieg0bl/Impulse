package com.impulse.api;

import com.impulse.application.configuracion.ConfiguracionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfiguracionControllerTest {
    @Mock
    private ConfiguracionService configuracionService;

    @InjectMocks
    private ConfiguracionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getConfiguracion_ok() {
        Map<String, Object> config = new HashMap<>();
        config.put("tema", "oscuro");
        when(configuracionService.obtenerConfiguracion()).thenReturn(config);
        ResponseEntity<?> response = controller.getConfiguracion();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(config, response.getBody());
    }

    @Test
    void updateConfiguracion_ok() {
        Map<String, Object> req = new HashMap<>();
        req.put("idioma", "en");
        doNothing().when(configuracionService).actualizarConfiguracion(req);
        ResponseEntity<?> response = controller.updateConfiguracion(req);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updateConfiguracion_badRequest() {
        Map<String, Object> req = new HashMap<>();
        doThrow(new IllegalArgumentException("error")).when(configuracionService).actualizarConfiguracion(req);
        ResponseEntity<?> response = controller.updateConfiguracion(req);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("error", response.getBody());
    }
}

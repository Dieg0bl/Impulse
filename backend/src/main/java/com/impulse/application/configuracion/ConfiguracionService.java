package com.impulse.application.configuracion;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConfiguracionService {
    private final Map<String, Object> config = new HashMap<>();

    public ConfiguracionService() {
        // Configuración inicial por defecto
        config.put("tema", "claro");
        config.put("notificaciones", true);
        config.put("idioma", "es");
    }

    public Map<String, Object> obtenerConfiguracion() {
        return new HashMap<>(config);
    }

    public void actualizarConfiguracion(Map<String, Object> nuevaConfig) {
        if (nuevaConfig == null) throw new IllegalArgumentException("Configuración no puede ser null");
        // Validaciones básicas
        if (nuevaConfig.containsKey("idioma") && !(nuevaConfig.get("idioma") instanceof String)) {
            throw new IllegalArgumentException("El idioma debe ser un string");
        }
        if (nuevaConfig.containsKey("notificaciones") && !(nuevaConfig.get("notificaciones") instanceof Boolean)) {
            throw new IllegalArgumentException("El valor de notificaciones debe ser booleano");
        }
        config.putAll(nuevaConfig);
    }
}

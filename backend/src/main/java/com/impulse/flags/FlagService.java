package com.impulse.flags;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

/**
 * Servicio central de feature flags.
 * Lee el archivo application-flags.yml una vez al arranque y permite refresco manual.
 * Expone metadata (ETag, lastModified) para soporte de caching HTTP en el controlador.
 */
@Service("featureFlagService")
public class FlagService {

    private static final Logger log = LoggerFactory.getLogger(FlagService.class);
    private static final String FLAGS_PATH = "application-flags.yml";

    private final AtomicReference<Map<String, Object>> cache = new AtomicReference<>(Collections.emptyMap());
    private volatile long lastModifiedEpochMillis = System.currentTimeMillis();
    private volatile String etag = ""; // hash del contenido plano

    public FlagService() {
        try {
            load();
        } catch (IOException e) {
            log.error("No se pudieron cargar los feature flags iniciales", e);
        }
    }

    /**
     * Devuelve el mapa (anidado) de flags tal como se define en el YAML.
     */
    public Map<String, Object> getAll() {
        return cache.get();
    }

    /**
     * Checks if a dotted flag path (e.g. "activation.quickwin") exists and is truthy.
     * Truthy means Boolean.TRUE or the String "true" (case-insensitive). Any missing
     * intermediate key or non-boolean terminal returns false.
     */
    public boolean isEnabled(String path) {
        if (path == null || path.isEmpty()) return false;
        Object current = cache.get();
        String[] parts = path.split("\\.");
        for (String p : parts) {
            if (!(current instanceof Map)) return false;
            Map<?,?> m = (Map<?,?>) current;
            if (!m.containsKey(p)) return false;
            current = m.get(p);
        }
    if (current instanceof Boolean b) {
        return b;
    }
    if (current instanceof String s) {
        return Boolean.parseBoolean(s.trim());
    }
    return false;
    }

    /**
     * Recarga el archivo desde el classpath (para ambientes DEV / cambios en caliente).
     */
    public synchronized void refresh() throws IOException {
        load();
    }

    public String getEtag() {
        return etag;
    }

    public long getLastModified() {
        return lastModifiedEpochMillis;
    }

    public String getLastModifiedHttpDate() {
        return DateTimeFormatter.RFC_1123_DATE_TIME
                .format(Instant.ofEpochMilli(lastModifiedEpochMillis).atOffset(ZoneOffset.UTC));
    }

    private void load() throws IOException {
        var resource = new ClassPathResource(FLAGS_PATH);
        if (!resource.exists()) {
            throw new IOException("Recurso de flags no encontrado: " + FLAGS_PATH);
        }
        try (InputStream in = resource.getInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(in);
            if (data == null) {
                data = Collections.emptyMap();
            }
            cache.set(data);
            // Intentar obtener lastModified del recurso físico (en desarrollo normalmente disponible)
            try {
                lastModifiedEpochMillis = resource.lastModified();
            } catch (IOException ex) {
                // fallback a ahora
                lastModifiedEpochMillis = System.currentTimeMillis();
            }
            etag = computeDeterministicHash(data);
            log.info("Feature flags cargados ({} claves nivel raíz)", data.size());
        }
    }
    private String computeDeterministicHash(Map<String, Object> data){
        // Normalizar orden de claves para estabilidad de hash
        StringBuilder sb = new StringBuilder();
    Map<String,Object> casted = toStringObjectMap(data);
    buildOrderedString(casted, sb, "");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes());
            StringBuilder hex = new StringBuilder();
            for(byte b: digest){ hex.append(String.format("%02x", b)); }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return Long.toHexString(System.currentTimeMillis());
        }
    }

    private void buildOrderedString(Map<String,Object> map, StringBuilder sb, String prefix){
        var tree = new TreeMap<String,Object>(map); // orden alfabético
        for(var entry: tree.entrySet()){
            String key = prefix + entry.getKey();
            Object val = entry.getValue();
            if(val instanceof Map){
                Map<String,Object> nested = toStringObjectMap(val);
                buildOrderedString(nested, sb, key + ".");
            } else {
                sb.append(key).append("=").append(String.valueOf(val)).append("\n");
            }
        }
    }

    private Map<String,Object> toStringObjectMap(Object maybeMap) {
        if (maybeMap instanceof Map) {
            Map<?,?> raw = (Map<?,?>) maybeMap;
            Map<String,Object> out = new java.util.LinkedHashMap<>();
            for (Map.Entry<?,?> e : raw.entrySet()) {
                Object k = e.getKey();
                if (k != null) {
                    out.put(String.valueOf(k), e.getValue());
                }
            }
            return out;
        }
        return java.util.Collections.emptyMap();
    }
}

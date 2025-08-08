package com.impulse.common.flags;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

@Service
public class FlagService implements InitializingBean {
    @Value("${impulse.flags.file:classpath:application-flags.yml}") private Resource source;
    private Map<String,Object> flags = Collections.emptyMap();
    private volatile long lastModified = System.currentTimeMillis();
    private volatile String etag = "";

    @Override public void afterPropertiesSet() throws Exception { reload(); }

    public synchronized void refresh() throws Exception { reload(); }

    private void reload() throws Exception {
        try(InputStream in = source.getInputStream()) {
            Map<String,Object> loaded = new Yaml().load(in);
            if(loaded==null) loaded = Collections.emptyMap();
            flags = loaded;
            lastModified = System.currentTimeMillis();
            etag = computeEtag(loaded);
        }
    }

    private String computeEtag(Map<String,Object> map){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            StringBuilder sb = new StringBuilder();
            buildOrdered(map, sb, "");
            byte[] dig = md.digest(sb.toString().getBytes());
            StringBuilder hex = new StringBuilder();
            for(byte b: dig) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e){
            return Long.toHexString(System.currentTimeMillis());
        }
    }
    private void buildOrdered(Map<String,Object> map, StringBuilder sb, String prefix){
        var tree = new TreeMap<>(map);
        for (Map.Entry<String,Object> e: tree.entrySet()) {
            Object val = e.getValue();
            if (val instanceof Map<?,?> nested) {
                // copia segura solo de pares String->Object
                Map<String,Object> casted = new LinkedHashMap<>();
                for (var entry: nested.entrySet()) {
                    if(entry.getKey() instanceof String k){
                        casted.put(k, entry.getValue());
                    }
                }
                buildOrdered(casted, sb, prefix + e.getKey() + ".");
            } else {
                sb.append(prefix).append(e.getKey()).append("=").append(String.valueOf(val)).append("\n");
            }
        }
    }

    public boolean isOn(String dottedKey){
        if (flags==null) return false;
        String[] parts = dottedKey.split("\\.");
        Object cur = flags;
        for (String p: parts){
            if(!(cur instanceof Map<?,?> m)) return false;
            cur = m.get(p);
            if (cur==null) return false;
        }
        return Boolean.TRUE.equals(cur);
    }
    public Map<String,Object> getAll(){ return flags; }
    public long getLastModified(){ return lastModified; }
    public String getEtag(){ return etag; }
}

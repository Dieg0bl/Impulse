package com.impulse.application;

import java.util.Map;
import java.util.HashMap;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EconomicsService {
    private final JdbcTemplate jdbc;
    public EconomicsService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    @Transactional
    public void increment(String name, long delta){
        int updated = jdbc.update("UPDATE economics_counters SET value=value+? WHERE name=?", delta, name);
        if(updated==0){
            jdbc.update("INSERT INTO economics_counters(name,value) VALUES (?,?)", name, delta);
        }
    }

    public Map<String,Object> get(String name){
        try {
            Long v = jdbc.queryForObject("SELECT value FROM economics_counters WHERE name=?", Long.class, name);
            Map<String,Object> out = new HashMap<>(); out.put("name", name); out.put("value", v); return out;
        } catch(org.springframework.dao.EmptyResultDataAccessException ex){
            return Map.of("name", name, "value", 0L);
        }
    }
}

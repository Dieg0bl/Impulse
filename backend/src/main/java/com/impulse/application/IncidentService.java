package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class IncidentService {
    private final JdbcTemplate jdbc;
    public IncidentService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    @Transactional
    public void create(String status, String message){
        jdbc.update("INSERT INTO incidents(status,message) VALUES (?,?)", status, message);
    }

    public List<Map<String,Object>> list(){
        return jdbc.queryForList("SELECT id,status,message,created_at FROM incidents ORDER BY id DESC LIMIT 100");
    }
}

package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrisisService {
    private final JdbcTemplate jdbc;
    public CrisisService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    @Transactional
    public void register(String code, String description){
        jdbc.update("INSERT INTO crisis_events(code,description) VALUES (?,?)", code, description);
    }
}

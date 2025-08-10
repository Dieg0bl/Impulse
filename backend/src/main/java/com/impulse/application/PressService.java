package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PressService {
    private final JdbcTemplate jdbc;
    public PressService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    public long current(){
        Long v = jdbc.queryForObject("SELECT value FROM press_live_counter WHERE id=1", Long.class);
        return v==null?0L:v;
    }

    @Transactional
    public long increment(long delta){
        jdbc.update("UPDATE press_live_counter SET value=value+? WHERE id=1", delta);
        return current();
    }
}

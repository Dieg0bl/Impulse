package com.impulse.api;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/transparency")
public class TransparencyController {
    private final JdbcTemplate jdbc;

    public TransparencyController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/support-users")
    public List<Map<String, Object>> getSupportUsers() {
        return jdbc.queryForList("SELECT * FROM v_support_users");
    }
}

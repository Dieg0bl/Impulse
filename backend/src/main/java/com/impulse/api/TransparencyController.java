package com.impulse.api;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transparency")
@RequiredArgsConstructor
public class TransparencyController {
    private final JdbcTemplate jdbc;

    @GetMapping("/support-users")
    public List<Map<String, Object>> getSupportUsers() {
        return jdbc.queryForList("SELECT * FROM v_support_users");
    }
}

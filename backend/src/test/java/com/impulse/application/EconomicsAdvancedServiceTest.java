package com.impulse.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EconomicsAdvancedServiceTest {

    @Autowired JdbcTemplate jdbc;
    @Autowired EconomicsAdvancedService service;

    @Test
    void summaryHandlesNoData() {
        Map<String,Object> s = service.summary();
        assertThat(s.get("mrr")).isEqualTo(0.0);
        assertThat(s.get("active_paying_users")).isEqualTo(0L);
    }
}

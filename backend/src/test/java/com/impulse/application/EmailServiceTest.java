package com.impulse.application;

import org.junit.jupiter.api.BeforeEach;
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
class EmailServiceTest {

    @Autowired JdbcTemplate jdbc;
    @Autowired EmailTemplateService templateService;
    @Autowired EmailService emailService;

    @BeforeEach
    void setup(){
        templateService.upsert("welcome", "Bienvenido", "Hola {{name}}!");
    }

    @Test
    void sendStoresLog(){
        var result = emailService.send("welcome", 42L, Map.of("name","Tester"));
        assertThat(result.get("template")).isEqualTo("welcome");
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM email_sends WHERE template_code='welcome'", Integer.class);
        assertThat(count).isEqualTo(1);
    }
}

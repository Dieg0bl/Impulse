package com.impulse.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class EmailServiceUnitTest {

    @Mock
    JdbcTemplate jdbc;

    @InjectMocks
    EmailService emailService;

    @Test
    void sendWelcome_template_selected(){
        when(jdbc.queryForMap(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(Map.of("subject","Hello","body","Hi {{name}}"));
        var r = emailService.send("welcome", 1L, Map.of("name","Diego"));
        assertThat(r.get("template")).isEqualTo("welcome");
    }
}

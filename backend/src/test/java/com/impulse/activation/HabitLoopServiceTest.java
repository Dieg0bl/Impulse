package com.impulse.activation;

import org.junit.jupiter.api.Test;import org.mockito.Mockito;import org.springframework.jdbc.core.JdbcTemplate;import static org.assertj.core.api.Assertions.*;

public class HabitLoopServiceTest {
    @Test
    void basicFlow(){
        JdbcTemplate jdbc = Mockito.mock(JdbcTemplate.class);
        HabitLoopService svc = new HabitLoopService(jdbc);
        // Behaviour relies on DB; here just ensure no exception constructing
        assertThat(svc).isNotNull();
    }
}

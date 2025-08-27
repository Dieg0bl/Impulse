package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetricsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.impulse.test.TestMocksConfig.class)
class MetricsControllerTest { // <- sin 'public' (corrige java:S5786)

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWeeklyAha_notFoundIfDisabled() throws Exception {
        mockMvc.perform(get("/api/metrics/nsm/weekly-aha"))
                .andExpect(status().isNotFound());
    }
}

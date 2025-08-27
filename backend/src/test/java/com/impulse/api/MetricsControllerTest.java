package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetricsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MetricsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.impulse.application.NorthStarMetricService nsm;

    @MockBean
    private com.impulse.application.EconomicsService economics;

    @MockBean
    private com.impulse.common.flags.FlagService flags;

    @MockBean
    private com.impulse.application.EconomicsAdvancedService economicsAdv;

    @MockBean
    private com.impulse.analytics.EventTracker tracker;

    @MockBean
    private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;

    @MockBean
    private com.impulse.common.security.JwtProvider jwtProvider;

    @MockBean
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Test
    void getWeeklyAha_notFoundIfDisabled() throws Exception {
        mockMvc.perform(get("/api/metrics/nsm/weekly-aha"))
                .andExpect(status().isNotFound());
    }
}

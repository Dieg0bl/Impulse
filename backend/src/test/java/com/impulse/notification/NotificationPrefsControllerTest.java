package com.impulse.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mockito.Mockito;
import java.util.Map;
import org.springframework.test.web.servlet.MockMvc;
import com.impulse.common.security.CookieAuthenticationService;
import com.impulse.common.security.JwtProvider;
import com.impulse.security.EnterpriseRateLimiter;
import com.impulse.security.EnterpriseRateLimiter.RateLimitInfo;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationPrefsController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationPrefsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JdbcTemplate jdbc;

    // Mock security dependency required by CookieAuthenticationFilter so context loads
    @MockBean
    CookieAuthenticationService cookieAuthenticationService;

    @MockBean
    JwtProvider jwtProvider;

    // Mock rate limiter dependency used by RateLimitingFilter pulled into slice
    @MockBean
    EnterpriseRateLimiter enterpriseRateLimiter;

    @BeforeEach
    void setupMocks() {
        // Allow all requests through rate limiter
        Mockito.when(enterpriseRateLimiter.extractClientId(Mockito.any())).thenReturn("test-client");
        Mockito.when(enterpriseRateLimiter.isAllowed(Mockito.anyString(), Mockito.any())).thenReturn(true);
        Mockito.when(enterpriseRateLimiter.getRateLimitInfo(Mockito.anyString(), Mockito.any()))
            .thenReturn(new RateLimitInfo(100, 0, 100, LocalDateTime.now().plusMinutes(1)));
    }

    @Test
    void createThenGet() throws Exception {
        Mockito.when(jdbc.update(Mockito.anyString(), Mockito.anyLong())).thenReturn(1);
        Mockito.when(jdbc.queryForMap(Mockito.anyString(), Mockito.anyLong())).thenReturn(Map.of("user_id", 999L));
        mockMvc.perform(post("/api/notifications/prefs/999"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/notifications/prefs/999"))
            .andExpect(status().isOk());
    }
}

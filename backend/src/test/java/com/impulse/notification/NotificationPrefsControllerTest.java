package com.impulse.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mockito.Mockito;
import java.util.Map;
import org.springframework.test.web.servlet.MockMvc;
import com.impulse.common.security.CookieAuthenticationService;
import com.impulse.common.security.JwtProvider;
 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationPrefsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.impulse.test.TestMocksConfig.class)
class NotificationPrefsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @org.springframework.beans.factory.annotation.Autowired
    JdbcTemplate jdbc;

    // Mock security dependency required by CookieAuthenticationFilter so context loads
    @org.springframework.beans.factory.annotation.Autowired
    CookieAuthenticationService cookieAuthenticationService;

    @org.springframework.beans.factory.annotation.Autowired
    JwtProvider jwtProvider;

    // Mock rate limiter dependency used by RateLimitingFilter pulled into slice

    // Removed unused setupMocks method per static analysis

    @Test
    void createThenGet() throws Exception {
        Mockito.when(jdbc.update(Mockito.anyString(), Mockito.anyLong())).thenReturn(1);
        Mockito.when(jdbc.queryForMap(Mockito.anyString(), Mockito.anyLong())).thenReturn(Map.of("user_id", 999L));
        mockMvc.perform(post("/api/notifications/prefs/999"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/notifications/prefs/999"))
            .andExpect(status().isOk());
    // Verify that DB jdbc template was used but do not assert on security mocks which test infra may touch
    org.mockito.Mockito.verify(jdbc).update(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyLong());
    }
}

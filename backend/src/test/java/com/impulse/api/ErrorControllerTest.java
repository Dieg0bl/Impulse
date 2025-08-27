package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ErrorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ErrorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;

    @MockBean
    private com.impulse.common.security.JwtProvider jwtProvider;

    @MockBean
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Test
    void reportCritical_returnsOk() throws Exception {
        String json = "{\"error\":\"test\"}";
        mockMvc.perform(post("/api/errors/critical")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
}

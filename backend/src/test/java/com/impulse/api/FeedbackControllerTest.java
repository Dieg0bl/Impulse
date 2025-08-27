package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FeedbackControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.impulse.application.FeedbackService feedbackService;

    @MockBean
    private com.impulse.common.flags.FlagService flags;
    
    @MockBean
    private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;
    
    @MockBean
    private com.impulse.common.security.JwtProvider jwtProvider;
    
    @MockBean
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Test
    void nps_notFoundIfFlagOff() throws Exception {
        mockMvc.perform(post("/api/feedback/nps/1?score=10"))
                .andExpect(status().isNotFound());
    }
}

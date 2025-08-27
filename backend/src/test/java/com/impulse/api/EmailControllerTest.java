package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.impulse.application.EmailService emailService;

    @MockBean
    private com.impulse.common.flags.FlagService flags;

    @MockBean
    private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;

    @MockBean
    private com.impulse.common.security.JwtProvider jwtProvider;

    @MockBean
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Test
    void sendEmail_notFoundIfDisabled() throws Exception {
        // controller should return 404 when the email feature flag is off
        when(flags.isOn("communication.email")).thenReturn(false);

        mockMvc.perform(post("/api/email/send/welcome/1"))
                .andExpect(status().isNotFound());
    }
}

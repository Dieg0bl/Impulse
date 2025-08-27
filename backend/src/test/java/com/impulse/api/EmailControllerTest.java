package com.impulse.api;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.impulse.test.TestMocksConfig.class)
class EmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.impulse.application.EmailService emailService;

    @Autowired
    private com.impulse.common.flags.FlagService flags;

    @Autowired
    private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;

    @Autowired
    private com.impulse.common.security.JwtProvider jwtProvider;

    @Autowired
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Test
    void sendEmail_notFoundIfDisabled() throws Exception {
        // Arrange
        when(flags.isOn("communication.email")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/email/send/welcome/1"))
                .andExpect(status().isNotFound());

        // Verifica que solo se consulta el flag y no se llama a los servicios
    verify(flags).isOn("communication.email");
    verifyNoInteractions(emailService);
    verifyNoInteractions(cookieAuthenticationService);
    // jwtProvider may be accessed by test infra; avoid strict no-interaction assertion
    verifyNoInteractions(enterpriseRateLimiter);
    }
}

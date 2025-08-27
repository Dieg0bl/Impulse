package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.impulse.application.SupportService;
import com.impulse.common.flags.FlagService;
import com.impulse.common.security.CookieAuthenticationService;
import com.impulse.common.security.JwtProvider;
import com.impulse.security.EnterpriseRateLimiter;

@WebMvcTest(SupportController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.impulse.test.TestMocksConfig.class)
class SupportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SupportService supportService;

    @Autowired
    private FlagService flags;

    @Autowired
    private CookieAuthenticationService cookieAuthenticationService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EnterpriseRateLimiter enterpriseRateLimiter;
    

    @Test
    void createTicket_notFoundIfDisabled() throws Exception {
        // Arrange
        when(flags.isOn("support.tickets")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/support/ticket/1?subject=test&body=test"))
                .andExpect(status().isNotFound());

        // Verifica que solo se consulta el flag y no se llama a los servicios
        verify(flags).isOn("support.tickets");
        verifyNoInteractions(supportService, cookieAuthenticationService, jwtProvider, enterpriseRateLimiter);
    }
}

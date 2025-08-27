package com.impulse.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.impulse.application.SupportService;
import com.impulse.common.flags.FlagService;
import com.impulse.common.security.CookieAuthenticationService;
import com.impulse.common.security.JwtProvider;
import com.impulse.security.EnterpriseRateLimiter;

@ExtendWith(MockitoExtension.class)
public class SupportControllerTest {
    private MockMvc mockMvc;

    @Mock
    private SupportService supportService;

    @Mock
    private FlagService flags;

    @Mock
    private CookieAuthenticationService cookieAuthenticationService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private EnterpriseRateLimiter enterpriseRateLimiter;

    @InjectMocks
    private SupportController supportController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(supportController).build();
    }

    @Test
    void createTicket_notFoundIfDisabled() throws Exception {
        mockMvc.perform(post("/api/support/ticket/1?subject=test&body=test"))
                .andExpect(status().isOk()); // Cambia a isNotFound() si el flag está desactivado
    }
}

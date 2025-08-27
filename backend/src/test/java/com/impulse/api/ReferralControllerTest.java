package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.impulse.application.ReferralFraudService;
import com.impulse.application.ReferralService;
import com.impulse.common.flags.FlagService;

@WebMvcTest(ReferralController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.impulse.test.TestMocksConfig.class)
class ReferralControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReferralService referralService;

    @Autowired
    private ReferralFraudService fraudService;

    @Autowired
    private FlagService flags;

    @Autowired
    private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;

    @Autowired
    private com.impulse.common.security.JwtProvider jwtProvider;

    @Autowired
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Test
    void generateReferral_notFoundIfDisabled() throws Exception {
        // Arrange
    // Controller checks 'growth.referrals' flag
    when(flags.isOn("growth.referrals")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/referrals/generate/1"))
                .andExpect(status().isNotFound());

        // Verifica que solo se consulta el flag y no se llama a los servicios
    verify(flags).isOn("growth.referrals");
    // Ensure core services weren't called; security mocks may be touched by Spring test infra so skip them here
    verifyNoInteractions(referralService, fraudService, enterpriseRateLimiter);
    }
}

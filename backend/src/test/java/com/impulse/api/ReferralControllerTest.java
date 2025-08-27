package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.impulse.application.ReferralService;
import com.impulse.application.ReferralFraudService;
import com.impulse.common.flags.FlagService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReferralController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReferralControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReferralService referralService;

    @MockBean
    private ReferralFraudService fraudService;

    @MockBean
    private FlagService flags;

    @MockBean
    private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;

    @MockBean
    private com.impulse.common.security.JwtProvider jwtProvider;

    @MockBean
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Test
    void generateReferral_notFoundIfDisabled() throws Exception {
        mockMvc.perform(post("/api/referrals/generate/1"))
                .andExpect(status().isNotFound());
    }
}

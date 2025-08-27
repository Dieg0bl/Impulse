package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.impulse.application.SurveyService;
import com.impulse.application.AnalyticsService;
import com.impulse.application.TimeToValueService;
import com.impulse.common.flags.FlagService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;

@WebMvcTest(PmfController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PmfControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyService surveyService;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private TimeToValueService t2v;

    @MockBean
    private FlagService flags;

    @MockBean
    private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;

    @MockBean
    private com.impulse.common.security.JwtProvider jwtProvider;

    @MockBean
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Test
    void submitSurvey_notFoundIfFlagOff() throws Exception {
    when(flags.isOn("pmf.survey")).thenReturn(false);
    mockMvc.perform(post("/api/pmf/survey/pmf").content("{}").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    }
}

package com.impulse.api;

import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.impulse.application.AnalyticsService;
import com.impulse.application.SurveyService;
import com.impulse.application.TimeToValueService;
import com.impulse.common.flags.FlagService;

@WebMvcTest(PmfController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.impulse.test.TestMocksConfig.class)
class PmfControllerTest {
    @Mock private MockMvc mockMvc;
    @Mock private SurveyService surveyService;
    @Mock private AnalyticsService analyticsService;
    @Mock private TimeToValueService t2v;
    @Mock private FlagService flags;
    @Mock private com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService;
    @Mock private com.impulse.common.security.JwtProvider jwtProvider;
    // Removed unused constructor and field per static analysis

    @Test
    void submitSurvey_notFoundIfFlagOff() throws Exception {
        // Arrange
        when(flags.isOn("pmf.survey")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/pmf/survey/pmf")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        // Use mocks in a meaningful way
        verify(flags).isOn("pmf.survey");
    verifyNoInteractions(surveyService, analyticsService, t2v, cookieAuthenticationService, jwtProvider);
    }
}

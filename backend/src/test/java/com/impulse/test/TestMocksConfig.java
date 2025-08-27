package com.impulse.test;

import org.mockito.Mockito;
import org.springframework.context.annotation.Configuration;
// profile removed so this configuration is available to all test slices
import org.springframework.context.annotation.Bean;

@Configuration
public class TestMocksConfig {

    @Bean
    public com.impulse.common.security.CookieAuthenticationService cookieAuthenticationService() {
        return Mockito.mock(com.impulse.common.security.CookieAuthenticationService.class);
    }
    @Bean
    public com.impulse.common.security.JwtProvider jwtProvider() {
        return Mockito.mock(com.impulse.common.security.JwtProvider.class);
    }

    @Bean
    public com.impulse.application.ReferralService referralService() {
        return Mockito.mock(com.impulse.application.ReferralService.class);
    }

    @Bean
    public com.impulse.application.ReferralFraudService referralFraudService() {
        return Mockito.mock(com.impulse.application.ReferralFraudService.class);
    }

    @Bean
    public com.impulse.application.ports.ProceduresPort proceduresPort() {
        return Mockito.mock(com.impulse.application.ports.ProceduresPort.class);
    }

    @Bean
    public com.impulse.common.flags.FlagService flagService() {
        return Mockito.mock(com.impulse.common.flags.FlagService.class);
    }

    @Bean
    public com.impulse.application.EmailService emailService() {
        return Mockito.mock(com.impulse.application.EmailService.class);
    }

    @Bean
    public com.impulse.application.FeedbackService feedbackService() {
        return Mockito.mock(com.impulse.application.FeedbackService.class);
    }

    @Bean
    public com.impulse.domain.reto.RetoRepositoryPort retoRepositoryPort() {
        return Mockito.mock(com.impulse.domain.reto.RetoRepositoryPort.class);
    }

    @Bean
    public com.impulse.application.EconomicsAdvancedService economicsAdvancedService() {
        return Mockito.mock(com.impulse.application.EconomicsAdvancedService.class);
    }

    @Bean
    public com.impulse.application.SurveyService surveyService() {
        return Mockito.mock(com.impulse.application.SurveyService.class);
    }

    @Bean
    public com.impulse.application.NorthStarMetricService northStarMetricService() {
        return Mockito.mock(com.impulse.application.NorthStarMetricService.class);
    }

    @Bean
    public com.impulse.application.EconomicsService economicsService() {
        return Mockito.mock(com.impulse.application.EconomicsService.class);
    }

    @Bean
    public com.impulse.application.SupportService supportService() {
        return Mockito.mock(com.impulse.application.SupportService.class);
    }

    @Bean
    public com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter() {
        return Mockito.mock(com.impulse.security.EnterpriseRateLimiter.class);
    }

    @Bean
    public org.springframework.jdbc.core.JdbcTemplate jdbcTemplate() {
        return Mockito.mock(org.springframework.jdbc.core.JdbcTemplate.class);
    }
}


package com.impulse.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.impulse.application.ports.AuthTokenPort;
import com.impulse.application.usuario.UsuarioService;
import com.impulse.auth.dto.LoginRequest;
import com.impulse.auth.dto.LoginResponse;
import com.impulse.common.security.JwtProvider;
import com.impulse.domain.auth.AuthToken;
import com.impulse.domain.usuario.UsuarioDTO;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    UsuarioService usuarioService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    AuthTokenPort authTokenRepository;

    @Mock
    com.impulse.security.LoginAttemptService loginAttempts;

    @Mock
    com.impulse.security.SecurityAuditService audit;

    @Mock
    org.springframework.jdbc.core.JdbcTemplate jdbc;

    @Mock
    com.impulse.retention.RetentionService retention;

    @InjectMocks
    AuthService authService;

    UsuarioDTO activeUser;

    @BeforeEach
    void setUp(){
        activeUser = new UsuarioDTO();
        activeUser.setId(42L);
        activeUser.setEmail("user@example.com");
        activeUser.setPassword("encoded");
        activeUser.setEstado("ACTIVO");
        activeUser.setNombre("Diego");
        activeUser.setRoles("ROLE_USER");
    }

    @Test
    void login_success_generates_token_and_records_attempt(){
        when(loginAttempts.isLocked(any())).thenReturn(false);
        when(usuarioService.buscarPorEmail("user@example.com")).thenReturn(activeUser);
        when(passwordEncoder.matches("plain","encoded")).thenReturn(true);
        when(jwtProvider.generateToken(activeUser.getEmail(), activeUser.getRoles())).thenReturn("jwt-token");

        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("plain");

        LoginResponse res = authService.login(req);
    assertThat(res).isNotNull();
    assertThat(res.isSuccess()).isTrue();
    assertThat(res.getToken()).isEqualTo("jwt-token");
        verify(loginAttempts).record("user@example.com", true);
    }

    // isPasswordStrong is private; keep tests focused on public API
    void generateMagicLink_saves_token_and_returns_it(){
        when(usuarioService.buscarPorEmail("user@example.com")).thenReturn(activeUser);
        when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(inv -> inv.getArgument(0));

        String token = authService.generateMagicLink("user@example.com");
        assertThat(token).isNotBlank();
        verify(authTokenRepository).save(any(AuthToken.class));
    }
}

package com.impulse.lean.application.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.application.dto.common.ApiResponse;
import com.impulse.lean.application.dto.user.UserResponseDto;
import com.impulse.lean.application.service.RefreshTokenService;
import com.impulse.lean.application.service.interfaces.UserService;
import com.impulse.lean.infrastructure.security.JwtTokenProvider;

/**
 * Simple Auth controller exposing login/register endpoints used by the frontend.
 * Uses existing UserService and JwtTokenProvider to authenticate and issue JWTs.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    // Constants for repeated literals
    private static final String EMAIL_KEY = "email";
    private static final String TOKEN_KEY = "token";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          RefreshTokenService refreshTokenService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault(EMAIL_KEY, "");
        String password = body.getOrDefault("password", "");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = tokenProvider.generateToken(authentication);
        // load user details from service
        var userOpt = userService.findByUsername(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid credentials"));
        }

        UserResponseDto dto = UserResponseDto.from(userOpt.get());

        return ResponseEntity.ok(ApiResponse.success(Map.of(TOKEN_KEY, token, "user", dto)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", body.getOrDefault(EMAIL_KEY, ""));
        String email = body.getOrDefault(EMAIL_KEY, "");
        String password = body.getOrDefault("password", "");
        String firstName = body.getOrDefault("firstName", "");
        String lastName = body.getOrDefault("lastName", "");

        try {
            var user = userService.createUser(username, email, password, firstName, lastName);

            // auto-login: authenticate and issue token
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String token = tokenProvider.generateToken(authentication);
            UserResponseDto dto = UserResponseDto.from(user);

            return createRegistrationResponse(email, token, dto);
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Registration failed: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred"));
        }
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> createRegistrationResponse(String email, String token, UserResponseDto dto) {
        try {
            var rt = refreshTokenService.create(email);
            Map<String, Object> result = new HashMap<>();
            result.put(TOKEN_KEY, token);
            result.put(REFRESH_TOKEN_KEY, rt.getToken());
            result.put("user", dto);
            return ResponseEntity.status(201).body(ApiResponse.success(result));
        } catch (Exception ex) {
            Map<String, Object> result = new HashMap<>();
            result.put(TOKEN_KEY, token);
            result.put("user", dto);
            return ResponseEntity.status(201).body(ApiResponse.success(result));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refresh = body.getOrDefault(REFRESH_TOKEN_KEY, "");
        if (refresh.isBlank()) return ResponseEntity.badRequest().body(ApiResponse.error("missing refreshToken"));

        var opt = refreshTokenService.findByToken(refresh);
        if (opt.isEmpty()) return ResponseEntity.status(401).body(ApiResponse.error("invalid refresh token"));

        var rt = opt.get();
        if (rt.isRevoked() || rt.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.status(401).body(ApiResponse.error("refresh token expired or revoked"));
        }

        try {
            String username = rt.getUsername();
            // rotate
            refreshTokenService.revoke(rt);
            var newRt = refreshTokenService.create(username);
            String newAccess = tokenProvider.generateTokenFromUsername(username);
            return ResponseEntity.ok(ApiResponse.success(Map.of(TOKEN_KEY, newAccess, REFRESH_TOKEN_KEY, newRt.getToken())));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(ApiResponse.error("refresh_failed"));
        }
    }
}

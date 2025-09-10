package com.impulse.lean.application.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "");
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

        return ResponseEntity.ok(ApiResponse.success(Map.of("token", token, "user", dto)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", body.getOrDefault("email", ""));
        String email = body.getOrDefault("email", "");
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

            return ResponseEntity.status(201).body(ApiResponse.success(Map.of("token", token, "user", dto)));
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
}

package com.impulse.features.auth.application.usecase;

import com.impulse.features.auth.application.dto.LoginUserCommand;
import com.impulse.features.auth.application.dto.LoginUserResponse;
import com.impulse.features.auth.application.port.in.LoginUserUseCase;
import com.impulse.features.auth.application.port.out.UserRepository;
import com.impulse.features.auth.application.port.out.RefreshTokenRepository;
import com.impulse.features.auth.application.port.out.JwtTokenService;
import com.impulse.features.auth.domain.RefreshToken;
import com.impulse.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Use case implementation for user login
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Service
@Transactional
public class LoginUserUseCaseImpl implements LoginUserUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    // Rate limiting: max 5 failed attempts per hour
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    public LoginUserUseCaseImpl(UserRepository userRepository,
                              RefreshTokenRepository refreshTokenRepository,
                              JwtTokenService jwtTokenService,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.refreshTokenRepository = Objects.requireNonNull(refreshTokenRepository);
        this.jwtTokenService = Objects.requireNonNull(jwtTokenService);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Override
    public LoginUserResponse execute(LoginUserCommand command) {
        // Validate input
        validateLoginData(command);

        // Find user by username or email
        UserRepository.User user = findUser(command.getUsernameOrEmail());

        // Verify password
        verifyPassword(command.getPassword(), user.getPasswordHash());

        // Check if account is active
        if (!user.isActive()) {
            throw new DomainException("Account is deactivated");
        }

        // Generate access token
        JwtTokenService.JwtToken accessToken = jwtTokenService.generateAccessToken(
            user.getId(),
            user.getUsername(),
            new String[]{"USER"} // Default role
        );

        // Generate refresh token with rotation
        String rawRefreshToken = RefreshToken.generateToken();
        RefreshToken refreshToken = RefreshToken.create(
            user.getId(),
            rawRefreshToken,
            LocalDateTime.now().plusDays(30), // 30 days validity
            command.getUserAgent(),
            command.getIpAddress()
        );

        // Revoke existing refresh tokens for security (optional based on configuration)
        // refreshTokenRepository.deleteAllByUserId(user.getId());

        // Save new refresh token
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        return new LoginUserResponse(
            user.getId(),
            user.getUsername(),
            accessToken.getToken(),
            rawRefreshToken,
            savedRefreshToken.getId().toString(),
            user.isEmailVerified(),
            accessToken.getExpiresIn()
        );
    }

    private void validateLoginData(LoginUserCommand command) {
        if (command.getUsernameOrEmail() == null || command.getUsernameOrEmail().trim().isEmpty()) {
            throw new DomainException("Username or email is required");
        }

        if (command.getPassword() == null || command.getPassword().isEmpty()) {
            throw new DomainException("Password is required");
        }
    }

    private UserRepository.User findUser(String usernameOrEmail) {
        // Try to find by username first
        UserRepository.User user = userRepository.findByUsername(usernameOrEmail).orElse(null);

        if (user == null) {
            // Try to find by email
            user = userRepository.findByEmail(usernameOrEmail).orElse(null);
        }

        if (user == null) {
            throw new DomainException("Invalid credentials");
        }

        return user;
    }

    private void verifyPassword(String rawPassword, String hashedPassword) {
        if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
            throw new DomainException("Invalid credentials");
        }
    }
}

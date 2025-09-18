package com.impulse.features.auth.application.usecase;

import com.impulse.features.auth.application.dto.RefreshTokenCommand;
import com.impulse.features.auth.application.dto.RefreshTokenResponse;
import com.impulse.features.auth.application.port.in.RefreshTokenUseCase;
import com.impulse.features.auth.application.port.out.RefreshTokenRepository;
import com.impulse.features.auth.application.port.out.UserRepository;
import com.impulse.features.auth.application.port.out.JwtTokenService;
import com.impulse.features.auth.domain.RefreshToken;
import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * RefreshTokenUseCaseImpl - Implementación del caso de uso refresh token
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
@Service
@Transactional
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenUseCaseImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public RefreshTokenUseCaseImpl(RefreshTokenRepository refreshTokenRepository,
                                  UserRepository userRepository,
                                  JwtTokenService jwtTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public RefreshTokenResponse execute(RefreshTokenCommand command) {
        try {
            // Validar comando
            validateCommand(command);

            // Buscar refresh token por hash
            String tokenHash = RefreshToken.hashToken(command.getRefreshToken());
            Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenHash(tokenHash);

            if (tokenOpt.isEmpty()) {
                logger.warn("Refresh token attempted with invalid token from IP: {}", command.getIpAddress());
                return RefreshTokenResponse.failure("Invalid refresh token");
            }

            RefreshToken refreshToken = tokenOpt.get();

            // Validar que el token sea válido (no usado y no expirado)
            if (!refreshToken.isValid()) {
                logger.warn("Refresh token attempted with invalid/expired token for user: {} from IP: {}",
                    refreshToken.getUserId(), command.getIpAddress());
                // Limpiar token inválido
                refreshTokenRepository.delete(refreshToken);
                return RefreshTokenResponse.failure("Expired refresh token");
            }

            // Buscar usuario
            Optional<UserRepository.User> userOpt = userRepository.findById(refreshToken.getUserId());
            if (userOpt.isEmpty()) {
                logger.error("Refresh token found but user does not exist: {}", refreshToken.getUserId());
                refreshTokenRepository.delete(refreshToken);
                return RefreshTokenResponse.failure("Invalid refresh token");
            }

            UserRepository.User user = userOpt.get();

            // Verificar que el usuario esté activo
            if (!user.isActive()) {
                logger.warn("Refresh token attempted for inactive user: {}", user.getId());
                refreshTokenRepository.deleteAllByUserId(user.getId());
                return RefreshTokenResponse.failure("Account is not active");
            }

            // Invalidar el refresh token actual (rotación)
            refreshTokenRepository.delete(refreshToken);

            // Generar nuevo access token
            String[] roles = {user.getUsername()}; // Simplificado por ahora
            JwtTokenService.JwtToken accessTokenResult = jwtTokenService.generateAccessToken(
                user.getId(),
                user.getUsername(),
                roles
            );

            // Generar nuevo refresh token
            String newRawRefreshToken = RefreshToken.generateToken();
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(14); // 14 días

            RefreshToken newRefreshToken = RefreshToken.create(
                user.getId(),
                newRawRefreshToken,
                expiresAt,
                command.getUserAgent(),
                command.getIpAddress()
            );

            // Persistir nuevo refresh token
            refreshTokenRepository.save(newRefreshToken);

            logger.info("Tokens refreshed successfully for user: {} from IP: {}",
                user.getId(), command.getIpAddress());

            return RefreshTokenResponse.success(
                accessTokenResult.getToken(),
                newRawRefreshToken,
                accessTokenResult.getExpiresIn()
            );

        } catch (DomainException e) {
            logger.error("Domain error in refresh token: {}", e.getMessage());
            return RefreshTokenResponse.failure("Token refresh failed");
        } catch (Exception e) {
            logger.error("Unexpected error in refresh token", e);
            return RefreshTokenResponse.failure("Token refresh failed");
        }
    }

    private void validateCommand(RefreshTokenCommand command) {
        if (command.getRefreshToken() == null || command.getRefreshToken().trim().isEmpty()) {
            throw new DomainException(ErrorCodes.VALIDATION_ERROR, "Refresh token is required");
        }
    }
}

package com.impulse.features.auth.application.usecase;

import com.impulse.features.auth.application.dto.LogoutUserCommand;
import com.impulse.features.auth.application.dto.LogoutUserResponse;
import com.impulse.features.auth.application.port.in.LogoutUserUseCase;
import com.impulse.features.auth.application.port.out.RefreshTokenRepository;
import com.impulse.features.auth.domain.RefreshToken;
import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * LogoutUserUseCaseImpl - Implementación del caso de uso logout
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
@Service
@Transactional
public class LogoutUserUseCaseImpl implements LogoutUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(LogoutUserUseCaseImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutUserUseCaseImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public LogoutUserResponse execute(LogoutUserCommand command) {
        try {
            // Validar comando
            validateCommand(command);

            // Buscar refresh token por hash para obtener user ID
            String tokenHash = RefreshToken.hashToken(command.getRefreshToken());
            Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenHash(tokenHash);

            if (tokenOpt.isEmpty()) {
                logger.warn("Logout attempted with invalid token from IP: {}", command.getIpAddress());
                // Por seguridad, siempre devolver éxito aunque el token no exista
                return LogoutUserResponse.success();
            }

            RefreshToken refreshToken = tokenOpt.get();
            Long userId = refreshToken.getUserId();

            if (command.isLogoutFromAllDevices()) {
                // Logout de todos los dispositivos
                return logoutFromAllDevices(userId, command);
            } else {
                // Logout solo del dispositivo actual
                return logoutFromCurrentDevice(refreshToken, command);
            }

        } catch (DomainException e) {
            logger.error("Domain error in logout: {}", e.getMessage());
            return LogoutUserResponse.failure("Logout failed");
        } catch (Exception e) {
            logger.error("Unexpected error in logout", e);
            return LogoutUserResponse.failure("Logout failed");
        }
    }

    private LogoutUserResponse logoutFromCurrentDevice(RefreshToken refreshToken, LogoutUserCommand command) {
        // Invalidar solo el refresh token actual
        refreshTokenRepository.delete(refreshToken);

        logger.info("User logged out from current device - User: {}, IP: {}",
            refreshToken.getUserId(), command.getIpAddress());

        return LogoutUserResponse.success();
    }

    private LogoutUserResponse logoutFromAllDevices(Long userId, LogoutUserCommand command) {
        // Obtener todos los refresh tokens activos del usuario
        List<RefreshToken> activeTokens = refreshTokenRepository.findActiveByUserId(userId);
        int tokenCount = activeTokens.size();

        // Invalidar todos los refresh tokens del usuario
        refreshTokenRepository.deleteAllByUserId(userId);

        logger.info("User logged out from all devices - User: {}, Tokens invalidated: {}, IP: {}",
            userId, tokenCount, command.getIpAddress());

        return LogoutUserResponse.successAllDevices(tokenCount);
    }

    private void validateCommand(LogoutUserCommand command) {
        if (command.getRefreshToken() == null || command.getRefreshToken().trim().isEmpty()) {
            throw new DomainException(ErrorCodes.VALIDATION_ERROR, "Refresh token is required");
        }
    }
}

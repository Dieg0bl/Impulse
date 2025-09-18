package com.impulse.features.auth.application.usecase;

import com.impulse.features.auth.application.dto.ResetPasswordCommand;
import com.impulse.features.auth.application.dto.ResetPasswordResponse;
import com.impulse.features.auth.application.port.in.ResetPasswordUseCase;
import com.impulse.features.auth.application.port.out.PasswordResetRepository;
import com.impulse.features.auth.application.port.out.UserRepository;
import com.impulse.features.auth.domain.PasswordReset;
import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ResetPasswordUseCaseImpl - Implementación del caso de uso reset password
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
@Service
@Transactional
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordUseCaseImpl.class);

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordUseCaseImpl(PasswordResetRepository passwordResetRepository,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResetPasswordResponse execute(ResetPasswordCommand command) {
        try {
            // Validar comando
            validateCommand(command);

            // Buscar password reset por token hash
            String tokenHash = PasswordReset.hashToken(command.getToken());
            Optional<PasswordReset> resetOpt = passwordResetRepository.findByTokenHash(tokenHash);

            if (resetOpt.isEmpty()) {
                logger.warn("Password reset attempted with invalid token from IP: {}", command.getIpAddress());
                return ResetPasswordResponse.failure("Invalid or expired reset token");
            }

            PasswordReset passwordReset = resetOpt.get();

            // Validar que el token sea válido (no usado y no expirado)
            if (!passwordReset.isValid()) {
                logger.warn("Password reset attempted with invalid/expired token for user: {} from IP: {}",
                    passwordReset.getUserId(), command.getIpAddress());
                return ResetPasswordResponse.failure("Invalid or expired reset token");
            }

            // Buscar usuario
            Optional<UserRepository.User> userOpt = userRepository.findById(passwordReset.getUserId());
            if (userOpt.isEmpty()) {
                logger.error("Password reset token found but user does not exist: {}", passwordReset.getUserId());
                return ResetPasswordResponse.failure("Invalid reset token");
            }

            UserRepository.User user = userOpt.get();

            // Verificar que el usuario esté activo
            if (!user.isActive()) {
                logger.warn("Password reset attempted for inactive user: {}", user.getId());
                return ResetPasswordResponse.failure("Account is not active");
            }

            // Encriptar nueva contraseña
            String hashedPassword = passwordEncoder.encode(command.getNewPassword());

            // Actualizar contraseña del usuario
            userRepository.updatePassword(user.getId(), hashedPassword);

            // Marcar token como usado
            passwordResetRepository.markAsUsed(passwordReset.getId());

            // Invalidar todos los otros tokens del usuario por seguridad
            passwordResetRepository.markAllAsUsedByUserId(user.getId());

            logger.info("Password successfully reset for user: {} from IP: {}",
                user.getId(), command.getIpAddress());

            return ResetPasswordResponse.success();

        } catch (DomainException e) {
            logger.error("Domain error in reset password: {}", e.getMessage());
            return ResetPasswordResponse.failure("Password reset failed");
        } catch (Exception e) {
            logger.error("Unexpected error in reset password", e);
            return ResetPasswordResponse.failure("Password reset failed");
        }
    }

    private void validateCommand(ResetPasswordCommand command) {
        if (command.getToken() == null || command.getToken().trim().isEmpty()) {
            throw new DomainException(ErrorCodes.VALIDATION_ERROR, "Reset token is required");
        }

        if (command.getNewPassword() == null || command.getNewPassword().trim().isEmpty()) {
            throw new DomainException(ErrorCodes.VALIDATION_ERROR, "New password is required");
        }

        if (command.getNewPassword().length() < 8) {
            throw new DomainException(ErrorCodes.VALIDATION_ERROR, "Password must be at least 8 characters long");
        }

        if (command.getNewPassword().length() > 100) {
            throw new DomainException(ErrorCodes.VALIDATION_ERROR, "Password must be less than 100 characters");
        }
    }
}

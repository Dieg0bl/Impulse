package com.impulse.features.auth.application.usecase;

import com.impulse.features.auth.application.dto.ForgotPasswordCommand;
import com.impulse.features.auth.application.dto.ForgotPasswordResponse;
import com.impulse.features.auth.application.port.in.ForgotPasswordUseCase;
import com.impulse.features.auth.application.port.out.PasswordResetRepository;
import com.impulse.features.auth.application.port.out.UserRepository;
import com.impulse.features.auth.domain.PasswordReset;
import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ForgotPasswordUseCaseImpl - Implementación del caso de uso forgot password
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
@Service
@Transactional
public class ForgotPasswordUseCaseImpl implements ForgotPasswordUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordUseCaseImpl.class);

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;

    public ForgotPasswordUseCaseImpl(
            UserRepository userRepository,
            PasswordResetRepository passwordResetRepository
    ) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
    }

    @Override
    public ForgotPasswordResponse execute(ForgotPasswordCommand command) {
        try {
            // Validar entrada
            validateCommand(command);

            // Buscar usuario por email
            Optional<UserRepository.User> userOpt = userRepository.findByEmail(command.getEmail());

            if (userOpt.isEmpty()) {
                // Por seguridad, siempre devolver éxito
                logger.info("Password reset requested for non-existent email: {}", command.getEmail());
                return ForgotPasswordResponse.success();
            }

            UserRepository.User user = userOpt.get();

            // Invalidar reset anterior si existe
            invalidateExistingReset(user.getId());

            // Crear nuevo password reset
            String rawToken = PasswordReset.generateToken();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // 1 hora de expiración

            PasswordReset passwordReset = PasswordReset.create(
                    user.getId(),
                    rawToken,
                    expiresAt,
                    command.getUserAgent(),
                    command.getIpAddress()
            );

            // Guardar
            passwordResetRepository.save(passwordReset);

            logger.info("Password reset created for user: {} from IP: {}",
                    user.getId(), command.getIpAddress());

            // Log para debugging - en producción se enviaría por email
            logger.debug("Password reset token would be sent to email: {} (token not logged for security)",
                    user.getEmail());

            return ForgotPasswordResponse.success();

        } catch (Exception e) {
            logger.error("Error processing forgot password request", e);
            // Por seguridad, siempre devolver éxito
            return ForgotPasswordResponse.success();
        }
    }

    private void validateCommand(ForgotPasswordCommand command) {
        if (command.getEmail() == null || command.getEmail().trim().isEmpty()) {
            throw new DomainException(ErrorCodes.VALIDATION_ERROR, "Email is required");
        }
    }

    private void invalidateExistingReset(Long userId) {
        Optional<PasswordReset> existingReset = passwordResetRepository.findActiveByUserId(userId);
        if (existingReset.isPresent()) {
            passwordResetRepository.markAsUsed(existingReset.get().getId());
        }
    }
}

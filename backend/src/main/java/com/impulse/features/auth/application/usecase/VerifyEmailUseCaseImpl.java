package com.impulse.features.auth.application.usecase;

import com.impulse.features.auth.application.dto.VerifyEmailCommand;
import com.impulse.features.auth.application.dto.VerifyEmailResponse;
import com.impulse.features.auth.application.port.in.VerifyEmailUseCase;
import com.impulse.features.auth.application.port.out.EmailVerificationRepository;
import com.impulse.features.auth.application.port.out.UserRepository;
import com.impulse.features.auth.domain.EmailVerification;
import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * VerifyEmailUseCaseImpl - Implementación del caso de uso verify email
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
@Service
@Transactional
public class VerifyEmailUseCaseImpl implements VerifyEmailUseCase {

    private static final Logger logger = LoggerFactory.getLogger(VerifyEmailUseCaseImpl.class);

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;

    public VerifyEmailUseCaseImpl(EmailVerificationRepository emailVerificationRepository,
                                 UserRepository userRepository) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public VerifyEmailResponse execute(VerifyEmailCommand command) {
        try {
            // Validar comando
            validateCommand(command);

            // Buscar verificación por token hash
            String tokenHash = EmailVerification.hashToken(command.getToken());
            Optional<EmailVerification> verificationOpt = emailVerificationRepository.findByTokenHash(tokenHash);

            if (verificationOpt.isEmpty()) {
                logger.warn("Email verification attempted with invalid token from IP: {}", command.getIpAddress());
                return VerifyEmailResponse.failure("Invalid or expired verification token");
            }

            EmailVerification verification = verificationOpt.get();

            // Validar que el token sea válido (no usado y no expirado)
            if (!verification.isValid()) {
                logger.warn("Email verification attempted with invalid/expired token for user: {} from IP: {}",
                    verification.getUserId(), command.getIpAddress());
                return VerifyEmailResponse.failure("Invalid or expired verification token");
            }

            // Buscar usuario
            Optional<UserRepository.User> userOpt = userRepository.findById(verification.getUserId());
            if (userOpt.isEmpty()) {
                logger.error("Email verification token found but user does not exist: {}", verification.getUserId());
                return VerifyEmailResponse.failure("Invalid verification token");
            }

            UserRepository.User user = userOpt.get();

            // Verificar que el usuario esté activo
            if (!user.isActive()) {
                logger.warn("Email verification attempted for inactive user: {}", user.getId());
                return VerifyEmailResponse.failure("Account is not active");
            }

            // Verificar que el email coincida
            if (!user.getEmail().equals(verification.getEmail())) {
                logger.error("Email verification token email mismatch for user: {} - token email: {}, user email: {}",
                    user.getId(), verification.getEmail(), user.getEmail());
                return VerifyEmailResponse.failure("Invalid verification token");
            }

            // Verificar si ya está verificado
            if (user.isEmailVerified()) {
                logger.info("Email verification attempted for already verified user: {}", user.getId());
                return VerifyEmailResponse.success(); // Ya verificado, devolver éxito
            }

            // Marcar email como verificado
            userRepository.updateEmailVerified(user.getId(), true);

            // Marcar token como usado (eliminar la verificación)
            emailVerificationRepository.delete(verification);

            logger.info("Email successfully verified for user: {} from IP: {}",
                user.getId(), command.getIpAddress());

            return VerifyEmailResponse.success();

        } catch (DomainException e) {
            logger.error("Domain error in verify email: {}", e.getMessage());
            return VerifyEmailResponse.failure("Email verification failed");
        } catch (Exception e) {
            logger.error("Unexpected error in verify email", e);
            return VerifyEmailResponse.failure("Email verification failed");
        }
    }

    private void validateCommand(VerifyEmailCommand command) {
        if (command.getToken() == null || command.getToken().trim().isEmpty()) {
            throw new DomainException(ErrorCodes.VALIDATION_ERROR, "Verification token is required");
        }
    }
}

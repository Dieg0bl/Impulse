package com.impulse.features.auth.application.usecase;

import com.impulse.features.auth.application.dto.RegisterUserCommand;
import com.impulse.features.auth.application.dto.RegisterUserResponse;
import com.impulse.features.auth.application.port.in.RegisterUserUseCase;
import com.impulse.features.auth.application.port.out.UserRepository;
import com.impulse.features.auth.application.port.out.EmailVerificationRepository;
import com.impulse.features.auth.domain.EmailVerification;
import com.impulse.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Use case implementation for user registration
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Service
@Transactional
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    // Password requirements pattern (at least 8 chars, 1 uppercase, 1 lowercase, 1 digit)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$"
    );

    public RegisterUserUseCaseImpl(UserRepository userRepository,
                                 EmailVerificationRepository emailVerificationRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.emailVerificationRepository = Objects.requireNonNull(emailVerificationRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Override
    public RegisterUserResponse execute(RegisterUserCommand command) {
        // Validate input
        validateRegistrationData(command);

        // Check if user already exists
        checkUserDoesNotExist(command);

        // Create new user
        UserRepository.User newUser = createUser(command);
        UserRepository.User savedUser = userRepository.save(newUser);

        // Create email verification token
        String verificationToken = EmailVerification.generateToken();
        EmailVerification emailVerification = EmailVerification.create(
            savedUser.getId(),
            savedUser.getEmail(),
            verificationToken,
            LocalDateTime.now().plusHours(24), // 24 hours to verify
            command.getUserAgent(),
            command.getIpAddress()
        );

        emailVerificationRepository.save(emailVerification);

        // TODO: Send verification email (implement EmailService)

        return new RegisterUserResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            true, // Email verification required
            "Registration successful. Please check your email to verify your account."
        );
    }

    private void validateRegistrationData(RegisterUserCommand command) {
        // Validate username
        if (command.getUsername() == null || command.getUsername().trim().isEmpty()) {
            throw new DomainException("Username is required");
        }

        if (command.getUsername().length() < 3 || command.getUsername().length() > 50) {
            throw new DomainException("Username must be between 3 and 50 characters");
        }

        if (!command.getUsername().matches("^[a-zA-Z0-9_-]+$")) {
            throw new DomainException("Username can only contain letters, numbers, underscore and dash");
        }

        // Validate email
        if (command.getEmail() == null || command.getEmail().trim().isEmpty()) {
            throw new DomainException("Email is required");
        }

        if (!EMAIL_PATTERN.matcher(command.getEmail()).matches()) {
            throw new DomainException("Invalid email format");
        }

        // Validate password
        if (command.getPassword() == null || command.getPassword().isEmpty()) {
            throw new DomainException("Password is required");
        }

        if (!PASSWORD_PATTERN.matcher(command.getPassword()).matches()) {
            throw new DomainException(
                "Password must be at least 8 characters with 1 uppercase, 1 lowercase, and 1 digit"
            );
        }
    }

    private void checkUserDoesNotExist(RegisterUserCommand command) {
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new DomainException("Username already exists");
        }

        if (userRepository.existsByEmail(command.getEmail())) {
            throw new DomainException("Email already registered");
        }
    }

    private UserRepository.User createUser(RegisterUserCommand command) {
        String hashedPassword = passwordEncoder.encode(command.getPassword());

        return new UserRepository.User(
            null, // ID will be generated by database
            command.getUsername(),
            command.getEmail(),
            hashedPassword,
            command.getFirstName(),
            command.getLastName(),
            false, // Email not verified yet
            true   // Account is active by default
        );
    }
}

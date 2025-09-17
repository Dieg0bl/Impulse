package com.impulse.application.user;

import com.impulse.application.user.dto.CreateUserCommand;
import com.impulse.application.user.dto.UserResponse;
import com.impulse.application.user.mappers.UserAppMapper;
import com.impulse.application.user.ports.UserRepository;
import com.impulse.domain.user.Email;
import com.impulse.domain.user.User;
import com.impulse.domain.user.UserDomainError;

/**
 * CreateUserUseCase - Caso de uso para crear nuevos usuarios
 */
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final UserAppMapper userMapper;

    public CreateUserUseCase(UserRepository userRepository, UserAppMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse execute(CreateUserCommand command) {
        // Validar que el email no exista
        Email email = Email.of(command.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new UserDomainError("User with email " + email.getValue() + " already exists");
        }

        // Crear usuario de dominio
        User user = User.create(
            email,
            command.getName(),
            command.getPassword(), // En producción esto debe estar hasheado
            command.getRole()
        );

        // Guardar
        User savedUser = userRepository.save(user);

        // Mapear a DTO de respuesta
        return userMapper.toResponse(savedUser);
    }
}

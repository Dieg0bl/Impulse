package com.impulse.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.impulse.application.user.CreateUserUseCase;
import com.impulse.application.user.mappers.UserAppMapper;
import com.impulse.application.user.ports.UserRepository;

/**
 * UserBeanConfig - Configuración de beans para el módulo de usuarios
 */
@Configuration
public class UserBeanConfig {

    @Bean
    public UserAppMapper userAppMapper() {
        return new UserAppMapper();
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository,
                                              UserAppMapper userAppMapper) {
        return new CreateUserUseCase(userRepository, userAppMapper);
    }

    // Aquí se agregarían más casos de uso: GetUserByIdUseCase, UpdateUserUseCase, etc.
}

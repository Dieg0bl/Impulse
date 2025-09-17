package com.impulse.adapters.persistence.user.mappers;

import com.impulse.adapters.persistence.user.entities.UserJpaEntity;
import com.impulse.domain.user.User;
import com.impulse.domain.user.UserId;
import com.impulse.domain.user.Email;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Collections;

/**
 * UserJpaMapper - Mapper entre entidades de dominio y JPA
 */
@Component
public class UserJpaMapper {

    private final ObjectMapper objectMapper;

    public UserJpaMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Convierte de entidad de dominio a entidad JPA
     */
    public UserJpaEntity toJpaEntity(User user) {
        UserJpaEntity jpaEntity = new UserJpaEntity(
            user.getEmail().getValue(),
            user.getName(),
            user.getPassword(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt()
        );

        if (user.getId() != null) {
            try {
                jpaEntity.setId(user.getId().asLong());
            } catch (IllegalStateException e) {
                // Si el ID no es un Long vÃ¡lido, lo dejamos null para que JPA genere uno nuevo
            }
        }

        jpaEntity.setLastLoginAt(user.getLastLoginAt());
        jpaEntity.setEmailVerified(user.isEmailVerified());

        // Convertir preferences de List<String> a JSON
        if (user.getPreferences() != null) {
            try {
                jpaEntity.setPreferences(objectMapper.writeValueAsString(user.getPreferences()));
            } catch (JsonProcessingException e) {
                jpaEntity.setPreferences("[]");
            }
        }

        return jpaEntity;
    }

    /**
     * Convierte de entidad JPA a entidad de dominio
     */
    public User toDomainEntity(UserJpaEntity jpaEntity) {
        User user = new User(
            UserId.of(jpaEntity.getId()),
            Email.of(jpaEntity.getEmail()),
            jpaEntity.getName(),
            jpaEntity.getPassword(),
            jpaEntity.getRole(),
            jpaEntity.getStatus(),
            jpaEntity.getCreatedAt()
        );

        // Note: lastLoginAt se establecerÃ¡ cuando se construya el User
        // Si necesitas establecer lastLoginAt, considera agregar un setter o constructor adicional

        if (jpaEntity.isEmailVerified()) {
            user.verifyEmail();
        }

        // Convertir preferences de JSON a List<String>
        if (jpaEntity.getPreferences() != null) {
            try {
                List<String> preferences = objectMapper.readValue(
                    jpaEntity.getPreferences(),
                    new TypeReference<List<String>>() {}
                );
                user.setPreferences(preferences);
            } catch (JsonProcessingException e) {
                user.setPreferences(Collections.emptyList());
            }
        }

        return user;
    }
}



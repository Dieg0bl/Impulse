package com.impulse.adapters.persistence.validator.mapper;

import com.impulse.adapters.persistence.validator.entity.ValidatorJpaEntity;
import com.impulse.domain.validator.Validator;
import com.impulse.domain.validator.ValidatorId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ValidatorJpaMapper {

    public ValidatorJpaEntity toEntity(Validator domain) {
        if (domain == null) {
            return null;
        }

        ValidatorJpaEntity entity = new ValidatorJpaEntity();
        entity.setId(domain.getId() != null ? UUID.fromString(domain.getId().getValue()) : null);
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setSpecializations(domain.getSpecializations());
        entity.setIsActive(domain.getIsActive());
        entity.setRating(domain.getRating());
        entity.setExperienceYears(domain.getExperienceYears());

        return entity;
    }

    public Validator toDomain(ValidatorJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Validator.builder()
                .id(entity.getId() != null ? new ValidatorId(entity.getId().toString()) : null)
                .name(entity.getName())
                .email(entity.getEmail())
                .specializations(entity.getSpecializations())
                .isActive(entity.getIsActive())
                .rating(entity.getRating())
                .experienceYears(entity.getExperienceYears())
                .build();
    }
}

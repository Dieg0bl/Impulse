package com.impulse.infrastructure.web.mapper;

import com.impulse.domain.challenge.Challenge;
import com.impulse.infrastructure.web.dto.ChallengeCreateDto;
import com.impulse.infrastructure.web.dto.ChallengeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Challenge entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChallengeMapper {
    
    ChallengeResponseDto toResponseDto(Challenge challenge);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Challenge toEntity(ChallengeCreateDto dto);
}

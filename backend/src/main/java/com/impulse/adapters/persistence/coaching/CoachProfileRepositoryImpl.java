package com.impulse.adapters.persistence.coaching;

import com.impulse.adapters.persistence.coaching.mapper.CoachProfileJpaMapper;
import com.impulse.adapters.persistence.coaching.repository.SpringDataCoachProfileRepository;
import com.impulse.application.coachprofile.port.CoachProfileRepository;
import com.impulse.domain.coachprofile.CoachProfile;
import com.impulse.domain.coachprofile.CoachProfileId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CoachProfileRepositoryImpl implements CoachProfileRepository {

    private final SpringDataCoachProfileRepository springDataRepository;
    private final CoachProfileJpaMapper mapper;

    @Override
    public CoachProfile save(CoachProfile coachProfile) {
        var entity = mapper.toEntity(coachProfile);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CoachProfile> findById(CoachProfileId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<CoachProfile> findByUserId(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return springDataRepository.findByUserId(userUuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<CoachProfile> findActiveCoaches() {
        return springDataRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachProfile> findBySpecialization(String specialization) {
        return springDataRepository.findBySpecializationAndActive(specialization)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachProfile> findByMaxRate(BigDecimal maxRate) {
        return springDataRepository.findByMaxRateAndActive(maxRate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(CoachProfileId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}

package com.impulse.application.gamification.ports;

import com.impulse.domain.gamification.PersonalLeague;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonalLeagueRepository {
    PersonalLeague save(PersonalLeague personalLeague);
    Optional<PersonalLeague> findById(UUID id);
    List<PersonalLeague> findAll();
    List<PersonalLeague> findByTier(String tier);
    List<PersonalLeague> findByMinCredPointsLessThanEqual(int credPoints);
    Optional<PersonalLeague> findByName(String name);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}

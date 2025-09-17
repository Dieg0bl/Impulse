package com.impulse.gamification.service;

import com.impulse.gamification.PersonalLeague;
import com.impulse.gamification.repository.PersonalLeagueRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonalLeagueService {
    private final PersonalLeagueRepository personalLeagueRepository;
    public PersonalLeagueService(PersonalLeagueRepository personalLeagueRepository) {
        this.personalLeagueRepository = personalLeagueRepository;
    }
    public List<PersonalLeague> getAllPersonalLeagues() {
        return personalLeagueRepository.findAll();
    }
}

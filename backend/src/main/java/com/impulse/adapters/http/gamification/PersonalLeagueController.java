package com.impulse.gamification.controller;

import com.impulse.gamification.PersonalLeague;
import com.impulse.gamification.service.PersonalLeagueService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gamification/personal-leagues")
public class PersonalLeagueController {
    private final PersonalLeagueService personalLeagueService;
    public PersonalLeagueController(PersonalLeagueService personalLeagueService) {
        this.personalLeagueService = personalLeagueService;
    }
    @GetMapping
    public List<PersonalLeague> getAllPersonalLeagues() {
        return personalLeagueService.getAllPersonalLeagues();
    }
}

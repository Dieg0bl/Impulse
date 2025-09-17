package com.impulse.coaching.service;

import com.impulse.domain.model.PersonalPlan;
import com.impulse.coaching.repository.PersonalPlanRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PersonalPlanService {
    private final PersonalPlanRepository repository;

    public PersonalPlanService(PersonalPlanRepository repository) {
        this.repository = repository;
    }

    public List<PersonalPlan> findAll() {
        return repository.findAll();
    }

    public Optional<PersonalPlan> findById(Long id) {
        return repository.findById(id);
    }

    public PersonalPlan save(PersonalPlan plan) {
        return repository.save(plan);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

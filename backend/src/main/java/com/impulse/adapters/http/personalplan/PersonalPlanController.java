package com.impulse.adapters.http.personalplan;

import com.impulse.application.personalplan.dto.PersonalPlanResponse;
import com.impulse.application.personalplan.dto.CreatePersonalPlanCommand;
import com.impulse.application.personalplan.usecase.CreatePersonalPlanUseCase;
import com.impulse.application.personalplan.usecase.GetPersonalPlanByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personal-plans")
public class PersonalPlanController {
    private final CreatePersonalPlanUseCase createPersonalPlanUseCase;
    private final GetPersonalPlanByIdUseCase getPersonalPlanByIdUseCase;

    public PersonalPlanController(CreatePersonalPlanUseCase createPersonalPlanUseCase, GetPersonalPlanByIdUseCase getPersonalPlanByIdUseCase) {
        this.createPersonalPlanUseCase = createPersonalPlanUseCase;
        this.getPersonalPlanByIdUseCase = getPersonalPlanByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<PersonalPlanResponse> createPersonalPlan(@RequestBody CreatePersonalPlanCommand command) {
        PersonalPlanResponse response = createPersonalPlanUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalPlanResponse> getPersonalPlanById(@PathVariable Long id) {
        PersonalPlanResponse response = getPersonalPlanByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

package com.impulse.adapters.http.coach;

import com.impulse.application.coach.dto.CoachResponse;
import com.impulse.application.coach.dto.CreateCoachCommand;
import com.impulse.application.coach.usecase.CreateCoachUseCase;
import com.impulse.application.coach.usecase.GetCoachByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {
    private final CreateCoachUseCase createCoachUseCase;
    private final GetCoachByIdUseCase getCoachByIdUseCase;

    public CoachController(CreateCoachUseCase createCoachUseCase, GetCoachByIdUseCase getCoachByIdUseCase) {
        this.createCoachUseCase = createCoachUseCase;
        this.getCoachByIdUseCase = getCoachByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<CoachResponse> createCoach(@RequestBody CreateCoachCommand command) {
        CoachResponse response = createCoachUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoachResponse> getCoachById(@PathVariable Long id) {
        CoachResponse response = getCoachByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

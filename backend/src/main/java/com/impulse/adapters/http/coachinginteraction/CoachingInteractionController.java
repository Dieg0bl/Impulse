package com.impulse.adapters.http.coachinginteraction;

import com.impulse.application.coachinginteraction.dto.CreateCoachingInteractionCommand;
import com.impulse.application.coachinginteraction.dto.CoachingInteractionResponse;
import com.impulse.application.coachinginteraction.usecase.CreateCoachingInteractionUseCase;
import com.impulse.application.coachinginteraction.usecase.GetCoachingInteractionByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaching-interactions")
@RequiredArgsConstructor
public class CoachingInteractionController {
    private final CreateCoachingInteractionUseCase createCoachingInteractionUseCase;
    private final GetCoachingInteractionByIdUseCase getCoachingInteractionByIdUseCase;
    @PostMapping
    public ResponseEntity<CoachingInteractionResponse> createCoachingInteraction(@RequestBody CreateCoachingInteractionCommand command) {
        CoachingInteractionResponse response = createCoachingInteractionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CoachingInteractionResponse> getCoachingInteractionById(@PathVariable String id) {
        CoachingInteractionResponse response = getCoachingInteractionByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

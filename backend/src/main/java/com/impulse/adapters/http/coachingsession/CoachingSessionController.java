package com.impulse.adapters.http.coachingsession;

import com.impulse.application.coachingsession.dto.CreateCoachingSessionCommand;
import com.impulse.application.coachingsession.dto.CoachingSessionResponse;
import com.impulse.application.coachingsession.usecase.CreateCoachingSessionUseCase;
import com.impulse.application.coachingsession.usecase.GetCoachingSessionByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaching-sessions")
@RequiredArgsConstructor
public class CoachingSessionController {
    private final CreateCoachingSessionUseCase createCoachingSessionUseCase;
    private final GetCoachingSessionByIdUseCase getCoachingSessionByIdUseCase;
    @PostMapping
    public ResponseEntity<CoachingSessionResponse> createCoachingSession(@RequestBody CreateCoachingSessionCommand command) {
        CoachingSessionResponse response = createCoachingSessionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CoachingSessionResponse> getCoachingSessionById(@PathVariable String id) {
        CoachingSessionResponse response = getCoachingSessionByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

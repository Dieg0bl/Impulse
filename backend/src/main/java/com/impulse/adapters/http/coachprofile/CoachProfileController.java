package com.impulse.adapters.http.coachprofile;

import com.impulse.application.coachprofile.dto.CreateCoachProfileCommand;
import com.impulse.application.coachprofile.dto.CoachProfileResponse;
import com.impulse.application.coachprofile.usecase.CreateCoachProfileUseCase;
import com.impulse.application.coachprofile.usecase.GetCoachProfileByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coach-profiles")
@RequiredArgsConstructor
public class CoachProfileController {
    private final CreateCoachProfileUseCase createCoachProfileUseCase;
    private final GetCoachProfileByIdUseCase getCoachProfileByIdUseCase;
    @PostMapping
    public ResponseEntity<CoachProfileResponse> createCoachProfile(@RequestBody CreateCoachProfileCommand command) {
        CoachProfileResponse response = createCoachProfileUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CoachProfileResponse> getCoachProfileById(@PathVariable String id) {
        CoachProfileResponse response = getCoachProfileByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

package com.impulse.adapters.http.challengeparticipation;

import com.impulse.application.challengeparticipation.dto.ChallengeParticipationResponse;
import com.impulse.application.challengeparticipation.dto.CreateChallengeParticipationCommand;
import com.impulse.application.challengeparticipation.usecase.CreateChallengeParticipationUseCase;
import com.impulse.application.challengeparticipation.usecase.GetChallengeParticipationByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenge-participations")
public class ChallengeParticipationController {
    private final CreateChallengeParticipationUseCase createChallengeParticipationUseCase;
    private final GetChallengeParticipationByIdUseCase getChallengeParticipationByIdUseCase;

    public ChallengeParticipationController(CreateChallengeParticipationUseCase createChallengeParticipationUseCase, GetChallengeParticipationByIdUseCase getChallengeParticipationByIdUseCase) {
        this.createChallengeParticipationUseCase = createChallengeParticipationUseCase;
        this.getChallengeParticipationByIdUseCase = getChallengeParticipationByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<ChallengeParticipationResponse> createChallengeParticipation(@RequestBody CreateChallengeParticipationCommand command) {
        ChallengeParticipationResponse response = createChallengeParticipationUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeParticipationResponse> getChallengeParticipationById(@PathVariable Long id) {
        ChallengeParticipationResponse response = getChallengeParticipationByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

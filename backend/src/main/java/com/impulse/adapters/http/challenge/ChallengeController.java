package com.impulse.adapters.http.challenge;

import com.impulse.adapters.http.challenge.dto.CreateChallengeRequestDto;
import com.impulse.adapters.http.challenge.dto.ChallengeResponseDto;
import com.impulse.adapters.http.challenge.mapper.ChallengeApiMapper;
import com.impulse.application.challenge.dto.CreateChallengeCommand;
import com.impulse.application.challenge.dto.ChallengeResponse;
import com.impulse.application.challenge.usecase.CreateChallengeUseCase;
import com.impulse.application.challenge.usecase.GetChallengeByIdUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * ChallengeController - Controlador REST para operaciones de challenges
 */
@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = "*")
public class ChallengeController {

    private final CreateChallengeUseCase createChallengeUseCase;
    private final GetChallengeByIdUseCase getChallengeByIdUseCase;
    private final ChallengeApiMapper challengeApiMapper;

    public ChallengeController(CreateChallengeUseCase createChallengeUseCase,
                              GetChallengeByIdUseCase getChallengeByIdUseCase,
                              ChallengeApiMapper challengeApiMapper) {
        this.createChallengeUseCase = createChallengeUseCase;
        this.getChallengeByIdUseCase = getChallengeByIdUseCase;
        this.challengeApiMapper = challengeApiMapper;
    }

    /**
     * Crear un nuevo challenge
     */
    @PostMapping
    public ResponseEntity<ChallengeResponseDto> createChallenge(
            @Valid @RequestBody CreateChallengeRequestDto requestDto,
            Authentication authentication) {

        String userId = authentication.getName(); // Obtener ID del usuario autenticado
        CreateChallengeCommand command = challengeApiMapper.toCommand(requestDto, userId);
        ChallengeResponse response = createChallengeUseCase.execute(command);
        ChallengeResponseDto responseDto = challengeApiMapper.toDto(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Obtener challenge por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto> getChallengeById(@PathVariable Long id) {
        ChallengeResponse response = getChallengeByIdUseCase.execute(id);
        ChallengeResponseDto responseDto = challengeApiMapper.toDto(response);

        return ResponseEntity.ok(responseDto);
    }
}

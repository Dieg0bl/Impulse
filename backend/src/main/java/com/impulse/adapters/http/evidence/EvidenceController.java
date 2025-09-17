package com.impulse.adapters.http.evidence;

import com.impulse.adapters.http.evidence.dto.CreateEvidenceRequestDto;
import com.impulse.adapters.http.evidence.dto.EvidenceResponseDto;
import com.impulse.adapters.http.evidence.mapper.EvidenceApiMapper;
import com.impulse.application.evidence.dto.CreateEvidenceCommand;
import com.impulse.application.evidence.dto.EvidenceResponse;
import com.impulse.application.evidence.usecase.CreateEvidenceUseCase;
import com.impulse.application.evidence.usecase.GetEvidenceByIdUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * EvidenceController - Controlador REST para operaciones de evidencias
 */
@RestController
@RequestMapping("/api/evidence")
@CrossOrigin(origins = "*")
public class EvidenceController {

    private final CreateEvidenceUseCase createEvidenceUseCase;
    private final GetEvidenceByIdUseCase getEvidenceByIdUseCase;
    private final EvidenceApiMapper evidenceApiMapper;

    public EvidenceController(CreateEvidenceUseCase createEvidenceUseCase,
                             GetEvidenceByIdUseCase getEvidenceByIdUseCase,
                             EvidenceApiMapper evidenceApiMapper) {
        this.createEvidenceUseCase = createEvidenceUseCase;
        this.getEvidenceByIdUseCase = getEvidenceByIdUseCase;
        this.evidenceApiMapper = evidenceApiMapper;
    }

    /**
     * Crear una nueva evidencia
     */
    @PostMapping
    public ResponseEntity<EvidenceResponseDto> createEvidence(
            @Valid @RequestBody CreateEvidenceRequestDto requestDto,
            Authentication authentication) {

        String userId = authentication.getName(); // Obtener ID del usuario autenticado
        CreateEvidenceCommand command = evidenceApiMapper.toCommand(requestDto, userId);
        EvidenceResponse response = createEvidenceUseCase.execute(command);
        EvidenceResponseDto responseDto = evidenceApiMapper.toDto(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Obtener evidencia por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvidenceResponseDto> getEvidenceById(@PathVariable String id) {
        EvidenceResponse response = getEvidenceByIdUseCase.execute(id);
        EvidenceResponseDto responseDto = evidenceApiMapper.toDto(response);

        return ResponseEntity.ok(responseDto);
    }
}

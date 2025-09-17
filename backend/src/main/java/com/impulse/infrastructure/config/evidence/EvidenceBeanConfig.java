package com.impulse.infrastructure.config.evidence;

import com.impulse.adapters.persistence.evidence.mapper.EvidenceJpaMapper;
import com.impulse.adapters.persistence.evidence.repository.EvidenceRepositoryImpl;
import com.impulse.adapters.persistence.evidence.repository.SpringDataEvidenceRepository;
import com.impulse.adapters.http.evidence.EvidenceController;
import com.impulse.adapters.http.evidence.mapper.EvidenceApiMapper;
import com.impulse.application.evidence.mapper.EvidenceAppMapper;
import com.impulse.application.evidence.port.EvidenceRepository;
import com.impulse.application.evidence.usecase.CreateEvidenceUseCase;
import com.impulse.application.evidence.usecase.GetEvidenceByIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EvidenceBeanConfig - Configuración de beans para Evidence
 */
@Configuration
public class EvidenceBeanConfig {

    @Bean
    public EvidenceJpaMapper evidenceJpaMapper() {
        return new EvidenceJpaMapper();
    }

    @Bean
    public EvidenceAppMapper evidenceAppMapper() {
        return new EvidenceAppMapper();
    }

    @Bean
    public EvidenceApiMapper evidenceApiMapper() {
        return new EvidenceApiMapper();
    }

    @Bean
    public EvidenceRepository evidenceRepository(SpringDataEvidenceRepository springDataRepository,
                                                EvidenceJpaMapper evidenceJpaMapper) {
        return new EvidenceRepositoryImpl(springDataRepository, evidenceJpaMapper);
    }

    @Bean
    public CreateEvidenceUseCase createEvidenceUseCase(EvidenceRepository evidenceRepository,
                                                      EvidenceAppMapper evidenceAppMapper) {
        return new CreateEvidenceUseCase(evidenceRepository, evidenceAppMapper);
    }

    @Bean
    public GetEvidenceByIdUseCase getEvidenceByIdUseCase(EvidenceRepository evidenceRepository,
                                                        EvidenceAppMapper evidenceAppMapper) {
        return new GetEvidenceByIdUseCase(evidenceRepository, evidenceAppMapper);
    }

    @Bean
    public EvidenceController evidenceController(CreateEvidenceUseCase createEvidenceUseCase,
                                                GetEvidenceByIdUseCase getEvidenceByIdUseCase,
                                                EvidenceApiMapper evidenceApiMapper) {
        return new EvidenceController(createEvidenceUseCase, getEvidenceByIdUseCase, evidenceApiMapper);
    }
}

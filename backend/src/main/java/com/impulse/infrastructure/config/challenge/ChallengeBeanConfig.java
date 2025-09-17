package com.impulse.infrastructure.config.challenge;

import com.impulse.adapters.persistence.challenge.mapper.ChallengeJpaMapper;
import com.impulse.adapters.persistence.challenge.repository.ChallengeRepositoryImpl;
import com.impulse.adapters.persistence.challenge.repository.SpringDataChallengeRepository;
import com.impulse.adapters.http.challenge.ChallengeController;
import com.impulse.adapters.http.challenge.mapper.ChallengeApiMapper;
import com.impulse.application.challenge.mapper.ChallengeAppMapper;
import com.impulse.application.challenge.port.ChallengeRepository;
import com.impulse.application.challenge.usecase.CreateChallengeUseCase;
import com.impulse.application.challenge.usecase.GetChallengeByIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChallengeBeanConfig - Configuración de beans para Challenge
 */
@Configuration
public class ChallengeBeanConfig {

    // Mappers
    @Bean
    public ChallengeJpaMapper challengeJpaMapper() {
        return new ChallengeJpaMapper();
    }

    @Bean
    public ChallengeAppMapper challengeAppMapper() {
        return new ChallengeAppMapper();
    }

    @Bean
    public ChallengeApiMapper challengeApiMapper() {
        return new ChallengeApiMapper();
    }

    // Repository
    @Bean
    public ChallengeRepository challengeRepository(SpringDataChallengeRepository springDataRepository,
                                                  ChallengeJpaMapper challengeJpaMapper) {
        return new ChallengeRepositoryImpl(springDataRepository, challengeJpaMapper);
    }

    // Use Cases
    @Bean
    public CreateChallengeUseCase createChallengeUseCase(ChallengeRepository challengeRepository,
                                                        ChallengeAppMapper challengeAppMapper) {
        return new CreateChallengeUseCase(challengeRepository, challengeAppMapper);
    }

    @Bean
    public GetChallengeByIdUseCase getChallengeByIdUseCase(ChallengeRepository challengeRepository,
                                                          ChallengeAppMapper challengeAppMapper) {
        return new GetChallengeByIdUseCase(challengeRepository, challengeAppMapper);
    }

    // Controller
    @Bean
    public ChallengeController challengeController(CreateChallengeUseCase createChallengeUseCase,
                                                  GetChallengeByIdUseCase getChallengeByIdUseCase,
                                                  ChallengeApiMapper challengeApiMapper) {
        return new ChallengeController(createChallengeUseCase, getChallengeByIdUseCase, challengeApiMapper);
    }
}

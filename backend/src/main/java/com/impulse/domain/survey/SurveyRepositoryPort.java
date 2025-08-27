package com.impulse.domain.survey;

import java.util.List;
import java.util.Optional;
import com.impulse.domain.pmf.Survey;

public interface SurveyRepositoryPort {
    Optional<Survey> findById(Long id);
    Survey save(Survey survey);
    List<Survey> findAll();
    void deleteById(Long id);
}

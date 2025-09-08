package com.impulse.infrastructure.repository;

import com.impulse.domain.pmf.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> { }

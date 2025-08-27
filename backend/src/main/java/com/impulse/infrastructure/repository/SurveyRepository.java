package com.impulse.infrastructure.repository;

import com.impulse.domain.pmf.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import com.impulse.application.ports.SurveyPort;
public interface SurveyRepository extends SurveyPort { }

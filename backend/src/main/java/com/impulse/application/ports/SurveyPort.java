package com.impulse.application.ports;

import com.impulse.domain.pmf.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SurveyPort extends JpaRepository<Survey, Long> {}

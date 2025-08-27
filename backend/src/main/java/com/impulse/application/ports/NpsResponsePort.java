package com.impulse.application.ports;

import com.impulse.domain.support.NpsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NpsResponsePort extends JpaRepository<NpsResponse, Long> {}

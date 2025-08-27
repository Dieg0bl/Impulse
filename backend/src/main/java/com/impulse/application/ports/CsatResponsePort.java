package com.impulse.application.ports;

import com.impulse.domain.support.CsatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CsatResponsePort extends JpaRepository<CsatResponse, Long> {}

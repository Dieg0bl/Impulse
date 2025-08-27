package com.impulse.application.ports;

import com.impulse.domain.gamificacion.Gamificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GamificacionPort extends JpaRepository<Gamificacion, Long> {}

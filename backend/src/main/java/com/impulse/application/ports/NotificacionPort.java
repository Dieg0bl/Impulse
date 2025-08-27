package com.impulse.application.ports;

import com.impulse.domain.notificacion.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NotificacionPort extends JpaRepository<Notificacion, Long> {}

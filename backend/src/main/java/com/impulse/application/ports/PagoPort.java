package com.impulse.application.ports;

import com.impulse.domain.monetizacion.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PagoPort extends JpaRepository<Pago, Long> {}

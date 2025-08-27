package com.impulse.application.ports;

import com.impulse.domain.reto.Reto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface RetoPort extends JpaRepository<Reto, Long> {
    List<Reto> findByUsuario_Id(Long usuarioId);
}

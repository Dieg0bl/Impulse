package com.impulse.application.ports;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.impulse.domain.auditoria.Auditoria;

@NoRepositoryBean
public interface AuditoriaPort extends JpaRepository<Auditoria, Long> {
    // puerto para operaciones de auditoría — añadir consultas personalizadas si son necesarias
}

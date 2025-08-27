package com.impulse.domain.auditoria;

import java.util.Optional;
import java.util.List;

public interface AuditoriaRepositoryPort {
    Optional<AuditRecord> findById(Long id);
    AuditRecord save(AuditRecord record);
    List<AuditRecord> findAll();
    void deleteById(Long id);
}

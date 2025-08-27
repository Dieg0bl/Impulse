package com.impulse.domain.support;

import java.util.Optional;
import java.util.List;

public interface CsatResponseRepositoryPort {
    Optional<CsatResponse> findById(Long id);
    CsatResponse save(CsatResponse r);
    List<CsatResponse> findAll();
    void deleteById(Long id);
}

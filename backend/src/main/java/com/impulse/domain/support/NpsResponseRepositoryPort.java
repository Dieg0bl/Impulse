package com.impulse.domain.support;

import java.util.Optional;
import java.util.List;

public interface NpsResponseRepositoryPort {
    Optional<NpsResponse> findById(Long id);
    NpsResponse save(NpsResponse r);
    List<NpsResponse> findAll();
    void deleteById(Long id);
}

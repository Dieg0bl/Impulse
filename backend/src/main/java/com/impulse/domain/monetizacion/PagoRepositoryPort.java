package com.impulse.domain.monetizacion;

import java.util.Optional;
import java.util.List;

public interface PagoRepositoryPort {
    Optional<Pago> findById(Long id);
    Pago save(Pago pago);
    List<Pago> findAll();
    void deleteById(Long id);
}

package com.impulse.domain.monetizacion;

import java.util.Optional;
import java.util.List;

public interface SubscriptionRepositoryPort {
    Optional<Subscription> findById(Long id);
    Subscription save(Subscription subscription);
    List<Subscription> findAll();
    void deleteById(Long id);
}

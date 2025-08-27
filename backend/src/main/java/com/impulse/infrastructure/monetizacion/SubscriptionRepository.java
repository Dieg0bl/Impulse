package com.impulse.infrastructure.monetizacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.monetizacion.Subscription;
import com.impulse.application.ports.SubscriptionPort;
import java.util.List;

@Repository
public interface SubscriptionRepository extends SubscriptionPort {
    List<Subscription> findByUserId(Long userId);
}

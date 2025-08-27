package com.impulse.application.ports;

import com.impulse.domain.monetizacion.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SubscriptionPort extends JpaRepository<Subscription, Long> {
	java.util.List<Subscription> findByUserId(Long userId);
}

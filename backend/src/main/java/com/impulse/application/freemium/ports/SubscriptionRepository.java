package com.impulse.application.freemium.ports;

import com.impulse.domain.freemium.Subscription;
import com.impulse.domain.user.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    Optional<Subscription> findById(UUID id);
    List<Subscription> findAll();
    Optional<Subscription> findByUserId(UserId userId);
    List<Subscription> findByStatus(String status);
    List<Subscription> findByPlanId(String planId);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
    List<Subscription> findActiveSubscriptions();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
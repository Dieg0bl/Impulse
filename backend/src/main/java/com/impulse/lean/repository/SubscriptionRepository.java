package com.impulse.lean.repository;

import com.impulse.lean.domain.model.Subscription;
import com.impulse.lean.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Subscription Repository
 * 
 * Repository for Subscription entity with billing operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUser(User user);

    Optional<Subscription> findByUserId(Long userId);

    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);

    Optional<Subscription> findByStripeCustomerId(String stripeCustomerId);

    List<Subscription> findByStatus(Subscription.SubscriptionStatus status);

    List<Subscription> findByPlanType(Subscription.SubscriptionPlan planType);

    List<Subscription> findByBillingCycle(Subscription.BillingCycle billingCycle);

    @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.nextBillingDate <= :date")
    List<Subscription> findSubscriptionsForBilling(@Param("status") Subscription.SubscriptionStatus status, 
                                                   @Param("date") LocalDateTime date);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'TRIALING' AND s.trialEndsAt <= :date")
    List<Subscription> findTrialsEndingSoon(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'PAST_DUE' AND s.gracePeriodEndsAt <= :date")
    List<Subscription> findExpiredGracePeriods(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Subscription s WHERE s.user = :user AND s.status IN ('ACTIVE', 'TRIALING')")
    Optional<Subscription> findActiveSubscriptionByUser(@Param("user") User user);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
    Page<Subscription> findActiveSubscriptions(Pageable pageable);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = :status")
    Long countByStatus(@Param("status") Subscription.SubscriptionStatus status);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.planType = :planType AND s.status IN ('ACTIVE', 'TRIALING')")
    Long countActiveByPlanType(@Param("planType") Subscription.SubscriptionPlan planType);

    @Query("SELECT s FROM Subscription s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    List<Subscription> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s.planType, COUNT(s) FROM Subscription s WHERE s.status IN ('ACTIVE', 'TRIALING') GROUP BY s.planType")
    List<Object[]> getSubscriptionDistribution();

    @Query("SELECT s FROM Subscription s WHERE s.failedPaymentsCount >= :maxFailures")
    List<Subscription> findSubscriptionsWithFailedPayments(@Param("maxFailures") Integer maxFailures);

    @Query("SELECT s FROM Subscription s WHERE s.autoRenew = false AND s.endsAt <= :date")
    List<Subscription> findNonRenewingExpiringSoon(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.planType != 'FREE' ORDER BY s.price DESC")
    List<Subscription> findPaidSubscriptionsOrderedByPrice();

    @Query("SELECT AVG(s.price) FROM Subscription s WHERE s.status = 'ACTIVE' AND s.planType != 'FREE'")
    Double getAverageRevenuePerUser();

    @Query("SELECT SUM(s.price) FROM Subscription s WHERE s.status = 'ACTIVE' AND s.planType != 'FREE'")
    Double getTotalMonthlyRecurringRevenue();

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.isTrial = true AND s.status = 'TRIALING'")
    Long countActiveTrials();

    @Query("SELECT s FROM Subscription s WHERE s.user.id IN :userIds")
    List<Subscription> findByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT s FROM Subscription s WHERE s.metadata LIKE %:keyword%")
    List<Subscription> findByMetadataContaining(@Param("keyword") String keyword);

    boolean existsByUserAndStatus(User user, Subscription.SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.status IN ('ACTIVE', 'TRIALING') AND s.updatedAt < :staleDate")
    List<Subscription> findStaleSubscriptions(@Param("staleDate") LocalDateTime staleDate);

    @Query("SELECT s FROM Subscription s WHERE s.canceledAt IS NOT NULL AND s.canceledAt >= :since")
    List<Subscription> findRecentCancellations(@Param("since") LocalDateTime since);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.billingCycle = :cycle AND s.nextBillingDate BETWEEN :start AND :end")
    List<Subscription> findDueForBilling(@Param("cycle") Subscription.BillingCycle cycle,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);
}

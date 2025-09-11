package com.impulse.billing.repository;

import com.impulse.billing.model.Payment;
import com.impulse.billing.model.Subscription;
import com.impulse.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Payment Repository
 * 
 * Repository for Payment entity with transaction operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUser(User user);

    List<Payment> findByUserOrderByCreatedAtDesc(User user);

    Page<Payment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Optional<Payment> findByStripeChargeId(String stripeChargeId);

    Optional<Payment> findByStripeInvoiceId(String stripeInvoiceId);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);

    List<Payment> findBySubscription(Subscription subscription);

    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findByUserAndStatus(@Param("user") User user, @Param("status") Payment.PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.processedAt >= :startDate AND p.processedAt <= :endDate")
    List<Payment> findSuccessfulPaymentsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.retryCount < 3 ORDER BY p.failedAt ASC")
    List<Payment> findFailedPaymentsForRetry();

    @Query("SELECT p FROM Payment p WHERE p.status IN ('REQUIRES_ACTION', 'REQUIRES_CONFIRMATION', 'REQUIRES_PAYMENT_METHOD')")
    List<Payment> findPaymentsRequiringAction();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.processedAt >= :startDate AND p.processedAt <= :endDate")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") Payment.PaymentStatus status);

    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p WHERE p.status = 'SUCCEEDED' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodDistribution();

    @Query("SELECT AVG(p.amount) FROM Payment p WHERE p.status = 'SUCCEEDED'")
    BigDecimal getAveragePaymentAmount();

    @Query("SELECT p FROM Payment p WHERE p.isRecurring = true AND p.status = 'SUCCEEDED' ORDER BY p.processedAt DESC")
    List<Payment> findRecurringPayments();

    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.refundedAt IS NOT NULL ORDER BY p.refundedAt DESC")
    List<Payment> findRefundedPaymentsByUser(@Param("user") User user);

    @Query("SELECT SUM(p.refundAmount) FROM Payment p WHERE p.status IN ('REFUNDED', 'PARTIALLY_REFUNDED') AND p.refundedAt >= :startDate")
    BigDecimal getTotalRefundsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p FROM Payment p WHERE p.amount >= :amount ORDER BY p.amount DESC")
    List<Payment> findLargePayments(@Param("amount") BigDecimal amount);

    @Query("SELECT p FROM Payment p WHERE p.transactionFee IS NOT NULL ORDER BY p.transactionFee DESC")
    List<Payment> findPaymentsWithFees();

    @Query("SELECT SUM(p.transactionFee) FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.processedAt >= :startDate")
    BigDecimal getTotalFeesSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p FROM Payment p WHERE p.description LIKE %:keyword% ORDER BY p.createdAt DESC")
    List<Payment> findByDescriptionContaining(@Param("keyword") String keyword);

    @Query("SELECT p FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.currency = :currency")
    List<Payment> findSuccessfulPaymentsByCurrency(@Param("currency") String currency);

    @Query("SELECT DATE(p.processedAt), COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.processedAt >= :startDate GROUP BY DATE(p.processedAt) ORDER BY DATE(p.processedAt)")
    List<Object[]> getDailyRevenueStats(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p FROM Payment p WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC")
    List<Payment> findByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.failureReason LIKE %:reason%")
    List<Payment> findFailedPaymentsByReason(@Param("reason") String reason);

    @Query("SELECT COUNT(DISTINCT p.user) FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.processedAt >= :startDate")
    Long countUniquePayingCustomersSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p FROM Payment p WHERE p.receiptUrl IS NOT NULL ORDER BY p.processedAt DESC")
    List<Payment> findPaymentsWithReceipts();

    boolean existsByStripePaymentIntentId(String stripePaymentIntentId);

    @Query("SELECT p FROM Payment p WHERE p.netAmount IS NOT NULL AND p.netAmount > 0 ORDER BY p.netAmount DESC")
    List<Payment> findPaymentsByNetAmount();

    @Query("SELECT p FROM Payment p WHERE p.subscription IS NOT NULL AND p.status = 'SUCCEEDED' ORDER BY p.processedAt DESC")
    List<Payment> findSubscriptionPayments();

    @Query("SELECT p FROM Payment p WHERE p.subscription IS NULL AND p.status = 'SUCCEEDED' ORDER BY p.processedAt DESC")
    List<Payment> findOneTimePayments();
}

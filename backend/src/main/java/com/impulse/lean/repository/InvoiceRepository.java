package com.impulse.lean.repository;

import com.impulse.lean.domain.model.Invoice;
import com.impulse.lean.domain.model.Payment;
import com.impulse.lean.domain.model.Subscription;
import com.impulse.lean.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Invoice Repository
 * 
 * Repository for Invoice entity with billing operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByUser(User user);

    List<Invoice> findByUserOrderByInvoiceDateDesc(User user);

    Page<Invoice> findByUserOrderByInvoiceDateDesc(User user, Pageable pageable);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByStripeInvoiceId(String stripeInvoiceId);

    List<Invoice> findByStatus(Invoice.InvoiceStatus status);

    List<Invoice> findBySubscription(Subscription subscription);

    Optional<Invoice> findByPayment(Payment payment);

    @Query("SELECT i FROM Invoice i WHERE i.user = :user AND i.status = :status ORDER BY i.invoiceDate DESC")
    List<Invoice> findByUserAndStatus(@Param("user") User user, @Param("status") Invoice.InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.status = 'OPEN' AND i.dueDate < :date")
    List<Invoice> findOverdueInvoices(@Param("date") LocalDate date);

    @Query("SELECT i FROM Invoice i WHERE i.status = 'OPEN' AND i.dueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesDueSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate >= :startDate AND i.invoiceDate <= :endDate")
    List<Invoice> findByInvoiceDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Invoice i WHERE i.status = 'PAID' AND i.paidAt >= :startDate AND i.paidAt <= :endDate")
    List<Invoice> findPaidInvoicesByDateRange(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.paidAt >= :startDate AND i.paidAt <= :endDate")
    BigDecimal getTotalPaidAmountByDateRange(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'OPEN' AND i.dueDate < :date")
    BigDecimal getTotalOverdueAmount(@Param("date") LocalDate date);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    Long countByStatus(@Param("status") Invoice.InvoiceStatus status);

    @Query("SELECT i.status, COUNT(i) FROM Invoice i GROUP BY i.status")
    List<Object[]> getInvoiceStatusDistribution();

    @Query("SELECT AVG(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID'")
    BigDecimal getAverageInvoiceAmount();

    @Query("SELECT i FROM Invoice i WHERE i.totalAmount >= :amount ORDER BY i.totalAmount DESC")
    List<Invoice> findLargeInvoices(@Param("amount") BigDecimal amount);

    @Query("SELECT i FROM Invoice i WHERE i.attemptCount >= :maxAttempts")
    List<Invoice> findInvoicesWithMultipleAttempts(@Param("maxAttempts") Integer maxAttempts);

    @Query("SELECT i FROM Invoice i WHERE i.nextPaymentAttempt <= :date AND i.status = 'OPEN'")
    List<Invoice> findInvoicesForPaymentRetry(@Param("date") LocalDateTime date);

    @Query("SELECT i FROM Invoice i WHERE i.user = :user AND i.status IN ('OPEN', 'PAID') ORDER BY i.invoiceDate DESC")
    List<Invoice> findActiveInvoicesByUser(@Param("user") User user);

    @Query("SELECT i FROM Invoice i WHERE i.taxAmount > 0 ORDER BY i.taxAmount DESC")
    List<Invoice> findInvoicesWithTax();

    @Query("SELECT i FROM Invoice i WHERE i.discountAmount > 0 ORDER BY i.discountAmount DESC")
    List<Invoice> findInvoicesWithDiscount();

    @Query("SELECT SUM(i.taxAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.paidAt >= :startDate")
    BigDecimal getTotalTaxCollectedSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT i FROM Invoice i WHERE i.description LIKE %:keyword% ORDER BY i.invoiceDate DESC")
    List<Invoice> findByDescriptionContaining(@Param("keyword") String keyword);

    @Query("SELECT i FROM Invoice i WHERE i.currency = :currency AND i.status = 'PAID'")
    List<Invoice> findPaidInvoicesByCurrency(@Param("currency") String currency);

    @Query("SELECT DATE(i.invoiceDate), COUNT(i), SUM(i.totalAmount) FROM Invoice i WHERE i.invoiceDate >= :startDate GROUP BY DATE(i.invoiceDate) ORDER BY DATE(i.invoiceDate)")
    List<Object[]> getDailyInvoiceStats(@Param("startDate") LocalDate startDate);

    @Query("SELECT i FROM Invoice i WHERE i.user.id IN :userIds ORDER BY i.invoiceDate DESC")
    List<Invoice> findByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT COUNT(DISTINCT i.user) FROM Invoice i WHERE i.status = 'PAID' AND i.paidAt >= :startDate")
    Long countUniqueCustomersWithPaidInvoicesSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT i FROM Invoice i WHERE i.hostedInvoiceUrl IS NOT NULL ORDER BY i.invoiceDate DESC")
    List<Invoice> findInvoicesWithHostedUrl();

    @Query("SELECT i FROM Invoice i WHERE i.invoicePdfUrl IS NOT NULL ORDER BY i.invoiceDate DESC")
    List<Invoice> findInvoicesWithPdf();

    boolean existsByInvoiceNumber(String invoiceNumber);

    @Query("SELECT i FROM Invoice i WHERE i.autoAdvance = false AND i.status = 'DRAFT'")
    List<Invoice> findManualInvoices();

    @Query("SELECT i FROM Invoice i WHERE i.collectionMethod = :method")
    List<Invoice> findByCollectionMethod(@Param("method") String collectionMethod);

    @Query("SELECT i FROM Invoice i WHERE i.status = 'OPEN' AND (i.sentAt IS NULL OR i.sentAt < :resendAfter)")
    List<Invoice> findUnsentOrOldInvoices(@Param("resendAfter") LocalDateTime resendAfter);

    @Query("SELECT MONTH(i.invoiceDate), YEAR(i.invoiceDate), COUNT(i), SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' GROUP BY MONTH(i.invoiceDate), YEAR(i.invoiceDate) ORDER BY YEAR(i.invoiceDate), MONTH(i.invoiceDate)")
    List<Object[]> getMonthlyRevenueStats();

    @Query("SELECT i FROM Invoice i WHERE i.periodStart IS NOT NULL AND i.periodEnd IS NOT NULL AND i.periodStart <= :date AND i.periodEnd >= :date")
    List<Invoice> findInvoicesForPeriod(@Param("date") LocalDate date);
}

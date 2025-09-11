package com.impulse.billing.model;

import com.impulse.user.model.User;
import com.impulse.billing.model.Subscription;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IMPULSE LEAN v1 - Payment Domain Model
 * 
 * Represents payment transactions and billing history
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(name = "stripe_payment_intent_id", unique = true)
    private String stripePaymentIntentId;

    @Column(name = "stripe_charge_id")
    private String stripeChargeId;

    @Column(name = "stripe_invoice_id")
    private String stripeInvoiceId;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "description", length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "receipt_url", length = 1000)
    private String receiptUrl;

    @Column(name = "metadata", length = 1000)
    private String metadata;

    @Column(name = "transaction_fee", precision = 10, scale = 2)
    private BigDecimal transactionFee;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    // Enums

    public enum PaymentStatus {
        PENDING("Pending"),
        PROCESSING("Processing"),
        SUCCEEDED("Succeeded"),
        FAILED("Failed"),
        CANCELED("Canceled"),
        REFUNDED("Refunded"),
        PARTIALLY_REFUNDED("Partially Refunded"),
        REQUIRES_ACTION("Requires Action"),
        REQUIRES_CONFIRMATION("Requires Confirmation"),
        REQUIRES_PAYMENT_METHOD("Requires Payment Method");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        public boolean isSuccessful() {
            return this == SUCCEEDED;
        }

        public boolean isFinal() {
            return this == SUCCEEDED || this == FAILED || this == CANCELED || this == REFUNDED;
        }

        public boolean requiresAction() {
            return this == REQUIRES_ACTION || this == REQUIRES_CONFIRMATION || this == REQUIRES_PAYMENT_METHOD;
        }
    }

    public enum PaymentMethod {
        CARD("Credit/Debit Card"),
        BANK_TRANSFER("Bank Transfer"),
        PAYPAL("PayPal"),
        APPLE_PAY("Apple Pay"),
        GOOGLE_PAY("Google Pay"),
        SEPA_DEBIT("SEPA Direct Debit"),
        ACH_DEBIT("ACH Direct Debit"),
        KLARNA("Klarna"),
        AFTERPAY("Afterpay"),
        WALLET("Digital Wallet"),
        OTHER("Other");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        public boolean isInstant() {
            return this == CARD || this == APPLE_PAY || this == GOOGLE_PAY || this == WALLET;
        }
    }

    // Constructors
    public Payment() {}

    public Payment(User user, BigDecimal amount, String currency, PaymentMethod paymentMethod) {
        this.user = user;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
    }

    public Payment(User user, Subscription subscription, BigDecimal amount, PaymentMethod paymentMethod) {
        this.user = user;
        this.subscription = subscription;
        this.amount = amount;
        this.currency = subscription.getCurrency();
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.isRecurring = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public String getStripeChargeId() {
        return stripeChargeId;
    }

    public void setStripeChargeId(String stripeChargeId) {
        this.stripeChargeId = stripeChargeId;
    }

    public String getStripeInvoiceId() {
        return stripeInvoiceId;
    }

    public void setStripeInvoiceId(String stripeInvoiceId) {
        this.stripeInvoiceId = stripeInvoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public BigDecimal getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    // Business Methods
    public void markAsSucceeded() {
        this.status = PaymentStatus.SUCCEEDED;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failedAt = LocalDateTime.now();
        this.failureReason = reason;
    }

    public void markAsRefunded(BigDecimal refundAmount) {
        if (refundAmount.compareTo(this.amount) >= 0) {
            this.status = PaymentStatus.REFUNDED;
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
        }
        this.refundedAt = LocalDateTime.now();
        this.refundAmount = refundAmount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean canBeRetried() {
        return this.status == PaymentStatus.FAILED && this.retryCount < 3;
    }

    public boolean isSuccessful() {
        return this.status != null && this.status.isSuccessful();
    }

    public boolean requiresAction() {
        return this.status != null && this.status.requiresAction();
    }

    public BigDecimal getRefundableAmount() {
        if (this.refundAmount == null) {
            return this.amount;
        }
        return this.amount.subtract(this.refundAmount);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", paymentMethod=" + paymentMethod +
                ", processedAt=" + processedAt +
                '}';
    }
}

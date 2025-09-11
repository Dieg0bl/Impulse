package com.impulse.billing.model;

import com.impulse.user.model.User;
import com.impulse.billing.model.Subscription;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * IMPULSE LEAN v1 - Invoice Domain Model
 * 
 * Represents billing invoices and payment requests
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "stripe_invoice_id", unique = true)
    private String stripeInvoiceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "USD";

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "void_at")
    private LocalDateTime voidAt;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "invoice_pdf_url", length = 1000)
    private String invoicePdfUrl;

    @Column(name = "hosted_invoice_url", length = 1000)
    private String hostedInvoiceUrl;

    @Column(name = "metadata", length = 1000)
    private String metadata;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(name = "next_payment_attempt")
    private LocalDateTime nextPaymentAttempt;

    @Column(name = "auto_advance", nullable = false)
    private Boolean autoAdvance = true;

    @Column(name = "collection_method", length = 50)
    private String collectionMethod = "charge_automatically";

    // Enums

    public enum InvoiceStatus {
        DRAFT("Draft"),
        OPEN("Open"),
        PAID("Paid"),
        VOID("Void"),
        UNCOLLECTIBLE("Uncollectible"),
        DELETED("Deleted");

        private final String displayName;

        InvoiceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        public boolean isPaid() {
            return this == PAID;
        }

        public boolean isOpen() {
            return this == OPEN;
        }

        public boolean isFinal() {
            return this == PAID || this == VOID || this == UNCOLLECTIBLE;
        }
    }

    // Constructors
    public Invoice() {}

    public Invoice(User user, Subscription subscription, BigDecimal amount) {
        this.user = user;
        this.subscription = subscription;
        this.subtotal = amount;
        this.totalAmount = amount;
        this.currency = subscription != null ? subscription.getCurrency() : "USD";
        this.status = InvoiceStatus.DRAFT;
        this.invoiceDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(30);
        this.generateInvoiceNumber();
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

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getStripeInvoiceId() {
        return stripeInvoiceId;
    }

    public void setStripeInvoiceId(String stripeInvoiceId) {
        this.stripeInvoiceId = stripeInvoiceId;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
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

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getVoidAt() {
        return voidAt;
    }

    public void setVoidAt(LocalDateTime voidAt) {
        this.voidAt = voidAt;
    }

    public LocalDateTime getFinalizedAt() {
        return finalizedAt;
    }

    public void setFinalizedAt(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInvoicePdfUrl() {
        return invoicePdfUrl;
    }

    public void setInvoicePdfUrl(String invoicePdfUrl) {
        this.invoicePdfUrl = invoicePdfUrl;
    }

    public String getHostedInvoiceUrl() {
        return hostedInvoiceUrl;
    }

    public void setHostedInvoiceUrl(String hostedInvoiceUrl) {
        this.hostedInvoiceUrl = hostedInvoiceUrl;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getNextPaymentAttempt() {
        return nextPaymentAttempt;
    }

    public void setNextPaymentAttempt(LocalDateTime nextPaymentAttempt) {
        this.nextPaymentAttempt = nextPaymentAttempt;
    }

    public Boolean getAutoAdvance() {
        return autoAdvance;
    }

    public void setAutoAdvance(Boolean autoAdvance) {
        this.autoAdvance = autoAdvance;
    }

    public String getCollectionMethod() {
        return collectionMethod;
    }

    public void setCollectionMethod(String collectionMethod) {
        this.collectionMethod = collectionMethod;
    }

    // Business Methods
    public void calculateTotal() {
        this.totalAmount = this.subtotal.add(this.taxAmount).subtract(this.discountAmount);
    }

    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markAsVoid() {
        this.status = InvoiceStatus.VOID;
        this.voidAt = LocalDateTime.now();
    }

    public void finalizeInvoice() {
        this.status = InvoiceStatus.OPEN;
        this.finalizedAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.sentAt = LocalDateTime.now();
    }

    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    public boolean isOverdue() {
        return this.status == InvoiceStatus.OPEN && 
               this.dueDate != null && 
               this.dueDate.isBefore(LocalDate.now());
    }

    public boolean isPaid() {
        return this.status != null && this.status.isPaid();
    }

    public boolean isOpen() {
        return this.status != null && this.status.isOpen();
    }

    public boolean canBeVoided() {
        return this.status == InvoiceStatus.DRAFT || this.status == InvoiceStatus.OPEN;
    }

    public long getDaysUntilDue() {
        if (this.dueDate == null) return 0;
        return LocalDate.now().until(this.dueDate).getDays();
    }

    private void generateInvoiceNumber() {
        // Generate invoice number format: INV-YYYY-MMDD-XXXX
        LocalDate now = LocalDate.now();
        String dateStr = String.format("%d-%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String randomSuffix = String.format("%04d", (int)(Math.random() * 10000));
        this.invoiceNumber = "INV-" + dateStr + "-" + randomSuffix;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", currency='" + currency + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
}

package com.impulse.billing.service;

import com.impulse.billing.model.Invoice;
import com.impulse.user.model.User;
import com.impulse.billing.model.Subscription;
import com.impulse.billing.model.Payment;
import com.impulse.billing.repository.InvoiceRepository;
import com.impulse.billing.repository.PaymentRepository;
import com.impulse.billing.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestión de facturas e invoices
 * Maneja generación automática, vencimientos, y analytics de billing
 */
@Service
@Transactional
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // @Autowired
    // private NotificationService notificationService;

    // @Autowired
    // private PdfGeneratorService pdfGeneratorService;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.21"); // 21% IVA por defecto

    /**
     * Generar invoice para subscription
     */
    public Invoice generateInvoiceForSubscription(Subscription subscription) {
        // Verificar que no exista invoice pendiente
        Optional<Invoice> existingInvoice = invoiceRepository
            .findPendingInvoiceBySubscription(subscription);
        
        if (existingInvoice.isPresent()) {
            return existingInvoice.get();
        }

        BigDecimal amount = getSubscriptionAmount(subscription.getPlan());
        BigDecimal taxAmount = amount.multiply(TAX_RATE);
        BigDecimal totalAmount = amount.add(taxAmount);

        Invoice invoice = new Invoice();
        invoice.setUser(subscription.getUser());
        invoice.setSubscription(subscription);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setAmount(amount);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus(Invoice.InvoiceStatus.PENDING);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(30)); // 30 días para pagar
        invoice.setBillingPeriodStart(subscription.getCurrentPeriodStart());
        invoice.setBillingPeriodEnd(subscription.getCurrentPeriodEnd());

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Generar PDF
        generateInvoicePdf(savedInvoice);

        // Enviar notificación
        // notificationService.sendInvoiceGeneratedNotification(subscription.getUser(), savedInvoice);

        return savedInvoice;
    }

    /**
     * Marcar invoice como pagada
     */
    public Invoice markInvoiceAsPaid(Long invoiceId, Payment payment) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice no encontrada"));

        if (invoice.getStatus() != Invoice.InvoiceStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden marcar como pagadas las invoices pendientes");
        }

        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setPayment(payment);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Enviar notificación de pago
        // notificationService.sendInvoicePaidNotification(invoice.getUser(), savedInvoice);

        return savedInvoice;
    }

    /**
     * Cancelar invoice
     */
    public Invoice cancelInvoice(Long invoiceId, String reason) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice no encontrada"));

        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("No se puede cancelar una invoice pagada");
        }

        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        invoice.setCancelledAt(LocalDateTime.now());
        invoice.setCancellationReason(reason);

        return invoiceRepository.save(invoice);
    }

    /**
     * Procesar invoices vencidas
     */
    public void processOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now());

        for (Invoice invoice : overdueInvoices) {
            if (invoice.getStatus() == Invoice.InvoiceStatus.PENDING) {
                invoice.setStatus(Invoice.InvoiceStatus.OVERDUE);
                invoiceRepository.save(invoice);

                // Enviar notificación de vencimiento
                // notificationService.sendInvoiceOverdueNotification(invoice.getUser(), invoice);

                // Suspender subscription si aplica
                handleOverdueSubscription(invoice.getSubscription());
            }
        }
    }

    /**
     * Generar invoices automáticas para todas las subscriptions
     */
    public void generateAutomaticInvoices() {
        List<Subscription> subscriptionsForBilling = subscriptionRepository
            .findSubscriptionsForBilling(LocalDateTime.now());

        for (Subscription subscription : subscriptionsForBilling) {
            try {
                generateInvoiceForSubscription(subscription);
            } catch (Exception e) {
                // Log error pero continuar con otros
                System.err.println("Error generando invoice para subscription " + 
                    subscription.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Obtener invoices del usuario
     */
    @Transactional(readOnly = true)
    public Page<Invoice> getUserInvoices(User user, Pageable pageable) {
        return invoiceRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    /**
     * Obtener invoice por número
     */
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    /**
     * Obtener invoices pendientes
     */
    @Transactional(readOnly = true)
    public List<Invoice> getPendingInvoices() {
        return invoiceRepository.findByStatus(Invoice.InvoiceStatus.PENDING);
    }

    /**
     * Obtener invoices vencidas
     */
    @Transactional(readOnly = true)
    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDateTime.now());
    }

    /**
     * Obtener total facturado en rango de fechas
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalInvoicedAmount(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.getTotalInvoicedAmountByDateRange(startDate, endDate);
    }

    /**
     * Obtener total pagado en rango de fechas
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidAmount(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.getTotalPaidAmountByDateRange(startDate, endDate);
    }

    /**
     * Obtener estadísticas de revenue mensual
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyRevenueStats(int year) {
        return invoiceRepository.getMonthlyRevenueStats(year);
    }

    /**
     * Obtener distribución de invoices por estado
     */
    @Transactional(readOnly = true)
    public Map<Invoice.InvoiceStatus, Long> getInvoiceStatusDistribution() {
        return invoiceRepository.getInvoiceStatusDistribution();
    }

    /**
     * Obtener revenue promedio por usuario
     */
    @Transactional(readOnly = true)
    public BigDecimal getAverageRevenuePerUser(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.getAverageRevenuePerUser(startDate, endDate);
    }

    /**
     * Obtener invoices por subscription plan
     */
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesBySubscriptionPlan(String planName, LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.findBySubscriptionPlanAndDateRange(planName, startDate, endDate);
    }

    /**
     * Reenviar invoice por email
     */
    public void resendInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice no encontrada"));

        // Regenerar PDF si es necesario
        if (invoice.getPdfPath() == null || invoice.getPdfPath().isEmpty()) {
            generateInvoicePdf(invoice);
        }

        // Reenviar notificación
        // notificationService.sendInvoiceGeneratedNotification(invoice.getUser(), invoice);
    }

    /**
     * Aplicar descuento a invoice
     */
    public Invoice applyDiscount(Long invoiceId, BigDecimal discountAmount, String discountReason) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice no encontrada"));

        if (invoice.getStatus() != Invoice.InvoiceStatus.PENDING) {
            throw new IllegalStateException("Solo se puede aplicar descuento a invoices pendientes");
        }

        BigDecimal newAmount = invoice.getAmount().subtract(discountAmount);
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            newAmount = BigDecimal.ZERO;
        }

        BigDecimal newTaxAmount = newAmount.multiply(TAX_RATE);
        BigDecimal newTotalAmount = newAmount.add(newTaxAmount);

        invoice.setAmount(newAmount);
        invoice.setTaxAmount(newTaxAmount);
        invoice.setTotalAmount(newTotalAmount);
        invoice.setDiscountAmount(discountAmount);
        invoice.setDiscountReason(discountReason);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Regenerar PDF con descuento
        generateInvoicePdf(savedInvoice);

        return savedInvoice;
    }

    /**
     * Generar prórroga para invoice vencida
     */
    public Invoice extendInvoiceDueDate(Long invoiceId, int daysExtension, String reason) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice no encontrada"));

        LocalDateTime newDueDate = invoice.getDueDate().plusDays(daysExtension);
        invoice.setDueDate(newDueDate);
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.OVERDUE) {
            invoice.setStatus(Invoice.InvoiceStatus.PENDING);
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Notificar prórroga
        // notificationService.sendInvoiceExtensionNotification(invoice.getUser(), savedInvoice, reason);

        return savedInvoice;
    }

    /**
     * Obtener siguiente número de invoice
     */
    private String generateInvoiceNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        Long lastNumber = invoiceRepository.getLastInvoiceNumberForYear(year);
        Long nextNumber = (lastNumber != null ? lastNumber : 0) + 1;
        
        return String.format("INV-%s-%06d", year, nextNumber);
    }

    /**
     * Generar PDF de la invoice
     */
    private void generateInvoicePdf(Invoice invoice) {
        try {
            // String pdfPath = pdfGeneratorService.generateInvoicePdf(invoice);
            invoice.setPdfPath(pdfPath);
            invoiceRepository.save(invoice);
        } catch (Exception e) {
            System.err.println("Error generando PDF para invoice " + invoice.getId() + ": " + e.getMessage());
        }
    }

    /**
     * Manejar subscription vencida
     */
    private void handleOverdueSubscription(Subscription subscription) {
        if (subscription.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
            subscription.setStatus(Subscription.SubscriptionStatus.PAST_DUE);
            subscriptionRepository.save(subscription);
        }
    }

    /**
     * Obtener monto de subscription según plan
     */
    private BigDecimal getSubscriptionAmount(Subscription.SubscriptionPlan plan) {
        switch (plan) {
            case FREE:
                return BigDecimal.ZERO;
            case PREMIUM:
                return new BigDecimal("9.99");
            case PRO:
                return new BigDecimal("19.99");
            case ENTERPRISE:
                return new BigDecimal("49.99");
            default:
                return BigDecimal.ZERO;
        }
    }
}

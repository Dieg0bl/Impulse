package com.impulse.lean.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generación de PDFs
 * Genera invoices, reportes y documentos de billing
 */
@Service
public class PdfGeneratorService {

    private static final String PDF_DIRECTORY = "uploads/invoices/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Mock Invoice class para compilación
     */
    public static class MockInvoice {
        private Long id;
        private String invoiceNumber;
        private BigDecimal amount;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private LocalDateTime createdAt;
        private LocalDateTime dueDate;
        private LocalDateTime billingPeriodStart;
        private LocalDateTime billingPeriodEnd;
        private String discountReason;
        private MockUser user;
        private MockSubscription subscription;

        // Getters y setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getDueDate() { return dueDate; }
        public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
        public LocalDateTime getBillingPeriodStart() { return billingPeriodStart; }
        public void setBillingPeriodStart(LocalDateTime billingPeriodStart) { this.billingPeriodStart = billingPeriodStart; }
        public LocalDateTime getBillingPeriodEnd() { return billingPeriodEnd; }
        public void setBillingPeriodEnd(LocalDateTime billingPeriodEnd) { this.billingPeriodEnd = billingPeriodEnd; }
        public String getDiscountReason() { return discountReason; }
        public void setDiscountReason(String discountReason) { this.discountReason = discountReason; }
        public MockUser getUser() { return user; }
        public void setUser(MockUser user) { this.user = user; }
        public MockSubscription getSubscription() { return subscription; }
        public void setSubscription(MockSubscription subscription) { this.subscription = subscription; }
    }

    /**
     * Mock User class
     */
    public static class MockUser {
        private Long id;
        private String name;
        private String email;
        private String address;
        private String city;
        private String country;
        private String taxId;

        // Getters y setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getTaxId() { return taxId; }
        public void setTaxId(String taxId) { this.taxId = taxId; }
    }

    /**
     * Mock Subscription class
     */
    public static class MockSubscription {
        private String plan;
        private String billingCycle;

        public String getPlan() { return plan; }
        public void setPlan(String plan) { this.plan = plan; }
        public String getBillingCycle() { return billingCycle; }
        public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    }

    /**
     * Generar PDF de invoice
     */
    public String generateInvoicePdf(MockInvoice invoice) {
        try {
            // Crear directorio si no existe
            File directory = new File(PDF_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generar nombre de archivo
            String fileName = String.format("invoice_%s_%s.html", 
                invoice.getInvoiceNumber(), 
                System.currentTimeMillis());
            
            String filePath = PDF_DIRECTORY + fileName;

            // Generar HTML (en implementación real usar librerías como iText, Flying Saucer, etc.)
            String htmlContent = generateInvoiceHtml(invoice);

            // Escribir archivo HTML (simular PDF)
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(htmlContent);
            }

            System.out.println("Generated invoice PDF: " + filePath);
            return filePath;

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de invoice: " + e.getMessage(), e);
        }
    }

    /**
     * Generar contenido HTML de la invoice
     */
    private String generateInvoiceHtml(MockInvoice invoice) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset='UTF-8'>\n");
        html.append("    <title>Invoice ").append(invoice.getInvoiceNumber()).append("</title>\n");
        html.append("    <style>\n");
        html.append(getInvoiceCss());
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // Header
        html.append("    <div class='header'>\n");
        html.append("        <h1>IMPULSE LEAN</h1>\n");
        html.append("        <p>Sistema de Gestión Personal y Coaching</p>\n");
        html.append("    </div>\n");
        
        // Invoice info
        html.append("    <div class='invoice-info'>\n");
        html.append("        <div class='invoice-details'>\n");
        html.append("            <h2>FACTURA</h2>\n");
        html.append("            <p><strong>Número:</strong> ").append(invoice.getInvoiceNumber()).append("</p>\n");
        html.append("            <p><strong>Fecha:</strong> ").append(invoice.getCreatedAt().format(DATE_FORMATTER)).append("</p>\n");
        html.append("            <p><strong>Vencimiento:</strong> ").append(invoice.getDueDate().format(DATE_FORMATTER)).append("</p>\n");
        html.append("        </div>\n");
        
        // Customer info
        html.append("        <div class='customer-details'>\n");
        html.append("            <h3>FACTURAR A:</h3>\n");
        if (invoice.getUser() != null) {
            html.append("            <p><strong>").append(invoice.getUser().getName() != null ? invoice.getUser().getName() : "Cliente").append("</strong></p>\n");
            html.append("            <p>").append(invoice.getUser().getEmail() != null ? invoice.getUser().getEmail() : "email@ejemplo.com").append("</p>\n");
            if (invoice.getUser().getAddress() != null) {
                html.append("            <p>").append(invoice.getUser().getAddress()).append("</p>\n");
            }
            if (invoice.getUser().getCity() != null && invoice.getUser().getCountry() != null) {
                html.append("            <p>").append(invoice.getUser().getCity()).append(", ").append(invoice.getUser().getCountry()).append("</p>\n");
            }
            if (invoice.getUser().getTaxId() != null) {
                html.append("            <p><strong>NIF/CIF:</strong> ").append(invoice.getUser().getTaxId()).append("</p>\n");
            }
        }
        html.append("        </div>\n");
        html.append("    </div>\n");
        
        // Items table
        html.append("    <table class='items-table'>\n");
        html.append("        <thead>\n");
        html.append("            <tr>\n");
        html.append("                <th>Descripción</th>\n");
        html.append("                <th>Período</th>\n");
        html.append("                <th>Cantidad</th>\n");
        html.append("                <th>Precio Unit.</th>\n");
        html.append("                <th>Total</th>\n");
        html.append("            </tr>\n");
        html.append("        </thead>\n");
        html.append("        <tbody>\n");
        
        // Main item
        String description = "Subscription Plan";
        if (invoice.getSubscription() != null) {
            description = String.format("Plan %s (%s)", 
                invoice.getSubscription().getPlan() != null ? invoice.getSubscription().getPlan() : "Premium",
                invoice.getSubscription().getBillingCycle() != null ? invoice.getSubscription().getBillingCycle() : "Monthly");
        }
        
        String period = "";
        if (invoice.getBillingPeriodStart() != null && invoice.getBillingPeriodEnd() != null) {
            period = String.format("%s - %s", 
                invoice.getBillingPeriodStart().format(DATE_FORMATTER),
                invoice.getBillingPeriodEnd().format(DATE_FORMATTER));
        }
        
        html.append("            <tr>\n");
        html.append("                <td>").append(description).append("</td>\n");
        html.append("                <td>").append(period).append("</td>\n");
        html.append("                <td>1</td>\n");
        html.append("                <td>€").append(formatAmount(invoice.getAmount())).append("</td>\n");
        html.append("                <td>€").append(formatAmount(invoice.getAmount())).append("</td>\n");
        html.append("            </tr>\n");
        
        // Discount row if applicable
        if (invoice.getDiscountAmount() != null && invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            html.append("            <tr>\n");
            html.append("                <td colspan='4'>Descuento");
            if (invoice.getDiscountReason() != null) {
                html.append(" (").append(invoice.getDiscountReason()).append(")");
            }
            html.append("</td>\n");
            html.append("                <td>-€").append(formatAmount(invoice.getDiscountAmount())).append("</td>\n");
            html.append("            </tr>\n");
        }
        
        html.append("        </tbody>\n");
        html.append("    </table>\n");
        
        // Totals
        html.append("    <div class='totals'>\n");
        html.append("        <div class='total-line'>\n");
        html.append("            <span>Subtotal:</span>\n");
        html.append("            <span>€").append(formatAmount(invoice.getAmount())).append("</span>\n");
        html.append("        </div>\n");
        
        if (invoice.getDiscountAmount() != null && invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            html.append("        <div class='total-line'>\n");
            html.append("            <span>Descuento:</span>\n");
            html.append("            <span>-€").append(formatAmount(invoice.getDiscountAmount())).append("</span>\n");
            html.append("        </div>\n");
        }
        
        html.append("        <div class='total-line'>\n");
        html.append("            <span>IVA (21%):</span>\n");
        html.append("            <span>€").append(formatAmount(invoice.getTaxAmount())).append("</span>\n");
        html.append("        </div>\n");
        html.append("        <div class='total-line total'>\n");
        html.append("            <span><strong>TOTAL:</strong></span>\n");
        html.append("            <span><strong>€").append(formatAmount(invoice.getTotalAmount())).append("</strong></span>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        
        // Footer
        html.append("    <div class='footer'>\n");
        html.append("        <p><strong>Términos de Pago:</strong> Pago requerido dentro de 30 días.</p>\n");
        html.append("        <p><strong>Información de Contacto:</strong> support@impulselean.com | +34 900 123 456</p>\n");
        html.append("        <p class='thank-you'>¡Gracias por tu confianza en IMPULSE LEAN!</p>\n");
        html.append("    </div>\n");
        
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }

    /**
     * CSS para styling de la invoice
     */
    private String getInvoiceCss() {
        return """
            body {
                font-family: Arial, sans-serif;
                line-height: 1.6;
                margin: 0;
                padding: 20px;
                color: #333;
            }
            
            .header {
                text-align: center;
                border-bottom: 3px solid #007bff;
                padding-bottom: 20px;
                margin-bottom: 30px;
            }
            
            .header h1 {
                color: #007bff;
                margin: 0;
                font-size: 2.5em;
            }
            
            .header p {
                margin: 5px 0 0 0;
                color: #666;
            }
            
            .invoice-info {
                display: flex;
                justify-content: space-between;
                margin-bottom: 30px;
            }
            
            .invoice-details, .customer-details {
                width: 48%;
            }
            
            .invoice-details h2 {
                color: #007bff;
                margin: 0 0 10px 0;
            }
            
            .customer-details h3 {
                margin: 0 0 10px 0;
                color: #333;
            }
            
            .items-table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 30px;
            }
            
            .items-table th,
            .items-table td {
                border: 1px solid #ddd;
                padding: 12px;
                text-align: left;
            }
            
            .items-table th {
                background-color: #f8f9fa;
                font-weight: bold;
                color: #333;
            }
            
            .items-table td:last-child,
            .items-table th:last-child {
                text-align: right;
            }
            
            .totals {
                margin-left: auto;
                width: 300px;
                border-top: 2px solid #007bff;
                padding-top: 15px;
            }
            
            .total-line {
                display: flex;
                justify-content: space-between;
                padding: 5px 0;
                border-bottom: 1px solid #eee;
            }
            
            .total-line.total {
                border-top: 2px solid #333;
                border-bottom: 3px double #333;
                margin-top: 10px;
                padding-top: 10px;
                font-size: 1.2em;
            }
            
            .footer {
                margin-top: 50px;
                padding-top: 20px;
                border-top: 1px solid #ddd;
                font-size: 0.9em;
                color: #666;
            }
            
            .thank-you {
                text-align: center;
                font-style: italic;
                color: #007bff;
                margin-top: 20px;
            }
            """;
    }

    /**
     * Formatear amount para mostrar
     */
    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f", amount);
    }

    /**
     * Generar reporte de pagos en PDF
     */
    public String generatePaymentReport(String title, String content) {
        try {
            File directory = new File(PDF_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = String.format("report_%s_%s.html", 
                title.replaceAll("[^a-zA-Z0-9]", "_"), 
                System.currentTimeMillis());
            
            String filePath = PDF_DIRECTORY + fileName;

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("    <meta charset='UTF-8'>\n");
            html.append("    <title>").append(title).append("</title>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("    <h1>").append(title).append("</h1>\n");
            html.append("    <div>").append(content).append("</div>\n");
            html.append("</body>\n");
            html.append("</html>");

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(html.toString());
            }

            return filePath;

        } catch (IOException e) {
            throw new RuntimeException("Error generando reporte PDF: " + e.getMessage(), e);
        }
    }
}

package com.revenueos.revenue;

import com.revenueos.expense.ExpenseRepository;
import com.revenueos.expense.ExpenseStatus;
import com.revenueos.invoice.Invoice;
import com.revenueos.invoice.InvoiceRepository;
import com.revenueos.invoice.InvoiceStatus;
import com.revenueos.invoice.PaymentRepository;
import com.revenueos.revenue.dto.MonthlyRevenuePoint;
import com.revenueos.revenue.dto.RevenueSummaryResponse;
import com.revenueos.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Read-only financial roll-ups computed from invoices, payments and expenses.
 * Nothing is stored denormalized — these are derived on demand.
 */
@Service
public class RevenueService {

    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("yyyy-MM");

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;

    public RevenueService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository,
                          ExpenseRepository expenseRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.expenseRepository = expenseRepository;
    }

    private static boolean counts(Invoice invoice) {
        return invoice.getStatus() != InvoiceStatus.DRAFT && invoice.getStatus() != InvoiceStatus.VOID;
    }

    @Transactional(readOnly = true)
    public RevenueSummaryResponse summary() {
        UUID org = TenantContext.getOrganizationId();
        BigDecimal invoiced = invoiceRepository.findByOrganizationId(org).stream()
                .filter(RevenueService::counts).map(Invoice::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal collected = paymentRepository.findByOrganizationId(org).stream()
                .map(p -> p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expenses = expenseRepository.findByOrganizationIdAndStatus(org, ExpenseStatus.APPROVED).stream()
                .map(e -> e.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new RevenueSummaryResponse(
                invoiced, collected, invoiced.subtract(collected), expenses, collected.subtract(expenses));
    }

    @Transactional(readOnly = true)
    public List<MonthlyRevenuePoint> monthly(int months) {
        UUID org = TenantContext.getOrganizationId();
        int window = Math.max(1, Math.min(months, 24));

        Map<String, BigDecimal> invoiced = new LinkedHashMap<>();
        Map<String, BigDecimal> collected = new LinkedHashMap<>();
        YearMonth start = YearMonth.now().minusMonths(window - 1L);
        for (int i = 0; i < window; i++) {
            String key = start.plusMonths(i).format(MONTH);
            invoiced.put(key, BigDecimal.ZERO);
            collected.put(key, BigDecimal.ZERO);
        }

        invoiceRepository.findByOrganizationId(org).stream().filter(RevenueService::counts).forEach(inv ->
                addTo(invoiced, inv.getIssueDate(), inv.getTotal()));
        paymentRepository.findByOrganizationId(org).forEach(p ->
                addTo(collected, p.getPaidOn(), p.getAmount()));

        List<MonthlyRevenuePoint> points = new ArrayList<>();
        invoiced.forEach((month, inv) -> points.add(new MonthlyRevenuePoint(month, inv, collected.get(month))));
        return points;
    }

    private void addTo(Map<String, BigDecimal> bucket, LocalDate date, BigDecimal amount) {
        if (date == null || amount == null) return;
        String key = YearMonth.from(date).format(MONTH);
        if (bucket.containsKey(key)) {
            bucket.merge(key, amount, BigDecimal::add);
        }
    }
}

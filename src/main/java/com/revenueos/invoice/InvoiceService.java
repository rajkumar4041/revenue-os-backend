package com.revenueos.invoice;

import com.revenueos.common.exception.ApiException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.common.web.PageResponse;
import com.revenueos.invoice.dto.CreateInvoiceItemRequest;
import com.revenueos.invoice.dto.CreateInvoiceRequest;
import com.revenueos.invoice.dto.InvoiceDetailResponse;
import com.revenueos.invoice.dto.InvoiceResponse;
import com.revenueos.invoice.dto.RecordPaymentRequest;
import com.revenueos.invoice.dto.UpdateInvoiceRequest;
import com.revenueos.tenant.TenantContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceItemMapper invoiceItemMapper;
    private final PaymentMapper paymentMapper;

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceItemRepository invoiceItemRepository,
                          PaymentRepository paymentRepository, InvoiceItemMapper invoiceItemMapper,
                          PaymentMapper paymentMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceItemMapper = invoiceItemMapper;
        this.paymentMapper = paymentMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponse> list(UUID clientId, InvoiceStatus status, Pageable pageable) {
        return PageResponse.of(
                invoiceRepository.search(TenantContext.getOrganizationId(), clientId, status, pageable),
                this::toResponse);
    }

    @Transactional(readOnly = true)
    public InvoiceDetailResponse getDetail(UUID id) {
        return toDetail(requireInvoice(id));
    }

    @Transactional
    public InvoiceDetailResponse create(CreateInvoiceRequest request) {
        UUID org = TenantContext.getOrganizationId();
        Invoice invoice = new Invoice();
        invoice.setOrganizationId(org);
        invoice.setInvoiceNumber(nextInvoiceNumber(org));
        invoice.setClientId(request.clientId());
        invoice.setProjectId(request.projectId());
        invoice.setIssueDate(request.issueDate());
        invoice.setDueDate(request.dueDate());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setCurrency(request.currency() != null ? request.currency() : "USD");
        invoice.setNotes(request.notes());
        applyTotals(invoice, request.taxAmount(), request.items());
        invoice = invoiceRepository.save(invoice);
        saveItems(invoice, request.items());
        return toDetail(invoice);
    }

    @Transactional
    public InvoiceDetailResponse update(UUID id, UpdateInvoiceRequest request) {
        Invoice invoice = requireInvoice(id);
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new ApiException(HttpStatus.CONFLICT, "Only draft invoices can be edited");
        }
        invoice.setProjectId(request.projectId());
        invoice.setIssueDate(request.issueDate());
        invoice.setDueDate(request.dueDate());
        if (request.currency() != null) {
            invoice.setCurrency(request.currency());
        }
        invoice.setNotes(request.notes());
        invoiceItemRepository.deleteByInvoiceId(invoice.getId());
        applyTotals(invoice, request.taxAmount(), request.items());
        invoice = invoiceRepository.save(invoice);
        saveItems(invoice, request.items());
        return toDetail(invoice);
    }

    @Transactional
    public InvoiceDetailResponse send(UUID id) {
        Invoice invoice = requireInvoice(id);
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new ApiException(HttpStatus.CONFLICT, "Only draft invoices can be sent");
        }
        invoice.setStatus(InvoiceStatus.SENT);
        return toDetail(invoiceRepository.save(invoice));
    }

    @Transactional
    public InvoiceDetailResponse voidInvoice(UUID id) {
        Invoice invoice = requireInvoice(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new ApiException(HttpStatus.CONFLICT, "Paid invoices cannot be voided");
        }
        invoice.setStatus(InvoiceStatus.VOID);
        return toDetail(invoiceRepository.save(invoice));
    }

    /** Records a collected payment and recomputes the invoice's paid status. */
    @Transactional
    public InvoiceDetailResponse recordPayment(UUID id, RecordPaymentRequest request) {
        Invoice invoice = requireInvoice(id);
        if (invoice.getStatus() != InvoiceStatus.SENT && invoice.getStatus() != InvoiceStatus.PARTIALLY_PAID) {
            throw new ApiException(HttpStatus.CONFLICT, "Payments can only be recorded against sent invoices");
        }
        Payment payment = new Payment();
        payment.setOrganizationId(invoice.getOrganizationId());
        payment.setInvoiceId(invoice.getId());
        payment.setAmount(request.amount());
        payment.setPaidOn(request.paidOn());
        if (request.method() != null) {
            payment.setMethod(request.method());
        }
        payment.setReference(request.reference());
        paymentRepository.save(payment);

        BigDecimal paid = paymentRepository.sumForInvoice(invoice.getId());
        invoice.setStatus(paid.compareTo(invoice.getTotal()) >= 0 ? InvoiceStatus.PAID : InvoiceStatus.PARTIALLY_PAID);
        return toDetail(invoiceRepository.save(invoice));
    }

    // --- helpers ---

    private Invoice requireInvoice(UUID id) {
        return invoiceRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Invoice", id));
    }

    private String nextInvoiceNumber(UUID org) {
        long count = invoiceRepository.countByOrganizationId(org);
        return String.format("INV-%04d", count + 1);
    }

    private void applyTotals(Invoice invoice, BigDecimal taxAmount, List<CreateInvoiceItemRequest> items) {
        BigDecimal subtotal = Optional.ofNullable(items).orElseGet(List::of).stream()
                .map(i -> i.quantity().multiply(i.unitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(tax);
        invoice.setTotal(subtotal.add(tax));
    }

    private void saveItems(Invoice invoice, List<CreateInvoiceItemRequest> items) {
        if (items == null) return;
        for (CreateInvoiceItemRequest request : items) {
            InvoiceItem item = new InvoiceItem();
            item.setOrganizationId(invoice.getOrganizationId());
            item.setInvoiceId(invoice.getId());
            item.setDescription(request.description());
            item.setQuantity(request.quantity());
            item.setUnitPrice(request.unitPrice());
            item.setAmount(request.quantity().multiply(request.unitPrice()));
            invoiceItemRepository.save(item);
        }
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        BigDecimal paid = paymentRepository.sumForInvoice(invoice.getId());
        return new InvoiceResponse(
                invoice.getId(), invoice.getInvoiceNumber(), invoice.getClientId(), invoice.getProjectId(),
                invoice.getIssueDate(), invoice.getDueDate(), invoice.getStatus(), invoice.getCurrency(),
                invoice.getSubtotal(), invoice.getTaxAmount(), invoice.getTotal(), paid,
                invoice.getTotal().subtract(paid));
    }

    private InvoiceDetailResponse toDetail(Invoice invoice) {
        List<InvoiceItem> items = new ArrayList<>(invoiceItemRepository.findByInvoiceId(invoice.getId()));
        List<Payment> payments = paymentRepository.findByInvoiceIdOrderByPaidOnAsc(invoice.getId());
        return new InvoiceDetailResponse(
                toResponse(invoice),
                invoiceItemMapper.toResponseList(items),
                paymentMapper.toResponseList(payments));
    }
}

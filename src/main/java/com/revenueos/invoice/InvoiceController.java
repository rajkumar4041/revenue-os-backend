package com.revenueos.invoice;

import com.revenueos.common.web.PageResponse;
import com.revenueos.invoice.dto.CreateInvoiceRequest;
import com.revenueos.invoice.dto.InvoiceDetailResponse;
import com.revenueos.invoice.dto.InvoiceResponse;
import com.revenueos.invoice.dto.RecordPaymentRequest;
import com.revenueos.invoice.dto.UpdateInvoiceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVOICE_VIEW')")
    @Operation(operationId = "listInvoices", summary = "List invoices, optionally filtered by client and status")
    public PageResponse<InvoiceResponse> list(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) InvoiceStatus status,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return invoiceService.list(clientId, status, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVOICE_VIEW')")
    @Operation(operationId = "getInvoice", summary = "Get an invoice with its line items and payments")
    public InvoiceDetailResponse get(@PathVariable UUID id) {
        return invoiceService.getDetail(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVOICE_CREATE')")
    @Operation(operationId = "createInvoice", summary = "Create a draft invoice")
    public ResponseEntity<InvoiceDetailResponse> create(@Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVOICE_CREATE')")
    @Operation(operationId = "updateInvoice", summary = "Update a draft invoice")
    public InvoiceDetailResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateInvoiceRequest request) {
        return invoiceService.update(id, request);
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasAuthority('INVOICE_CREATE')")
    @Operation(operationId = "sendInvoice", summary = "Mark a draft invoice as sent")
    public InvoiceDetailResponse send(@PathVariable UUID id) {
        return invoiceService.send(id);
    }

    @PostMapping("/{id}/void")
    @PreAuthorize("hasAuthority('INVOICE_CREATE')")
    @Operation(operationId = "voidInvoice", summary = "Void an invoice")
    public InvoiceDetailResponse voidInvoice(@PathVariable UUID id) {
        return invoiceService.voidInvoice(id);
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('INVOICE_CREATE')")
    @Operation(operationId = "recordPayment", summary = "Record a payment against an invoice")
    public InvoiceDetailResponse recordPayment(@PathVariable UUID id, @Valid @RequestBody RecordPaymentRequest request) {
        return invoiceService.recordPayment(id, request);
    }
}

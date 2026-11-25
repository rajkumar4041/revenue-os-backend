package com.revenueos.invoice.dto;

import com.revenueos.invoice.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        String invoiceNumber,
        UUID clientId,
        UUID projectId,
        LocalDate issueDate,
        LocalDate dueDate,
        InvoiceStatus status,
        String currency,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal total,
        BigDecimal amountPaid,
        BigDecimal balance
) {}

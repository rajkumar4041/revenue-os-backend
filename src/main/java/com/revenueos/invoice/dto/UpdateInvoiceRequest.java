package com.revenueos.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdateInvoiceRequest(
        UUID projectId,

        @NotNull(message = "Issue date is required")
        LocalDate issueDate,

        LocalDate dueDate,
        String currency,
        BigDecimal taxAmount,
        String notes,

        @Valid
        List<CreateInvoiceItemRequest> items
) {}

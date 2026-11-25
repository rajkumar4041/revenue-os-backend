package com.revenueos.invoice.dto;

import com.revenueos.invoice.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordPaymentRequest(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Payment date is required")
        LocalDate paidOn,

        PaymentMethod method,
        String reference
) {}

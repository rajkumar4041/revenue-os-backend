package com.revenueos.invoice.dto;

import com.revenueos.invoice.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID invoiceId,
        BigDecimal amount,
        LocalDate paidOn,
        PaymentMethod method,
        String reference
) {}

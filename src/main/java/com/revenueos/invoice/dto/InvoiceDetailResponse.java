package com.revenueos.invoice.dto;

import java.util.List;

public record InvoiceDetailResponse(
        InvoiceResponse invoice,
        List<InvoiceItemResponse> items,
        List<PaymentResponse> payments
) {}

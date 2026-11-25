package com.revenueos.revenue.dto;

import java.math.BigDecimal;

public record RevenueSummaryResponse(
        BigDecimal totalInvoiced,
        BigDecimal totalCollected,
        BigDecimal outstanding,
        BigDecimal totalExpenses,
        BigDecimal netProfit
) {}

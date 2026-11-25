package com.revenueos.revenue.dto;

import java.math.BigDecimal;

public record MonthlyRevenuePoint(
        String month,
        BigDecimal invoiced,
        BigDecimal collected
) {}

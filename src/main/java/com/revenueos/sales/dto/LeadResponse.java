package com.revenueos.sales.dto;

import com.revenueos.sales.LeadStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LeadResponse(
        UUID id,
        String name,
        String company,
        String email,
        String phone,
        String source,
        UUID assignedUserId,
        BigDecimal estimatedValue,
        LocalDate expectedCloseDate,
        String notes,
        LeadStatus status
) {}

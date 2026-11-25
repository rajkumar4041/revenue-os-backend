package com.revenueos.sales.dto;

import com.revenueos.sales.LeadStatus;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LeadRequest(
        @NotBlank(message = "Name is required")
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

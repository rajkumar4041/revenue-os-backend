package com.revenueos.sales.dto;

import com.revenueos.sales.OpportunityStage;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OpportunityRequest(
        @NotBlank(message = "Name is required")
        String name,

        UUID clientId,
        UUID ownerUserId,
        OpportunityStage stage,
        BigDecimal value,
        LocalDate expectedCloseDate,
        Integer probability,
        String notes
) {}

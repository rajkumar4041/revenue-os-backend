package com.revenueos.sales.dto;

import com.revenueos.sales.OpportunityStage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OpportunityResponse(
        UUID id,
        String name,
        UUID leadId,
        UUID clientId,
        UUID ownerUserId,
        OpportunityStage stage,
        BigDecimal value,
        LocalDate expectedCloseDate,
        Integer probability,
        String notes
) {}

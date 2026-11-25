package com.revenueos.sales.dto;

import com.revenueos.sales.OpportunityStage;
import jakarta.validation.constraints.NotNull;

public record OpportunityStageRequest(
        @NotNull(message = "Stage is required")
        OpportunityStage stage
) {}

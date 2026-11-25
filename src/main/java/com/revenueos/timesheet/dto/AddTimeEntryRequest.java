package com.revenueos.timesheet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AddTimeEntryRequest(
        @NotNull(message = "Project is required")
        UUID projectId,

        UUID taskId,

        @NotNull(message = "Work date is required")
        LocalDate workDate,

        @NotNull(message = "Hours are required")
        @PositiveOrZero(message = "Hours cannot be negative")
        BigDecimal hours,

        Boolean billable,
        String description
) {}

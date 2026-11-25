package com.revenueos.timesheet.dto;

import com.revenueos.timesheet.TimesheetStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TimesheetResponse(
        UUID id,
        UUID userId,
        LocalDate periodStart,
        LocalDate periodEnd,
        TimesheetStatus status,
        BigDecimal totalHours,
        BigDecimal billableHours,
        Instant submittedAt,
        Instant approvedAt,
        String reviewNote
) {}

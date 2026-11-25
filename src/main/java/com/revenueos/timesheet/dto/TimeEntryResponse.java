package com.revenueos.timesheet.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TimeEntryResponse(
        UUID id,
        UUID timesheetId,
        UUID projectId,
        UUID taskId,
        LocalDate workDate,
        BigDecimal hours,
        boolean billable,
        String description,
        boolean running,
        Instant startedAt
) {}

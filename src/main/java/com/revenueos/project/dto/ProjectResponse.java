package com.revenueos.project.dto;

import com.revenueos.project.BillingType;
import com.revenueos.project.ProjectPriority;
import com.revenueos.project.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String code,
        UUID clientId,
        String description,
        UUID managerUserId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal estimatedBudget,
        BigDecimal contractValue,
        BillingType billingType,
        BigDecimal hourlyRate,
        ProjectStatus status,
        ProjectPriority priority,
        Set<UUID> memberUserIds
) {}

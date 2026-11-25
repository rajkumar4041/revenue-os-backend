package com.revenueos.task.dto;

import com.revenueos.task.TaskPriority;
import com.revenueos.task.TaskStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        UUID assigneeUserId,
        UUID createdByUserId,
        TaskPriority priority,
        TaskStatus status,
        LocalDate startDate,
        LocalDate dueDate,
        BigDecimal estimatedHours,
        BigDecimal actualHours
) {}

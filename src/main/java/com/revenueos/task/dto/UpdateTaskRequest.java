package com.revenueos.task.dto;

import com.revenueos.task.TaskPriority;
import com.revenueos.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateTaskRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,
        UUID assigneeUserId,
        TaskPriority priority,
        TaskStatus status,
        LocalDate startDate,
        LocalDate dueDate,
        BigDecimal estimatedHours,
        BigDecimal actualHours
) {}

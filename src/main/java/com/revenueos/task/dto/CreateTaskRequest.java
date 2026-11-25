package com.revenueos.task.dto;

import com.revenueos.task.TaskPriority;
import com.revenueos.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotNull(message = "Project is required")
        UUID projectId,

        @NotBlank(message = "Title is required")
        String title,

        String description,
        UUID assigneeUserId,
        TaskPriority priority,
        TaskStatus status,
        LocalDate startDate,
        LocalDate dueDate,
        BigDecimal estimatedHours
) {}

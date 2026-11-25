package com.revenueos.timesheet.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartTimerRequest(
        @NotNull(message = "Project is required")
        UUID projectId,

        UUID taskId,
        String description
) {}

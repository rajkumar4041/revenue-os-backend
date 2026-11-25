package com.revenueos.department.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateDepartmentRequest(
        @NotBlank(message = "Department name is required")
        String name,

        UUID managerUserId
) {}

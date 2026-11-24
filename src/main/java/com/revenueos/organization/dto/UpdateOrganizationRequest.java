package com.revenueos.organization.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrganizationRequest(
        @NotBlank(message = "Organization name is required")
        String name
) {}

package com.revenueos.team.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateTeamRequest(
        @NotBlank(message = "Team name is required")
        String name,

        UUID managerUserId
) {}

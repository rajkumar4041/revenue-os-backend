package com.revenueos.user.dto;

import com.revenueos.user.UserStatus;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "Name is required")
        String name,

        UserStatus status
) {}

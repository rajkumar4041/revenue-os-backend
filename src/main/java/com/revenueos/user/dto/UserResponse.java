package com.revenueos.user.dto;

import com.revenueos.role.dto.RoleResponse;
import com.revenueos.user.UserStatus;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserStatus status,
        Set<RoleResponse> roles
) {}

package com.revenueos.role.dto;

import com.revenueos.permission.dto.PermissionResponse;

import java.util.Set;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        boolean system,
        Set<PermissionResponse> permissions
) {}

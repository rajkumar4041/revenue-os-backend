package com.revenueos.role.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateRoleRequest(
        @Size(max = 255)
        String description,

        Set<String> permissionCodes
) {}

package com.revenueos.auth.dto;

import java.util.List;
import java.util.UUID;

/**
 * Identity payload the frontend uses to drive navigation and permission-gated
 * UI. Roles and permissions are flattened to plain strings for convenience.
 */
public record AuthUser(
        UUID id,
        String name,
        String email,
        UUID organizationId,
        List<String> roles,
        List<String> permissions
) {}

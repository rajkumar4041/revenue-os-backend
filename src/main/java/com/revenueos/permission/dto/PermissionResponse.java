package com.revenueos.permission.dto;

import java.util.UUID;

public record PermissionResponse(UUID id, String code, String category, String description) {
}

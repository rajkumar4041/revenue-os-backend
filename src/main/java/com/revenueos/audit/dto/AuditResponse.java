package com.revenueos.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditResponse(
        UUID id,
        String action,
        String entityType,
        UUID entityId,
        UUID userId,
        String metadata,
        Instant createdAt
) {}

package com.revenueos.audit;

import com.revenueos.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Append-only trail of notable actions. Organization and user may be null for
 * platform-level events (e.g. company registration before a tenant exists).
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog extends BaseEntity {

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String action;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(columnDefinition = "text")
    private String metadata;
}

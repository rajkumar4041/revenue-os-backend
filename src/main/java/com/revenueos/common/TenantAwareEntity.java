package com.revenueos.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Base for every entity that belongs to a tenant. The {@code organizationId} is
 * always set from the authenticated user's tenant, never from client input, and
 * every query that touches these entities is scoped by it.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class TenantAwareEntity extends BaseEntity {

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;
}

package com.revenueos.tenant;

import java.util.UUID;

/**
 * Holds the tenant/user resolved from the JWT for the duration of a request.
 * Every tenant-scoped query reads the organization id from here rather than
 * trusting anything supplied by the client.
 */
public final class TenantContext {

    private record Principal(UUID organizationId, UUID userId, boolean platformAdmin) {}

    private static final ThreadLocal<Principal> HOLDER = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(UUID organizationId, UUID userId, boolean platformAdmin) {
        HOLDER.set(new Principal(organizationId, userId, platformAdmin));
    }

    public static UUID getOrganizationId() {
        Principal p = HOLDER.get();
        if (p == null || p.organizationId() == null) {
            throw new IllegalStateException("No tenant bound to the current request");
        }
        return p.organizationId();
    }

    public static UUID getUserId() {
        Principal p = HOLDER.get();
        return p == null ? null : p.userId();
    }

    public static boolean isPlatformAdmin() {
        Principal p = HOLDER.get();
        return p != null && p.platformAdmin();
    }

    public static boolean isBound() {
        return HOLDER.get() != null;
    }

    public static void clear() {
        HOLDER.remove();
    }
}

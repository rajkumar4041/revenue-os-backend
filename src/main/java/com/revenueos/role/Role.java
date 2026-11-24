package com.revenueos.role;

import com.revenueos.common.TenantAwareEntity;
import com.revenueos.permission.Permission;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * A named bundle of permissions, scoped to one organization. Admins can create
 * custom roles; the {@code system} flag marks the defaults seeded per org that
 * cannot be deleted.
 */
@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(name = "uk_roles_org_name", columnNames = {"organization_id", "name"}))
@Getter
@Setter
public class Role extends TenantAwareEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "is_system", nullable = false)
    private boolean system = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
}

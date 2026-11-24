package com.revenueos.permission;

import com.revenueos.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * A single grantable capability (e.g. CLIENT_CREATE). Permissions are a fixed
 * platform-wide catalog; roles reference them, roles are assigned to users.
 */
@Entity
@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(name = "uk_permissions_code", columnNames = "code"))
@Getter
@Setter
public class Permission extends BaseEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String category;

    @Column
    private String description;
}

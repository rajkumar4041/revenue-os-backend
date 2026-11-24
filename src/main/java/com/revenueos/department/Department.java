package com.revenueos.department;

import com.revenueos.common.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "departments", uniqueConstraints = @UniqueConstraint(name = "uk_departments_org_name", columnNames = {"organization_id", "name"}))
@Getter
@Setter
public class Department extends TenantAwareEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "manager_user_id")
    private UUID managerUserId;
}

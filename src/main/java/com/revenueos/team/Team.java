package com.revenueos.team;

import com.revenueos.common.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "teams", uniqueConstraints = @UniqueConstraint(name = "uk_teams_org_name", columnNames = {"organization_id", "name"}))
@Getter
@Setter
public class Team extends TenantAwareEntity {

    @Column(nullable = false)
    private String name;

    /** The single manager responsible for this team. */
    @Column(name = "manager_user_id")
    private UUID managerUserId;
}

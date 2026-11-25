package com.revenueos.organization;

import com.revenueos.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "organizations", uniqueConstraints = @UniqueConstraint(name = "uk_organizations_slug", columnNames = "slug"))
@Getter
@Setter
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationStatus status = OrganizationStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan", nullable = false)
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.FREE;

    /** When the free trial ends (set to 20 days out at registration). */
    @Column(name = "trial_ends_at")
    private LocalDate trialEndsAt;
}

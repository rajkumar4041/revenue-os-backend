package com.revenueos.organization.dto;

import com.revenueos.organization.OrganizationStatus;
import com.revenueos.organization.SubscriptionPlan;

import java.time.LocalDate;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String slug,
        OrganizationStatus status,
        SubscriptionPlan subscriptionPlan,
        LocalDate trialEndsAt
) {}

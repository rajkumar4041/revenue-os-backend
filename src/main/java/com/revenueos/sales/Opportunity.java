package com.revenueos.sales;

import com.revenueos.common.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
@Getter
@Setter
public class Opportunity extends TenantAwareEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "lead_id")
    private UUID leadId;

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "owner_user_id")
    private UUID ownerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpportunityStage stage = OpportunityStage.QUALIFICATION;

    @Column(precision = 15, scale = 2)
    private BigDecimal value;

    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;

    /** Win likelihood 0–100. */
    private Integer probability;

    @Column(columnDefinition = "text")
    private String notes;
}

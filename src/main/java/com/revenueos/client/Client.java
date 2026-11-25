package com.revenueos.client;

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
@Table(name = "clients")
@Getter
@Setter
public class Client extends TenantAwareEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "company_name")
    private String companyName;

    private String industry;
    private String email;
    private String phone;
    private String address;
    private String country;
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType = ClientType.DIRECT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus status = ClientStatus.PROSPECT;

    /** The user who owns this account relationship. */
    @Column(name = "account_manager_user_id")
    private UUID accountManagerUserId;

    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "contract_value", precision = 15, scale = 2)
    private BigDecimal contractValue;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(nullable = false)
    private boolean archived = false;
}

package com.revenueos.client.dto;

import com.revenueos.client.ClientStatus;
import com.revenueos.client.ClientType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String name,
        String companyName,
        String industry,
        String email,
        String phone,
        String address,
        String country,
        String website,
        ClientType clientType,
        ClientStatus status,
        UUID accountManagerUserId,
        LocalDate contractStartDate,
        LocalDate contractEndDate,
        BigDecimal contractValue,
        String paymentTerms,
        String notes,
        boolean archived
) {}

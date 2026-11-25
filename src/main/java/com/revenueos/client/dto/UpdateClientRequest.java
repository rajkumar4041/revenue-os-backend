package com.revenueos.client.dto;

import com.revenueos.client.ClientStatus;
import com.revenueos.client.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateClientRequest(
        @NotBlank(message = "Client name is required")
        String name,

        String companyName,
        String industry,

        @Email(message = "Email must be valid")
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
        String notes
) {}

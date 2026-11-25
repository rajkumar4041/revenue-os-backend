package com.revenueos.employee.dto;

import com.revenueos.employee.EmploymentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OnboardEmployeeRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        UUID userId,
        UUID departmentId,
        String designation,
        EmploymentType employmentType,
        BigDecimal costRate,
        BigDecimal billableRate,
        LocalDate hireDate
) {}

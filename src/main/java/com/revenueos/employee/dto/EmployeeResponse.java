package com.revenueos.employee.dto;

import com.revenueos.employee.EmploymentStatus;
import com.revenueos.employee.EmploymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String fullName,
        String email,
        UUID userId,
        UUID departmentId,
        String designation,
        EmploymentType employmentType,
        EmploymentStatus employmentStatus,
        BigDecimal costRate,
        BigDecimal billableRate,
        LocalDate hireDate,
        LocalDate endDate
) {}

package com.revenueos.expense.dto;

import com.revenueos.expense.ExpenseCategory;
import com.revenueos.expense.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        UUID projectId,
        ExpenseCategory category,
        BigDecimal amount,
        LocalDate incurredOn,
        String description,
        ExpenseStatus status
) {}

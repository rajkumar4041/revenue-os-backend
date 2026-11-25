package com.revenueos.expense;

import com.revenueos.expense.dto.ExpenseResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    ExpenseResponse toResponse(Expense expense);
}

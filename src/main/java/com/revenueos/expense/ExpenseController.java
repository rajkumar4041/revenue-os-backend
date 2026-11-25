package com.revenueos.expense;

import com.revenueos.common.web.PageResponse;
import com.revenueos.expense.dto.CreateExpenseRequest;
import com.revenueos.expense.dto.ExpenseResponse;
import com.revenueos.expense.dto.UpdateExpenseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@Tag(name = "Expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REVENUE_VIEW')")
    @Operation(operationId = "listExpenses", summary = "List expenses, optionally filtered by project and status")
    public PageResponse<ExpenseResponse> list(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) ExpenseStatus status,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return expenseService.list(projectId, status, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('REVENUE_MANAGE')")
    @Operation(operationId = "createExpense", summary = "Record an expense")
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('REVENUE_MANAGE')")
    @Operation(operationId = "updateExpense", summary = "Update an expense")
    public ExpenseResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateExpenseRequest request) {
        return expenseService.update(id, request);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('REVENUE_MANAGE')")
    @Operation(operationId = "approveExpense", summary = "Approve an expense")
    public ExpenseResponse approve(@PathVariable UUID id) {
        return expenseService.setStatus(id, ExpenseStatus.APPROVED);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('REVENUE_MANAGE')")
    @Operation(operationId = "rejectExpense", summary = "Reject an expense")
    public ExpenseResponse reject(@PathVariable UUID id) {
        return expenseService.setStatus(id, ExpenseStatus.REJECTED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REVENUE_MANAGE')")
    @Operation(operationId = "deleteExpense", summary = "Delete an expense")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

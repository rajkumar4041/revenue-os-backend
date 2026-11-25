package com.revenueos.expense;

import com.revenueos.audit.AuditService;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.common.web.PageResponse;
import com.revenueos.expense.dto.CreateExpenseRequest;
import com.revenueos.expense.dto.ExpenseResponse;
import com.revenueos.expense.dto.UpdateExpenseRequest;
import com.revenueos.tenant.TenantContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final AuditService auditService;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, AuditService auditService) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public PageResponse<ExpenseResponse> list(UUID projectId, ExpenseStatus status, Pageable pageable) {
        return PageResponse.of(
                expenseRepository.search(TenantContext.getOrganizationId(), projectId, status, pageable),
                expenseMapper::toResponse);
    }

    @Transactional
    public ExpenseResponse create(CreateExpenseRequest request) {
        Expense expense = new Expense();
        expense.setOrganizationId(TenantContext.getOrganizationId());
        expense.setRecordedByUserId(TenantContext.getUserId());
        expense.setProjectId(request.projectId());
        if (request.category() != null) {
            expense.setCategory(request.category());
        }
        expense.setAmount(request.amount());
        expense.setIncurredOn(request.incurredOn());
        expense.setDescription(request.description());
        expense.setStatus(ExpenseStatus.PENDING);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(UUID id, UpdateExpenseRequest request) {
        Expense expense = requireExpense(id);
        expense.setProjectId(request.projectId());
        if (request.category() != null) {
            expense.setCategory(request.category());
        }
        expense.setAmount(request.amount());
        expense.setIncurredOn(request.incurredOn());
        expense.setDescription(request.description());
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse setStatus(UUID id, ExpenseStatus status) {
        Expense expense = requireExpense(id);
        expense.setStatus(status);
        Expense saved = expenseRepository.save(expense);
        auditService.record("EXPENSE_" + status.name(), "Expense", saved.getId(),
                status.name().toLowerCase() + " expense of " + saved.getAmount());
        return expenseMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        expenseRepository.delete(requireExpense(id));
    }

    private Expense requireExpense(UUID id) {
        return expenseRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Expense", id));
    }
}

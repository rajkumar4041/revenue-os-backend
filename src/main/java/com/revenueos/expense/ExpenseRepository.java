package com.revenueos.expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    Optional<Expense> findByIdAndOrganizationId(UUID id, UUID organizationId);

    java.util.List<Expense> findByOrganizationIdAndStatus(UUID organizationId, ExpenseStatus status);

    @Query("""
            select e from Expense e
            where e.organizationId = :organizationId
              and (:projectId is null or e.projectId = :projectId)
              and (:status is null or e.status = :status)
            order by e.incurredOn desc
            """)
    Page<Expense> search(@Param("organizationId") UUID organizationId,
                        @Param("projectId") UUID projectId,
                        @Param("status") ExpenseStatus status,
                        Pageable pageable);
}

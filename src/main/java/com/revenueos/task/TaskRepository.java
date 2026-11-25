package com.revenueos.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    Optional<Task> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query("""
            select t from Task t
            where t.organizationId = :organizationId
              and (:projectId is null or t.projectId = :projectId)
              and (:status is null or t.status = :status)
            order by t.createdAt desc
            """)
    List<Task> findForOrganization(@Param("organizationId") UUID organizationId,
                                   @Param("projectId") UUID projectId,
                                   @Param("status") TaskStatus status);

    List<Task> findByOrganizationIdAndAssigneeUserIdOrderByDueDateAsc(UUID organizationId, UUID assigneeUserId);
}

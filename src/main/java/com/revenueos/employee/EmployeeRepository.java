package com.revenueos.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query("""
            select e from Employee e
            where e.organizationId = :organizationId
              and (:status is null or e.employmentStatus = :status)
              and (:departmentId is null or e.departmentId = :departmentId)
            """)
    Page<Employee> search(@Param("organizationId") UUID organizationId,
                         @Param("status") EmploymentStatus status,
                         @Param("departmentId") UUID departmentId,
                         Pageable pageable);
}

package com.revenueos.timesheet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimesheetRepository extends JpaRepository<Timesheet, UUID> {

    Optional<Timesheet> findByIdAndOrganizationId(UUID id, UUID organizationId);

    List<Timesheet> findByOrganizationIdAndUserIdOrderByPeriodStartDesc(UUID organizationId, UUID userId);

    Optional<Timesheet> findByOrganizationIdAndUserIdAndPeriodStart(UUID organizationId, UUID userId, LocalDate periodStart);

    List<Timesheet> findByOrganizationIdAndStatusOrderBySubmittedAtDesc(UUID organizationId, TimesheetStatus status);
}

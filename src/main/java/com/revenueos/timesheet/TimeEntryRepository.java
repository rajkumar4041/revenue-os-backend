package com.revenueos.timesheet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, UUID> {

    List<TimeEntry> findByTimesheetIdOrderByWorkDateAsc(UUID timesheetId);

    Optional<TimeEntry> findByIdAndOrganizationId(UUID id, UUID organizationId);

    /** The single running timer for a user, found by joining through the timesheet. */
    @Query("""
            select te from TimeEntry te, Timesheet t
            where te.timesheetId = t.id
              and t.userId = :userId
              and te.organizationId = :organizationId
              and te.running = true
            """)
    Optional<TimeEntry> findRunningForUser(@Param("organizationId") UUID organizationId, @Param("userId") UUID userId);
}

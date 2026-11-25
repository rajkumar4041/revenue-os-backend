package com.revenueos.timesheet;

import com.revenueos.common.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "time_entries")
@Getter
@Setter
public class TimeEntry extends TenantAwareEntity {

    @Column(name = "timesheet_id", nullable = false)
    private UUID timesheetId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "task_id")
    private UUID taskId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal hours = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean billable = true;

    @Column(columnDefinition = "text")
    private String description;

    /** When a live timer is running this is set and {@code running} is true. */
    @Column(name = "started_at")
    private Instant startedAt;

    @Column(nullable = false)
    private boolean running = false;
}

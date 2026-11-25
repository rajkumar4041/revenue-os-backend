package com.revenueos.timesheet;

import com.revenueos.common.exception.ApiException;
import com.revenueos.timesheet.dto.StartTimerRequest;
import com.revenueos.timesheet.dto.TimeEntryResponse;
import com.revenueos.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

/**
 * The live work timer: one running entry per user at a time. Start opens an
 * entry on the current week's timesheet; stop computes the elapsed hours.
 */
@Service
public class TimerService {

    private final TimeEntryRepository timeEntryRepository;
    private final TimesheetService timesheetService;
    private final TimeEntryMapper timeEntryMapper;

    public TimerService(TimeEntryRepository timeEntryRepository, TimesheetService timesheetService,
                        TimeEntryMapper timeEntryMapper) {
        this.timeEntryRepository = timeEntryRepository;
        this.timesheetService = timesheetService;
        this.timeEntryMapper = timeEntryMapper;
    }

    @Transactional(readOnly = true)
    public TimeEntryResponse active() {
        return timeEntryRepository
                .findRunningForUser(TenantContext.getOrganizationId(), TenantContext.getUserId())
                .map(timeEntryMapper::toResponse)
                .orElse(null);
    }

    @Transactional
    public TimeEntryResponse start(StartTimerRequest request) {
        if (timeEntryRepository.findRunningForUser(TenantContext.getOrganizationId(), TenantContext.getUserId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "A timer is already running");
        }
        Timesheet timesheet = timesheetService.getOrCreateCurrent();
        TimeEntry entry = new TimeEntry();
        entry.setOrganizationId(timesheet.getOrganizationId());
        entry.setTimesheetId(timesheet.getId());
        entry.setProjectId(request.projectId());
        entry.setTaskId(request.taskId());
        entry.setDescription(request.description());
        entry.setWorkDate(LocalDate.now());
        entry.setHours(BigDecimal.ZERO);
        entry.setStartedAt(Instant.now());
        entry.setRunning(true);
        return timeEntryMapper.toResponse(timeEntryRepository.save(entry));
    }

    @Transactional
    public TimeEntryResponse stop() {
        TimeEntry entry = timeEntryRepository
                .findRunningForUser(TenantContext.getOrganizationId(), TenantContext.getUserId())
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "No timer is running"));
        long seconds = Duration.between(entry.getStartedAt(), Instant.now()).getSeconds();
        BigDecimal hours = BigDecimal.valueOf(seconds).divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
        entry.setHours(hours);
        entry.setRunning(false);
        return timeEntryMapper.toResponse(timeEntryRepository.save(entry));
    }
}

package com.revenueos.timesheet;

import com.revenueos.common.exception.ApiException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.timesheet.dto.AddTimeEntryRequest;
import com.revenueos.timesheet.dto.TimeEntryResponse;
import com.revenueos.timesheet.dto.TimesheetDetailResponse;
import com.revenueos.timesheet.dto.TimesheetResponse;
import com.revenueos.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
public class TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final TimeEntryMapper timeEntryMapper;

    public TimesheetService(TimesheetRepository timesheetRepository, TimeEntryRepository timeEntryRepository,
                            TimeEntryMapper timeEntryMapper) {
        this.timesheetRepository = timesheetRepository;
        this.timeEntryRepository = timeEntryRepository;
        this.timeEntryMapper = timeEntryMapper;
    }

    @Transactional(readOnly = true)
    public List<TimesheetResponse> myTimesheets() {
        UUID org = TenantContext.getOrganizationId();
        return timesheetRepository
                .findByOrganizationIdAndUserIdOrderByPeriodStartDesc(org, TenantContext.getUserId())
                .stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<TimesheetResponse> pendingApproval() {
        return timesheetRepository
                .findByOrganizationIdAndStatusOrderBySubmittedAtDesc(TenantContext.getOrganizationId(), TimesheetStatus.SUBMITTED)
                .stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public TimesheetDetailResponse getDetail(UUID id) {
        return toDetail(requireTimesheet(id));
    }

    /** Returns the current week's draft for the user, creating it if needed. */
    @Transactional
    public TimesheetDetailResponse currentWeek() {
        return toDetail(getOrCreateCurrent());
    }

    @Transactional
    public Timesheet getOrCreateCurrent() {
        UUID org = TenantContext.getOrganizationId();
        UUID userId = TenantContext.getUserId();
        LocalDate periodStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return timesheetRepository.findByOrganizationIdAndUserIdAndPeriodStart(org, userId, periodStart)
                .orElseGet(() -> {
                    Timesheet timesheet = new Timesheet();
                    timesheet.setOrganizationId(org);
                    timesheet.setUserId(userId);
                    timesheet.setPeriodStart(periodStart);
                    timesheet.setPeriodEnd(periodStart.plusDays(6));
                    timesheet.setStatus(TimesheetStatus.DRAFT);
                    return timesheetRepository.save(timesheet);
                });
    }

    @Transactional
    public TimeEntryResponse addEntry(UUID timesheetId, AddTimeEntryRequest request) {
        Timesheet timesheet = requireOwnedTimesheet(timesheetId);
        requireEditable(timesheet);
        TimeEntry entry = new TimeEntry();
        entry.setOrganizationId(timesheet.getOrganizationId());
        entry.setTimesheetId(timesheet.getId());
        entry.setProjectId(request.projectId());
        entry.setTaskId(request.taskId());
        entry.setWorkDate(request.workDate());
        entry.setHours(request.hours());
        entry.setBillable(request.billable() == null || request.billable());
        entry.setDescription(request.description());
        return timeEntryMapper.toResponse(timeEntryRepository.save(entry));
    }

    @Transactional
    public void deleteEntry(UUID entryId) {
        TimeEntry entry = timeEntryRepository.findByIdAndOrganizationId(entryId, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Time entry", entryId));
        requireEditable(requireOwnedTimesheet(entry.getTimesheetId()));
        timeEntryRepository.delete(entry);
    }

    @Transactional
    public TimesheetDetailResponse submit(UUID id) {
        Timesheet timesheet = requireOwnedTimesheet(id);
        requireEditable(timesheet);
        timesheet.setStatus(TimesheetStatus.SUBMITTED);
        timesheet.setSubmittedAt(Instant.now());
        timesheet.setReviewNote(null);
        return toDetail(timesheetRepository.save(timesheet));
    }

    @Transactional
    public TimesheetDetailResponse approve(UUID id) {
        Timesheet timesheet = requireTimesheet(id);
        requireStatus(timesheet, TimesheetStatus.SUBMITTED);
        timesheet.setStatus(TimesheetStatus.APPROVED);
        timesheet.setApprovedByUserId(TenantContext.getUserId());
        timesheet.setApprovedAt(Instant.now());
        return toDetail(timesheetRepository.save(timesheet));
    }

    @Transactional
    public TimesheetDetailResponse reject(UUID id, String note) {
        Timesheet timesheet = requireTimesheet(id);
        requireStatus(timesheet, TimesheetStatus.SUBMITTED);
        timesheet.setStatus(TimesheetStatus.REJECTED);
        timesheet.setReviewNote(note);
        return toDetail(timesheetRepository.save(timesheet));
    }

    // --- helpers ---

    private Timesheet requireTimesheet(UUID id) {
        return timesheetRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Timesheet", id));
    }

    private Timesheet requireOwnedTimesheet(UUID id) {
        Timesheet timesheet = requireTimesheet(id);
        if (!timesheet.getUserId().equals(TenantContext.getUserId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "This timesheet belongs to someone else");
        }
        return timesheet;
    }

    private void requireEditable(Timesheet timesheet) {
        if (timesheet.getStatus() != TimesheetStatus.DRAFT && timesheet.getStatus() != TimesheetStatus.REJECTED) {
            throw new ApiException(HttpStatus.CONFLICT, "Only draft or rejected timesheets can be edited");
        }
    }

    private void requireStatus(Timesheet timesheet, TimesheetStatus expected) {
        if (timesheet.getStatus() != expected) {
            throw new ApiException(HttpStatus.CONFLICT, "Timesheet is not " + expected);
        }
    }

    private TimesheetResponse toSummary(Timesheet timesheet) {
        List<TimeEntry> entries = timeEntryRepository.findByTimesheetIdOrderByWorkDateAsc(timesheet.getId());
        return toResponse(timesheet, entries);
    }

    private TimesheetDetailResponse toDetail(Timesheet timesheet) {
        List<TimeEntry> entries = timeEntryRepository.findByTimesheetIdOrderByWorkDateAsc(timesheet.getId());
        return new TimesheetDetailResponse(toResponse(timesheet, entries), timeEntryMapper.toResponseList(entries));
    }

    private TimesheetResponse toResponse(Timesheet timesheet, List<TimeEntry> entries) {
        BigDecimal total = entries.stream().map(TimeEntry::getHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal billable = entries.stream().filter(TimeEntry::isBillable)
                .map(TimeEntry::getHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TimesheetResponse(
                timesheet.getId(), timesheet.getUserId(), timesheet.getPeriodStart(), timesheet.getPeriodEnd(),
                timesheet.getStatus(), total, billable, timesheet.getSubmittedAt(), timesheet.getApprovedAt(),
                timesheet.getReviewNote());
    }
}

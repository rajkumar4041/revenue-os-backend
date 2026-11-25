package com.revenueos.timesheet;

import com.revenueos.timesheet.dto.AddTimeEntryRequest;
import com.revenueos.timesheet.dto.ReviewRequest;
import com.revenueos.timesheet.dto.TimeEntryResponse;
import com.revenueos.timesheet.dto.TimesheetDetailResponse;
import com.revenueos.timesheet.dto.TimesheetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/timesheets")
@Tag(name = "Timesheets")
public class TimesheetController {

    private final TimesheetService timesheetService;

    public TimesheetController(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('TIMESHEET_SUBMIT')")
    @Operation(operationId = "myTimesheets", summary = "List the current user's timesheets")
    public List<TimesheetResponse> my() {
        return timesheetService.myTimesheets();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('TIMESHEET_APPROVE')")
    @Operation(operationId = "pendingTimesheets", summary = "List timesheets awaiting approval")
    public List<TimesheetResponse> pending() {
        return timesheetService.pendingApproval();
    }

    @PostMapping("/current")
    @PreAuthorize("hasAuthority('TIMESHEET_SUBMIT')")
    @Operation(operationId = "openCurrentTimesheet", summary = "Open (creating if needed) the current week's timesheet")
    public TimesheetDetailResponse current() {
        return timesheetService.currentWeek();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TIMESHEET_SUBMIT','TIMESHEET_APPROVE')")
    @Operation(operationId = "getTimesheet", summary = "Get a timesheet with its entries")
    public TimesheetDetailResponse get(@PathVariable UUID id) {
        return timesheetService.getDetail(id);
    }

    @PostMapping("/{id}/entries")
    @PreAuthorize("hasAuthority('TIMESHEET_SUBMIT')")
    @Operation(operationId = "addTimeEntry", summary = "Add a time entry to a timesheet")
    public ResponseEntity<TimeEntryResponse> addEntry(@PathVariable UUID id, @Valid @RequestBody AddTimeEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timesheetService.addEntry(id, request));
    }

    @DeleteMapping("/entries/{entryId}")
    @PreAuthorize("hasAuthority('TIMESHEET_SUBMIT')")
    @Operation(operationId = "deleteTimeEntry", summary = "Delete a time entry")
    public ResponseEntity<Void> deleteEntry(@PathVariable UUID entryId) {
        timesheetService.deleteEntry(entryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('TIMESHEET_SUBMIT')")
    @Operation(operationId = "submitTimesheet", summary = "Submit a timesheet for approval")
    public TimesheetDetailResponse submit(@PathVariable UUID id) {
        return timesheetService.submit(id);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('TIMESHEET_APPROVE')")
    @Operation(operationId = "approveTimesheet", summary = "Approve a submitted timesheet")
    public TimesheetDetailResponse approve(@PathVariable UUID id) {
        return timesheetService.approve(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('TIMESHEET_APPROVE')")
    @Operation(operationId = "rejectTimesheet", summary = "Reject a submitted timesheet")
    public TimesheetDetailResponse reject(@PathVariable UUID id, @RequestBody ReviewRequest request) {
        return timesheetService.reject(id, request.note());
    }
}

package com.revenueos.timesheet.dto;

import java.util.List;

public record TimesheetDetailResponse(
        TimesheetResponse timesheet,
        List<TimeEntryResponse> entries
) {}

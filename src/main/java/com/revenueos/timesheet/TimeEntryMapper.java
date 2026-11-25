package com.revenueos.timesheet;

import com.revenueos.timesheet.dto.TimeEntryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TimeEntryMapper {

    TimeEntryResponse toResponse(TimeEntry entry);

    List<TimeEntryResponse> toResponseList(List<TimeEntry> entries);
}

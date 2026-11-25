package com.revenueos.timesheet;

import com.revenueos.timesheet.dto.StartTimerRequest;
import com.revenueos.timesheet.dto.TimeEntryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/timer")
@Tag(name = "Timer")
@PreAuthorize("hasAuthority('TIMESHEET_SUBMIT')")
public class TimerController {

    private final TimerService timerService;

    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    @GetMapping("/active")
    @Operation(operationId = "activeTimer", summary = "Get the running timer, if any")
    public TimeEntryResponse active() {
        return timerService.active();
    }

    @PostMapping("/start")
    @Operation(operationId = "startTimer", summary = "Start a work timer")
    public TimeEntryResponse start(@Valid @RequestBody StartTimerRequest request) {
        return timerService.start(request);
    }

    @PostMapping("/stop")
    @Operation(operationId = "stopTimer", summary = "Stop the running timer and record the elapsed hours")
    public TimeEntryResponse stop() {
        return timerService.stop();
    }
}

package com.revenueos.task;

import com.revenueos.task.dto.CreateTaskRequest;
import com.revenueos.task.dto.TaskResponse;
import com.revenueos.task.dto.UpdateTaskRequest;
import com.revenueos.task.dto.UpdateTaskStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TASK_VIEW')")
    @Operation(operationId = "listTasks", summary = "List tasks, optionally filtered by project and status")
    public List<TaskResponse> list(@RequestParam(required = false) UUID projectId,
                                   @RequestParam(required = false) TaskStatus status) {
        return taskService.list(projectId, status);
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasAuthority('TASK_VIEW')")
    @Operation(operationId = "listMyTasks", summary = "List tasks assigned to the current user")
    public List<TaskResponse> myTasks() {
        return taskService.myTasks();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TASK_VIEW')")
    @Operation(operationId = "getTask", summary = "Get a task by id")
    public TaskResponse get(@PathVariable UUID id) {
        return taskService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TASK_CREATE')")
    @Operation(operationId = "createTask", summary = "Create a task")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TASK_UPDATE')")
    @Operation(operationId = "updateTask", summary = "Update a task")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('TASK_UPDATE')")
    @Operation(operationId = "updateTaskStatus", summary = "Update only a task's status")
    public TaskResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateStatus(id, request.status());
    }
}

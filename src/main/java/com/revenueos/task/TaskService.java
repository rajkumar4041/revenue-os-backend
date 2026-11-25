package com.revenueos.task;

import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.task.dto.CreateTaskRequest;
import com.revenueos.task.dto.TaskResponse;
import com.revenueos.task.dto.UpdateTaskRequest;
import com.revenueos.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> list(UUID projectId, TaskStatus status) {
        return taskRepository.findForOrganization(TenantContext.getOrganizationId(), projectId, status)
                .stream().map(taskMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> myTasks() {
        return taskRepository
                .findByOrganizationIdAndAssigneeUserIdOrderByDueDateAsc(
                        TenantContext.getOrganizationId(), TenantContext.getUserId())
                .stream().map(taskMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse get(UUID id) {
        return taskMapper.toResponse(requireTask(id));
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        Task task = new Task();
        task.setOrganizationId(TenantContext.getOrganizationId());
        task.setCreatedByUserId(TenantContext.getUserId());
        task.setProjectId(request.projectId());
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setAssigneeUserId(request.assigneeUserId());
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        task.setStartDate(request.startDate());
        task.setDueDate(request.dueDate());
        task.setEstimatedHours(request.estimatedHours());
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(UUID id, UpdateTaskRequest request) {
        Task task = requireTask(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setAssigneeUserId(request.assigneeUserId());
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        task.setStartDate(request.startDate());
        task.setDueDate(request.dueDate());
        task.setEstimatedHours(request.estimatedHours());
        task.setActualHours(request.actualHours());
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateStatus(UUID id, TaskStatus status) {
        Task task = requireTask(id);
        task.setStatus(status);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    private Task requireTask(UUID id) {
        return taskRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Task", id));
    }
}

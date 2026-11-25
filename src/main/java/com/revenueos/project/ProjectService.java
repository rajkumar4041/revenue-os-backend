package com.revenueos.project;

import com.revenueos.common.exception.ConflictException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.common.web.PageResponse;
import com.revenueos.project.dto.CreateProjectRequest;
import com.revenueos.project.dto.ProjectResponse;
import com.revenueos.project.dto.UpdateProjectRequest;
import com.revenueos.tenant.TenantContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> list(Pageable pageable) {
        return PageResponse.of(
                projectRepository.findByOrganizationId(TenantContext.getOrganizationId(), pageable),
                projectMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(UUID id) {
        return projectMapper.toResponse(requireProject(id));
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        if (projectRepository.existsByOrganizationIdAndCode(organizationId, request.code())) {
            throw new ConflictException("A project with code '" + request.code() + "' already exists");
        }
        Project project = new Project();
        project.setOrganizationId(organizationId);
        project.setCode(request.code());
        apply(project, request.name(), request.clientId(), request.description(), request.managerUserId(),
                request.startDate(), request.endDate(), request.estimatedBudget(), request.contractValue(),
                request.billingType(), request.hourlyRate(), request.status(), request.priority(),
                request.memberUserIds());
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(UUID id, UpdateProjectRequest request) {
        Project project = requireProject(id);
        apply(project, request.name(), request.clientId(), request.description(), request.managerUserId(),
                request.startDate(), request.endDate(), request.estimatedBudget(), request.contractValue(),
                request.billingType(), request.hourlyRate(), request.status(), request.priority(),
                request.memberUserIds());
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(UUID id) {
        projectRepository.delete(requireProject(id));
    }

    private Project requireProject(UUID id) {
        return projectRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Project", id));
    }

    @SuppressWarnings("java:S107") // deliberately plain field assignment, grouped for reuse
    private void apply(Project project, String name, UUID clientId, String description, UUID managerUserId,
                       java.time.LocalDate startDate, java.time.LocalDate endDate,
                       java.math.BigDecimal estimatedBudget, java.math.BigDecimal contractValue,
                       BillingType billingType, java.math.BigDecimal hourlyRate,
                       ProjectStatus status, ProjectPriority priority, java.util.Set<UUID> memberUserIds) {
        project.setName(name);
        project.setClientId(clientId);
        project.setDescription(description);
        project.setManagerUserId(managerUserId);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setEstimatedBudget(estimatedBudget);
        project.setContractValue(contractValue);
        if (billingType != null) {
            project.setBillingType(billingType);
        }
        project.setHourlyRate(hourlyRate);
        if (status != null) {
            project.setStatus(status);
        }
        if (priority != null) {
            project.setPriority(priority);
        }
        project.setMemberUserIds(memberUserIds != null ? new HashSet<>(memberUserIds) : new HashSet<>());
    }
}

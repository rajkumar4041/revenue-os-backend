package com.revenueos.department;

import com.revenueos.common.exception.ConflictException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.department.dto.CreateDepartmentRequest;
import com.revenueos.department.dto.DepartmentResponse;
import com.revenueos.department.dto.UpdateDepartmentRequest;
import com.revenueos.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> list() {
        return departmentRepository.findByOrganizationIdOrderByName(TenantContext.getOrganizationId())
                .stream().map(departmentMapper::toResponse).toList();
    }

    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        if (departmentRepository.existsByOrganizationIdAndName(organizationId, request.name())) {
            throw new ConflictException("A department named '" + request.name() + "' already exists");
        }
        Department department = new Department();
        department.setOrganizationId(organizationId);
        department.setName(request.name());
        department.setManagerUserId(request.managerUserId());
        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentResponse update(UUID id, UpdateDepartmentRequest request) {
        Department department = requireDepartment(id);
        department.setName(request.name());
        department.setManagerUserId(request.managerUserId());
        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    @Transactional
    public void delete(UUID id) {
        departmentRepository.delete(requireDepartment(id));
    }

    private Department requireDepartment(UUID id) {
        return departmentRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Department", id));
    }
}

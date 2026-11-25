package com.revenueos.employee;

import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.common.web.PageResponse;
import com.revenueos.employee.dto.EmployeeResponse;
import com.revenueos.employee.dto.OffboardEmployeeRequest;
import com.revenueos.employee.dto.OnboardEmployeeRequest;
import com.revenueos.employee.dto.UpdateEmployeeRequest;
import com.revenueos.tenant.TenantContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> list(EmploymentStatus status, UUID departmentId, Pageable pageable) {
        return PageResponse.of(
                employeeRepository.search(TenantContext.getOrganizationId(), status, departmentId, pageable),
                employeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(UUID id) {
        return employeeMapper.toResponse(requireEmployee(id));
    }

    /** Onboarding: a new hire starts in ONBOARDING until their start date. */
    @Transactional
    public EmployeeResponse onboard(OnboardEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setOrganizationId(TenantContext.getOrganizationId());
        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setUserId(request.userId());
        employee.setDepartmentId(request.departmentId());
        employee.setDesignation(request.designation());
        if (request.employmentType() != null) {
            employee.setEmploymentType(request.employmentType());
        }
        employee.setEmploymentStatus(EmploymentStatus.ONBOARDING);
        employee.setCostRate(request.costRate());
        employee.setBillableRate(request.billableRate());
        employee.setHireDate(request.hireDate());
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(UUID id, UpdateEmployeeRequest request) {
        Employee employee = requireEmployee(id);
        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setDepartmentId(request.departmentId());
        employee.setDesignation(request.designation());
        if (request.employmentType() != null) {
            employee.setEmploymentType(request.employmentType());
        }
        if (request.employmentStatus() != null) {
            employee.setEmploymentStatus(request.employmentStatus());
        }
        employee.setCostRate(request.costRate());
        employee.setBillableRate(request.billableRate());
        employee.setHireDate(request.hireDate());
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    /** Offboarding: marks the person OFFBOARDED and stamps their last day. */
    @Transactional
    public EmployeeResponse offboard(UUID id, OffboardEmployeeRequest request) {
        Employee employee = requireEmployee(id);
        employee.setEmploymentStatus(EmploymentStatus.OFFBOARDED);
        employee.setEndDate(request.endDate() != null ? request.endDate() : LocalDate.now());
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    private Employee requireEmployee(UUID id) {
        return employeeRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Employee", id));
    }
}

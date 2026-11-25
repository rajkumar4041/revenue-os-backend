package com.revenueos.employee;

import com.revenueos.common.web.PageResponse;
import com.revenueos.employee.dto.EmployeeResponse;
import com.revenueos.employee.dto.OffboardEmployeeRequest;
import com.revenueos.employee.dto.OnboardEmployeeRequest;
import com.revenueos.employee.dto.UpdateEmployeeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @Operation(operationId = "listEmployees", summary = "List employees, optionally filtered by status and team")
    public PageResponse<EmployeeResponse> list(
            @RequestParam(required = false) EmploymentStatus status,
            @RequestParam(required = false) UUID teamId,
            @ParameterObject @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return employeeService.list(status, teamId, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @Operation(operationId = "getEmployee", summary = "Get an employee by id")
    public EmployeeResponse get(@PathVariable UUID id) {
        return employeeService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_MANAGE')")
    @Operation(operationId = "onboardEmployee", summary = "Onboard a new employee")
    public ResponseEntity<EmployeeResponse> onboard(@Valid @RequestBody OnboardEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.onboard(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_MANAGE')")
    @Operation(operationId = "updateEmployee", summary = "Update an employee")
    public EmployeeResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @PatchMapping("/{id}/offboard")
    @PreAuthorize("hasAuthority('EMPLOYEE_MANAGE')")
    @Operation(operationId = "offboardEmployee", summary = "Offboard an employee")
    public EmployeeResponse offboard(@PathVariable UUID id, @RequestBody OffboardEmployeeRequest request) {
        return employeeService.offboard(id, request);
    }
}

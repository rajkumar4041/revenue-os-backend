package com.revenueos.department;

import com.revenueos.department.dto.DepartmentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentResponse toResponse(Department department);
}

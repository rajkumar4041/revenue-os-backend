package com.revenueos.permission;

import com.revenueos.permission.dto.PermissionResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);

    Set<PermissionResponse> toResponseSet(Set<Permission> permissions);

    List<PermissionResponse> toResponseList(List<Permission> permissions);
}

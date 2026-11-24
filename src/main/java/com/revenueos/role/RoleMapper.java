package com.revenueos.role;

import com.revenueos.permission.PermissionMapper;
import com.revenueos.role.dto.RoleResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {

    RoleResponse toResponse(Role role);

    Set<RoleResponse> toResponseSet(Set<Role> roles);

    List<RoleResponse> toResponseList(List<Role> roles);
}

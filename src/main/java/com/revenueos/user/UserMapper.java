package com.revenueos.user;

import com.revenueos.role.RoleMapper;
import com.revenueos.user.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    UserResponse toResponse(User user);
}

package com.revenueos.team;

import com.revenueos.team.dto.TeamResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    TeamResponse toResponse(Team team);
}

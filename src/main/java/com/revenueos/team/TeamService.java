package com.revenueos.team;

import com.revenueos.common.exception.ConflictException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.team.dto.CreateTeamRequest;
import com.revenueos.team.dto.TeamResponse;
import com.revenueos.team.dto.UpdateTeamRequest;
import com.revenueos.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> list() {
        return teamRepository.findByOrganizationIdOrderByName(TenantContext.getOrganizationId())
                .stream().map(teamMapper::toResponse).toList();
    }

    @Transactional
    public TeamResponse create(CreateTeamRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        if (teamRepository.existsByOrganizationIdAndName(organizationId, request.name())) {
            throw new ConflictException("A team named '" + request.name() + "' already exists");
        }
        Team team = new Team();
        team.setOrganizationId(organizationId);
        team.setName(request.name());
        team.setManagerUserId(request.managerUserId());
        return teamMapper.toResponse(teamRepository.save(team));
    }

    @Transactional
    public TeamResponse update(UUID id, UpdateTeamRequest request) {
        Team team = requireTeam(id);
        team.setName(request.name());
        team.setManagerUserId(request.managerUserId());
        return teamMapper.toResponse(teamRepository.save(team));
    }

    @Transactional
    public void delete(UUID id) {
        teamRepository.delete(requireTeam(id));
    }

    private Team requireTeam(UUID id) {
        return teamRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Team", id));
    }
}

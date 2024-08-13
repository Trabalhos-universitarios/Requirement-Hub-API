package com.br.requirementhub.services;
import com.br.requirementhub.dtos.team.TeamResponseDTO;
import com.br.requirementhub.entity.Team;
import com.br.requirementhub.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public List<TeamResponseDTO> findByProjectId(Long id) {
        List<Team> teams = teamRepository.findByProjectId(id);
        return teams.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TeamResponseDTO convertToDTO(Team team) {
        TeamResponseDTO dto = new TeamResponseDTO();
        dto.setId(team.getId());
        dto.setUserName(team.getUserName());
        dto.setProjectId(team.getProjectId());
        dto.setUserId(team.getUserId());
        return dto;
    }
}
